package pro.paulek.data.cache;

import pro.paulek.IRocketDiscord;
import pro.paulek.data.ICache;
import pro.paulek.data.ISQLDataModel;
import pro.paulek.data.cache.mysql.DiscordMessageMySQLModel;
import pro.paulek.objects.guild.DiscordMessage;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;

public class DiscordMessageCache implements ICache<DiscordMessage, String> {
    private final IRocketDiscord rocketDiscord;
    private ISQLDataModel<DiscordMessage, String> mySQLModel;


    public DiscordMessageCache(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);
    }

    @Override
    public void init() {
        mySQLModel = new DiscordMessageMySQLModel(rocketDiscord);
        mySQLModel.createTable();
    }

    @Override
    public Optional<DiscordMessage> get(String id) {
        return mySQLModel.load(id);
    }

    @Override
    public boolean add(String id, DiscordMessage discordMessage) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Future<Boolean> deleteFromDatabase(String id) {
        return mySQLModel.delete(id);
    }

    @Override
    public boolean delete(String id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Future<Boolean> save(DiscordMessage message) {
        return mySQLModel.save(message);
    }
}
