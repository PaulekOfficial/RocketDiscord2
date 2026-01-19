package pro.paulek.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.components.buttons.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.commands.Command;
import pro.paulek.managers.MusicManager;
import pro.paulek.util.PlaylistUtils;
import pro.paulek.util.TimeUtils;

import java.awt.*;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

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
        MusicManager musicPlayer = getOrCreateMusicManager(guild);

        String searchStr = getSearchString(event);
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
                        handleTrackLoaded(event, guild, musicPlayer, audioTrack);
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist audioPlaylist) {
                        handlePlaylistLoaded(event, guild, musicPlayer, audioPlaylist);
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

    private MusicManager getOrCreateMusicManager(Guild guild) {
        var manager = rocketDiscord.getMusicManager(guild.getId());
        if (manager.isPresent()) {
            return manager.get();
        }

        MusicManager musicPlayer = new MusicManager(rocketDiscord.getAudioManager().createPlayer(), guild);
        musicPlayer.init();
        rocketDiscord.getMusicManagers().add(guild.getId(), musicPlayer);
        return musicPlayer;
    }

    private String getSearchString(SlashCommandInteractionEvent event) {
        String searchStr = "";
        if (event.getOption("tags") != null) {
            searchStr = SEARCH_PREFIX + event.getOption("tags").getAsString();
        }

        if (event.getOption("url") != null) {
            searchStr = event.getOption("url").getAsString();
        }

        return searchStr;
    }

    private void handleTrackLoaded(SlashCommandInteractionEvent event, Guild guild, MusicManager musicPlayer, AudioTrack audioTrack) {
        AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
        if (!event.getMember().getVoiceState().inAudioChannel() && musicPlayer.getAudioChannel().isEmpty()) {
            event.reply(":satellite: Muszisz być na jakimś kanale, abym mógł dołączyć do niego").queue();
            return;
        }

        var optionalChannel = musicPlayer.getAudioChannel();
        if (optionalChannel.isPresent()) {
            audioChannel = optionalChannel.get();
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
        MessageEmbed embed = null;
        try {
            embed = createEmbed(audioTrack, (VoiceChannel) audioChannel, playedIn, musicPlayer);
            event.replyEmbeds(embed)
//                    .addComponents(createButtons())
                    .queue();

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void handlePlaylistLoaded(SlashCommandInteractionEvent event, Guild guild, MusicManager musicPlayer, AudioPlaylist audioPlaylist) {
        AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
        if (!event.getMember().getVoiceState().inAudioChannel() && musicPlayer.getAudioChannel().isEmpty()) {
            event.reply(":satellite: Muszisz być na jakimś kanale, abym mógł dołączyć do niego").queue();
            return;
        }

        var optionalChannel = musicPlayer.getAudioChannel();
        if (optionalChannel.isPresent()) {
            audioChannel = optionalChannel.get();
        }

        if (!guild.getAudioManager().isConnected()) {
            guild.getAudioManager().openAudioConnection(audioChannel);
        }

        audioPlaylist.getTracks().forEach(musicPlayer::queue);

        event.replyEmbeds(PlaylistUtils.generatePlaylistEmbed(musicPlayer, ":arrow_lower_left: Dodaję do playlisty :arrow_lower_right:").build()).queue();
    }

    private MessageEmbed createEmbed(AudioTrack audioTrack, VoiceChannel audioChannel, String playedIn, MusicManager musicPlayer) throws ExecutionException, InterruptedException {
        return new EmbedBuilder()
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
    }

    private List<Button> createButtons() {
        Button pauseButton = Button.primary("rocket-player-pause", "⏯");
        Button previousButton = Button.secondary("rocket-player-previous", "⏪");
        Button nextButton = Button.secondary("rocket-player-next", "⏩");
        Button repeatButton = Button.secondary("rocket-player-repeat", "\uD83D\uDD01");
        Button stopButton = Button.danger("rocket-player-stop", "⏹");

        return List.of(pauseButton, previousButton, nextButton, repeatButton, stopButton);
    }
}