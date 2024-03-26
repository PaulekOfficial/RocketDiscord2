package pro.paulek.data.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.objects.GuildConfiguration;
import pro.paulek.data.ICache;
import pro.paulek.data.ISQLDataModel;
import pro.paulek.data.cache.mysql.GuildConfigurationMySQLModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;

public class GuildConfigurationICache implements ICache<GuildConfiguration, String> {

    private final static Logger logger = LoggerFactory.getLogger(GuildConfigurationICache.class);

    private final IRocketDiscord rocketDiscord;
    public Map<String, GuildConfiguration> guildConfigurationMap = new HashMap<>(10);

    private ISQLDataModel<GuildConfiguration, String> mySQLModel;

    public GuildConfigurationICache(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);
    }

    @Override
    public void init() {
        mySQLModel = new GuildConfigurationMySQLModel(rocketDiscord);
        mySQLModel.createTable();

        var settings = mySQLModel.load();
        if (settings.isEmpty()) {
            return;
        }

        settings.get().forEach(setting -> {
            guildConfigurationMap.put(setting.getGuildID(), setting);
        });
    }

    @Override
    public Optional<GuildConfiguration> get(String s) {
        if (guildConfigurationMap.containsKey(s)) {
            return Optional.of(guildConfigurationMap.get(s));
        }

        return Optional.empty();
    }

    @Override
    public boolean add(String s, GuildConfiguration guildConfiguration) {
        guildConfigurationMap.put(s, guildConfiguration);

        return guildConfigurationMap.containsKey(s);
    }

    @Override
    public Future<Boolean> deleteFromDatabase(String s) {
        return this.mySQLModel.delete(s);
    }

    @Override
    public boolean delete(String s) {
        guildConfigurationMap.remove(s);

        return !guildConfigurationMap.containsKey(s);
    }

    @Override
    public Future<Boolean> save(String s) {
        return this.mySQLModel.save(guildConfigurationMap.get(s));
    }
}
