package pro.paulek.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.commands.Command;
import pro.paulek.objects.MusicManager;
import pro.paulek.util.PlaylistUtils;
import pro.paulek.util.TimeUtils;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Objects;

public class PlayCommand extends Command {

    private final static Logger logger = LoggerFactory.getLogger(PlayCommand.class);

    private final IRocketDiscord rocketDiscord;

    public PlayCommand(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);

        this.setName("play");
        this.setDescription("pozwala wlaczyc muzyke na kanale glosowym");
        this.setUsage("/play <link do muzyki>");
        var commandData = Commands.slash("play", "Plays music on voice channels");
        commandData.addOption(OptionType.STRING, "url", "URL to music source", true);
        this.setCommandData(commandData);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, TextChannel channel, Guild guild, Member member) {
       var musicPlayer = rocketDiscord.getMusicManager(guild.getId());

       if (musicPlayer == null) {
           musicPlayer = new MusicManager(rocketDiscord.getAudioManager().createPlayer(), guild);
           musicPlayer.init();
           rocketDiscord.getMusicManagers().add(guild.getId(), musicPlayer);
       }

        var musicManagerCopy = musicPlayer;
        MusicManager finalMusicPlayer = musicPlayer;
        rocketDiscord.getAudioManager().loadItemOrdered(musicPlayer, Objects.requireNonNull(event.getOption("url")).getAsString(), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                var audioChannel = Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).getChannel();
                if (!event.getMember().getVoiceState().inAudioChannel()) {
                    event.reply(":satellite: Muszisz być na jakimś kanale, abym mógł dołączyć do niego").queue();
                    return;
                }

                if (!guild.getAudioManager().isConnected()) {
                    guild.getAudioManager().openAudioConnection(audioChannel);
                    finalMusicPlayer.setAudioChannel(audioChannel);
                }

                var playedIn = "Teraz";
                if (TimeUtils.playlistTime(musicManagerCopy) >= 30000L) {
                    playedIn = TimeUtils.calculateTimeToPlayTrack(musicManagerCopy);
                }

                musicManagerCopy.queue(audioTrack);
                var embed = new EmbedBuilder()
                        .setDescription(audioTrack.getInfo().title)
                        .setThumbnail("https://img.youtube.com/vi/" + audioTrack.getIdentifier() + "/0.jpg")
                        .setColor(Color.GREEN)
                        .addField("Kanał", audioChannel.getName(), true)
                        .addField("Czas trwania", TimeUtils.millisecondsToMinutesFormat(audioTrack.getDuration()), true)
                        .addField("Przewidywany czas odtworzenia utworu", playedIn, true)
                        .addField("Pozycja w kolejne", String.valueOf(musicManagerCopy.getQueue().size()), true)
                        .setAuthor("Teraz gram", audioTrack.getInfo().uri, "https://cdn.pixabay.com/photo/2019/08/11/18/27/icon-4399630_1280.png")
                        .setTimestamp(LocalDateTime.now())
                        .build();
                event.replyEmbeds(embed).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                var audioChannel = Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).getChannel();
                if (!Objects.requireNonNull(event.getMember().getVoiceState()).inAudioChannel()) {
                    event.reply(":satellite: Muszisz być na jakimś kanale, abym mógł dołączyć do niego").queue();
                    return;
                }
                if (!guild.getAudioManager().isConnected()) {
                    guild.getAudioManager().openAudioConnection(audioChannel);
                }

                audioPlaylist.getTracks().forEach(musicManagerCopy::queue);

                event.replyEmbeds(PlaylistUtils.generatePlaylistEmbed(musicManagerCopy, ":arrow_lower_left: Dodaję do playlisty :arrow_lower_right:").build()).queue();
            }

            @Override
            public void noMatches() {
                event.reply(":dragon: Nie znalazłęm dopasowań do podanego linku").queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                event.reply(":screwdriver: Wystąpił nieznany błąd podczas wyszukiwania muzyki").queue();
            }
        });
    }
}
