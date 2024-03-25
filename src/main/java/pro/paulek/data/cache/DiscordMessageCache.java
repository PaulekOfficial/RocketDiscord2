package pro.paulek.data.cache;

import pro.paulek.IRocketDiscord;
import pro.paulek.data.ICache;
import pro.paulek.data.ISQLDataModel;
import pro.paulek.data.cache.mysql.DiscordMessageMySQLModel;
import pro.paulek.objects.guild.DiscordMessage;

import java.util.Objects;

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
    public DiscordMessage get(String id) {
//        if (messages.containsKey(id)) {
//            return messages.get(id);
//        }

        var loadedFromDatabase = mySQLModel.load(id);
        if (loadedFromDatabase == null) {
            return null;
        }
//        messages.put(id, loadedFromDatabase);
        return loadedFromDatabase;
    }

    @Override
    public void add(String id, DiscordMessage discordMessage) {
//        this.messages.put(id, discordMessage);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteFromDatabase(String id) {
        mySQLModel.delete(id);
    }

    @Override
    public void delete(String id) {
//        this.messages.remove(id);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void save(String id, DiscordMessage discordMessage) {
        mySQLModel.save(discordMessage);
    }

    @Override
    public void save(String id) {
//        var discordMessage = messages.get(id);
//        if (discordMessage != null) {
//            mySQLModel.save(discordMessage);
//        }
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
