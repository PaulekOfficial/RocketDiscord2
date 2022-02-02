package pro.paulek.data;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.data.api.Cache;
import pro.paulek.objects.MusicManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MusicPlayerCache implements Cache<MusicManager, String> {

    private final static Logger logger = LoggerFactory.getLogger(MusicPlayerCache.class);

    private final IRocketDiscord rocketDiscord;
    private final Map<String, MusicManager> musicManagers;

    public MusicPlayerCache(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);
        this.musicManagers = new HashMap<>();
    }

    @Override
    public void init() {

    }

    @Override
    public MusicManager get(String s) {
        return musicManagers.get(s);
    }

    @Override
    public void add(String s, MusicManager musicManager) {
        this.musicManagers.put(s, musicManager);
    }

    @Override
    public void delete(String s) {
        this.musicManagers.remove(s);
    }

    @Override
    public void remove(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(String s, MusicManager musicManager) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(String s) {
        throw new UnsupportedOperationException();
    }
}
