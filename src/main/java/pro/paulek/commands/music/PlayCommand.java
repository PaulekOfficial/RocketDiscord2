package pro.paulek.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.commands.Command;
import pro.paulek.objects.MusicManager;
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
        var commandData = new CommandData("play", "Plays music on voice channels");
        commandData.addOption(OptionType.STRING, "url", "URL to music source", true);
        this.setCommandData(commandData);
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event, TextChannel channel, Guild guild, Member member) {
       var musicPlayer = rocketDiscord.getMusicManager(guild.getId());

       if (musicPlayer == null) {
           musicPlayer = new MusicManager(rocketDiscord.getAudioManager().createPlayer(), guild);
           musicPlayer.init();
           rocketDiscord.getMusicManagers().add(guild.getId(), musicPlayer);
       }

        var musicManagerCopy = musicPlayer;
        rocketDiscord.getAudioManager().loadItemOrdered(musicPlayer, Objects.requireNonNull(event.getOption("url")).getAsString(), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                var audioChannel = Objects.requireNonNull(event.getMember()).getVoiceState().getChannel();
                if (!event.getMember().getVoiceState().inAudioChannel()) {
                    event.reply(":satellite: Muszisz by?? na jakim?? kanale, abym m??g?? do????czy?? do niego").queue();
                    return;
                }

                if (!guild.getAudioManager().isConnected()) {
                    guild.getAudioManager().openAudioConnection(audioChannel);
                }

                var playedIn = "Teraz";
                if (TimeUtils.playlistTime(musicManagerCopy) >= 30000L) {
                    playedIn = TimeUtils.calculateTimeToPlayTrack(musicManagerCopy);
                }

                if (musicManagerCopy.getQueue().size() <= 0) {
                    musicManagerCopy.queue(audioTrack);
                    var embed = new EmbedBuilder()
                            .setDescription(audioTrack.getInfo().title)
                            .setColor(Color.GREEN)
                            .addField("Kana??", audioChannel.getName(), true)
                            .addField("Czas trwania", TimeUtils.millisecondsToMinutesFormat(musicManagerCopy.getCurrentTrack().getDuration()), true)
                            .addField("Przewidywany czas odtworzenia utworu", playedIn, true)
                            .setAuthor("Teraz gram", audioTrack.getInfo().uri, "https://cdn.discordapp.com/attachments/885206963598819360/927269255337087026/butelka.png")
                            .setTimestamp(LocalDateTime.now())
                            .build();
                    event.replyEmbeds(embed).queue();
                    return;
                }

                musicManagerCopy.queue(audioTrack);
                var embed = new EmbedBuilder()
                        .setDescription(audioTrack.getInfo().title)
                        .setColor(Color.GREEN)
                        .addField("Kana??", audioChannel.getName(), true)
                        .addField("Czas trwania", TimeUtils.millisecondsToMinutesFormat(audioTrack.getDuration()), true)
                        .addField("Przewidywany czas odtworzenia utworu", playedIn, true)
                        .addField("Pozycja w kolejne", String.valueOf(musicManagerCopy.getQueue().size()), true)
                        .setAuthor("Dodano do playlisty", audioTrack.getInfo().uri, "https://cdn.discordapp.com/attachments/885206963598819360/927269255337087026/butelka.png")
                        .setTimestamp(LocalDateTime.now())
                        .build();
                event.replyEmbeds(embed).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                var audioChannel = Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).getChannel();
                if (!Objects.requireNonNull(event.getMember().getVoiceState()).inAudioChannel()) {
                    event.reply(":satellite: Muszisz by?? na jakim?? kanale, abym m??g?? do????czy?? do niego").queue();
                    return;
                }
                if (!guild.getAudioManager().isConnected()) {
                    guild.getAudioManager().openAudioConnection(audioChannel);
                }

                StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append(":arrow_lower_left: Dodaje do playlisty :arrow_lower_right:");
                stringBuilder.append(System.getProperty("line.separator"));

                int queueSize = musicManagerCopy.getQueue().size();
                int i = 1 + queueSize;

                for (AudioTrack audioTrack : audioPlaylist.getTracks()) {
                    if (i <= 10 + queueSize) {
                        stringBuilder.append("`");
                        stringBuilder.append(i);
                        stringBuilder.append(".` ");
                        stringBuilder.append(audioTrack.getInfo().title);
                        stringBuilder.append(" | ");
                        stringBuilder.append(TimeUtils.millisecondsToMinutesFormat(audioTrack.getDuration()));
                        stringBuilder.append(System.getProperty("line.separator"));
                        stringBuilder.append(System.getProperty("line.separator"));
                    }

                    musicManagerCopy.queue(audioTrack);
                    i++;
                }
                stringBuilder.append(System.getProperty("line.separator"));
                stringBuilder.append("Liczba utwor??w w playli??cie: ");
                stringBuilder.append(String.valueOf(musicManagerCopy.getQueue().size()));

                var embed = new EmbedBuilder()
                        .setDescription(stringBuilder.toString())
                        .setColor(Color.GREEN)
                        .setAuthor("Dodano do playlisty", "https://paulek.pro/", "https://cdn.discordapp.com/attachments/885206963598819360/927269255337087026/butelka.png")
                        .setTimestamp(LocalDateTime.now())
                        .build();
                event.replyEmbeds(embed).queue();
            }

            @Override
            public void noMatches() {
                event.reply(":dragon: Nie znalaz????m dopasowa?? do podanego linku").queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                event.reply(":screwdriver: Wyst??pi?? nieznany b????d podczas wyszukiwania muzyki").queue();
            }
        });
    }
}
