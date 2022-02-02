package pro.paulek.data.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.RocketDiscord;
import pro.paulek.data.GuildConfiguration;
import pro.paulek.data.api.Cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GuildConfigurationCache implements Cache<GuildConfiguration, String> {

    private final static Logger logger = LoggerFactory.getLogger(GuildConfigurationCache.class);

    private final IRocketDiscord rocketDiscord;
    public Map<String, GuildConfiguration> guildConfigurationMap = new HashMap<>(10);

    public GuildConfigurationCache(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);
    }

    @Override
    public void init() {
        //TODO load from database
    }

    @Override
    public GuildConfiguration get(String s) {
        return guildConfigurationMap.get(s);
    }

    @Override
    public void add(String s, GuildConfiguration guildConfiguration) {
        guildConfigurationMap.put(s, guildConfiguration);
    }

    @Override
    public void delete(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(String s) {
        guildConfigurationMap.remove(s);
    }

    @Override
    public void save(String s, GuildConfiguration guildConfiguration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(String s) {
        throw new UnsupportedOperationException();
    }
}
