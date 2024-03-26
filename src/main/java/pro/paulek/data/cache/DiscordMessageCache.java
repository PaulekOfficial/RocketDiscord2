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
//    private Map<String, DiscordMessage> messages;


    public DiscordMessageCache(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);
//        messages = new HashMap<>(10);
    }

    @Override
    public void init() {
        mySQLModel = new DiscordMessageMySQLModel(rocketDiscord);
        mySQLModel.createTable();
    }

    @Override
    public Optional<DiscordMessage> get(String id) {
//        if (messages.containsKey(id)) {
//            return messages.get(id);
//        }
        return mySQLModel.load(id);
//        messages.put(id, loadedFromDatabase);
    }

    @Override
    public boolean add(String id, DiscordMessage discordMessage) {
//        this.messages.put(id, discordMessage);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Future<Boolean> deleteFromDatabase(String id) {
        return mySQLModel.delete(id);
    }

    @Override
    public boolean delete(String id) {
//        this.messages.remove(id);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Future<Boolean> save(String id) {
//        var discordMessage = messages.get(id);
//        if (discordMessage != null) {
//            mySQLModel.save(discordMessage);
//        }
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
