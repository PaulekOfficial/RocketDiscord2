package pro.paulek.data;

import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import pro.paulek.IRocketDiscord;
import pro.paulek.data.api.Cache;

import java.util.HashMap;
import java.util.Map;

//TODO zapisujemy każdą wiadomość, nawet zedytowaną - jeżeli wystopi więcej niż 1 rekord wiadomości to znaczy, że wiadomość była edytowana
//jeżeli usunięta wszystkim ustawiamy flagę deleted
public class MessageCache implements Cache<Message, String> {

    private Map<Message, Integer> messageCache = new HashMap<>();

    @Override
    public void init(IRocketDiscord rocketDiscord, Logger logger) {

    }

    @Override
    public Message get(String string) {
        return null;
    }

    @Override
    public void add(String string, Message message) {

    }

    @Override
    public void delete(String string) {

    }

    @Override
    public void remove(String string) {

    }

    @Override
    public void save(String string, Message message) {

    }

    @Override
    public void save(String string) {

    }
}
