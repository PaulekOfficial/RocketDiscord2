package pro.paulek.managers;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import pro.paulek.IRocketDiscord;

import java.util.Objects;

public class RocketPlayerManager extends DefaultAudioPlayerManager {

    private final IRocketDiscord rocketDiscord;

    public RocketPlayerManager(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);
    }

    public void initialize() {
        AudioSourceManagers.registerLocalSource(this);
//        DuncteBotSources.registerAll(this, "pl-PL");

        YoutubeAudioSourceManager ytSourceManager = new dev.lavalink.youtube.YoutubeAudioSourceManager();
//        ytSourceManager.useOauth2(null, false);
        ytSourceManager.setPlaylistPageCount(5);
        ytSourceManager.useOauth2(rocketDiscord.getConfiguration().getYoutubeOauth2(), rocketDiscord.getConfiguration().isInitializeOauth2ForYoutube());

        this.registerSourceManager(ytSourceManager);
    }
}
