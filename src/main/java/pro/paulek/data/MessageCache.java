package pro.paulek.data;

import org.slf4j.Logger;
import pro.paulek.IRocketDiscord;
import pro.paulek.data.api.Cache;
import pro.paulek.objects.CachedMessage;

import java.util.HashMap;
import java.util.Map;

public class MessageCache implements Cache<CachedMessage, String> {

    private final Map<String, CachedMessage> messageCache = new HashMap<>();

    @Override
    public void init(IRocketDiscord rocketDiscord, Logger logger) {

    }

    @Override
    public CachedMessage get(String string) {
        return null;
    }

    @Override
    public void add(String string, CachedMessage message) {
        this.messageCache.put(string, message);
    }

    @Override
    public void delete(String string) {

    }

    @Override
    public void remove(String string) {
        this.messageCache.remove(string);
    }

    @Override
    public void save(String string, CachedMessage message) {

    }

    @Override
    public void save(String string) {

    }
}
