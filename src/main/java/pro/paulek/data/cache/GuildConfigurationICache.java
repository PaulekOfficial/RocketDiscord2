package pro.paulek.data.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.data.ICache;
import pro.paulek.data.ISQLDataModel;
import pro.paulek.data.cache.mysql.GuildConfigurationMySQLModel;
import pro.paulek.objects.GuildConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;

//TODO complete rework of guild configuration system
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
        try {
            mySQLModel.createTable().get();
        } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
            logger.error("Cannot create guild configuration table", e);
        }

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

        var configuration = new GuildConfiguration(s, "unknown", false, new java.util.ArrayList<>(), new java.util.ArrayList<>(), new java.util.ArrayList<>(), false, false, "Witaj {user} na serwerze!", "Żegnaj {user}!", "0", "0", new java.util.ArrayList<>(), "0");
        this.add(s, configuration);
        this.save(configuration);

        return Optional.of(configuration);
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
    public Future<Boolean> save(GuildConfiguration configuration) {
        return this.mySQLModel.save(configuration);
    }
}
