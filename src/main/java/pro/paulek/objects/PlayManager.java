package pro.paulek.objects;

import com.dunctebot.sourcemanagers.DuncteBotSources;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import pro.paulek.IRocketDiscord;

import java.util.Objects;

public class PlayManager extends DefaultAudioPlayerManager {

    private final IRocketDiscord rocketDiscord;

    public PlayManager(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);
    }

    public void initialize() {
        AudioSourceManagers.registerLocalSource(this);
        AudioSourceManagers.registerRemoteSources(this);
        DuncteBotSources.registerAll(this, "pl-PL");

        source(YoutubeAudioSourceManager.class).setPlaylistPageCount(20);
    }
}
