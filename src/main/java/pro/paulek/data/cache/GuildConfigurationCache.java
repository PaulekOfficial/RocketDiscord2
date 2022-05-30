package pro.paulek.data.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.data.GuildConfiguration;
import pro.paulek.data.api.Cache;
import pro.paulek.data.cache.mysql.GuildConfigurationMySQLModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GuildConfigurationCache implements Cache<GuildConfiguration, String> {

    private final static Logger logger = LoggerFactory.getLogger(GuildConfigurationCache.class);

    private final IRocketDiscord rocketDiscord;
    public Map<String, GuildConfiguration> guildConfigurationMap = new HashMap<>(10);

    private GuildConfigurationMySQLModel mySQLModel;

    public GuildConfigurationCache(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);
    }

    @Override
    public void init() {
        mySQLModel = new GuildConfigurationMySQLModel(rocketDiscord);
        mySQLModel.createTable();

        var settings = mySQLModel.load();
        settings.forEach(setting -> {
            guildConfigurationMap.put(setting.getGuildID(), setting);
        });
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
        this.mySQLModel.delete(s);
    }

    @Override
    public void remove(String s) {
        guildConfigurationMap.remove(s);
    }

    @Override
    public void save(String s, GuildConfiguration guildConfiguration) {
        guildConfigurationMap.put(s, guildConfiguration);
        this.mySQLModel.save(guildConfiguration);
    }

    @Override
    public void save(String s) {
        this.mySQLModel.save(guildConfigurationMap.get(s));
    }
}
