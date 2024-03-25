package pro.paulek.data.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.data.ICache;
import pro.paulek.objects.MusicManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    public MusicManager get(String s) {
        return musicManagers.get(s);
    }

    @Override
    public void add(String s, MusicManager musicManager) {
        this.musicManagers.put(s, musicManager);
    }

    @Override
    public void deleteFromDatabase(String s) {
        this.musicManagers.remove(s);
    }

    @Override
    public void delete(String s) {
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
