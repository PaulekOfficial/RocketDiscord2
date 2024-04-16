package pro.paulek.data.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.data.ICache;
import pro.paulek.managers.MusicManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;

public class MusicPlayerCache implements ICache<MusicManager, String> {

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
    public Optional<MusicManager> get(String s) {
        if (musicManagers.containsKey(s)) return Optional.of(musicManagers.get(s));

        return Optional.empty();
    }

    @Override
    public boolean add(String s, MusicManager musicManager) {
        this.musicManagers.put(s, musicManager);
        return false;
    }

    @Override
    public Future<Boolean> deleteFromDatabase(String s) {
        this.musicManagers.remove(s);
        return null;
    }

    @Override
    public boolean delete(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<Boolean> save(MusicManager musicManager) {
        throw new UnsupportedOperationException();
    }
}
