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
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.commands.Command;
import pro.paulek.managers.MusicManager;
import pro.paulek.util.PlaylistUtils;
import pro.paulek.util.TimeUtils;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Objects;

public class PlayCommand extends Command {

    private final static Logger logger = LoggerFactory.getLogger(PlayCommand.class);
    private final static String SEARCH_PREFIX = "ytsearch:";

    private final IRocketDiscord rocketDiscord;

    public PlayCommand(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);

        this.setName("play");
        this.setDescription("pozwala wlaczyc muzyke na kanale glosowym");
        this.setUsage("/play <link do muzyki>");
        var commandData = Commands.slash("play", "Plays music on voice channels");
        commandData.addOption(OptionType.STRING, "url", "URL to music source", false);
        commandData.addOption(OptionType.STRING, "tags", "Youtube tags to music source", false);
        this.setCommandData(commandData);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, TextChannel channel, Guild guild, Member member) {
        var manager = rocketDiscord.getMusicManager(guild.getId());
        if (manager.isEmpty()) {
            logger.warn("Music manager is empty for guild {}", guild.getId());

            var musicPlayer = new MusicManager(rocketDiscord.getAudioManager().createPlayer(), guild);
            musicPlayer.init();

            rocketDiscord.getMusicManagers().add(guild.getId(), musicPlayer);
            manager = rocketDiscord.getMusicManager(guild.getId());
        }

        if (manager.isEmpty()) {
            event.reply(":screwdriver: Wystąpił nieznany błąd podczas inicjalizacji odtwarzacza").queue();
            return;
        }

        var musicPlayer = manager.get();
        var searchStr = "";
        if (event.getOption("tags") != null) {
            searchStr = SEARCH_PREFIX + event.getOption("tags").getAsString();
        }

        if (event.getOption("url") != null) {
            searchStr = event.getOption("url").getAsString();
        }

        if (searchStr.isBlank()) {
            event.reply(":satellite: Musisz podać link do utworu lub tagi, aby wyszukać muzykę").queue();
            return;
        }

        rocketDiscord.getAudioManager().loadItemOrdered(
                event.getGuild(),
                searchStr,
                new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack audioTrack) {
                        var audioChannel = Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).getChannel();
                        if (!event.getMember().getVoiceState().inAudioChannel()) {
                            event.reply(":satellite: Muszisz być na jakimś kanale, abym mógł dołączyć do niego").queue();
                            return;
                        }

                        if (!guild.getAudioManager().isConnected()) {
                            guild.getAudioManager().openAudioConnection(audioChannel);
                            musicPlayer.setAudioChannel(audioChannel);
                        }

                        var playedIn = "Teraz";
                        if (TimeUtils.playlistTime(musicPlayer) >= 30000L) {
                            playedIn = TimeUtils.calculateTimeToPlayTrack(musicPlayer);
                        }

                        musicPlayer.queue(audioTrack);
                        var embed = new EmbedBuilder()
                                .setDescription(audioTrack.getInfo().title)
                                .setThumbnail("https://img.youtube.com/vi/" + audioTrack.getIdentifier() + "/0.jpg")
                                .setColor(Color.GREEN)
                                .addField("Kanał", audioChannel.getName(), true)
                                .addField("Czas trwania", TimeUtils.millisecondsToMinutesFormat(audioTrack.getDuration()), true)
                                .addField("Przewidywany czas odtworzenia utworu", playedIn, true)
                                .addField("Pozycja w kolejne", String.valueOf(musicPlayer.getPlaylist().size()), true)
                                .setAuthor("Teraz gram", audioTrack.getInfo().uri, "https://cdn.pixabay.com/photo/2019/08/11/18/27/icon-4399630_1280.png")
                                .setTimestamp(LocalDateTime.now())
                                .build();

                        Button pauseButton = Button.primary("rocket-player-pause", "⏯");
                        Button previousButton = Button.secondary("rocket-player-previous", "⏪");
                        Button nextButton = Button.secondary("rocket-player-next", "⏩");
                        Button repeatButton = Button.secondary("rocket-player-repeat", "\uD83D\uDD01");
                        Button stopButton = Button.danger("rocket-player-stop", "⏹");

                        event.replyEmbeds(embed)
                                .addActionRow(pauseButton,
                                        previousButton,
                                        nextButton,
                                        repeatButton,
                                        stopButton)
                                .queue();
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

                        audioPlaylist.getTracks().forEach(musicPlayer::queue);

                        event.replyEmbeds(PlaylistUtils.generatePlaylistEmbed(musicPlayer, ":arrow_lower_left: Dodaję do playlisty :arrow_lower_right:").build()).queue();
                    }

                    @Override
                    public void noMatches() {
                        event.reply(":dragon: Nie znalazłęm dopasowań do podanego linku").queue();
                    }

                    @Override
                    public void loadFailed(FriendlyException e) {
                        logger.warn("Error while loading music", e);
                        event.reply(":screwdriver: Wystąpił nieznany błąd podczas wyszukiwania muzyki").queue();
                    }
                });
    }
}
