package pro.paulek.data;

import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import pro.paulek.IRocketDiscord;
import pro.paulek.data.api.Cache;

public class UserCache implements Cache<User, String> {

    @Override
    public void init(IRocketDiscord rocketDiscord, Logger logger) {
        
    }

    @Override
    public User get(String s) {
        return null;
    }

    @Override
    public void add(String s, User user) {

    }

    @Override
    public void delete(String s) {

    }

    @Override
    public void remove(String s) {

    }

    @Override
    public void save(String s, User user) {

    }

    @Override
    public void save(String s) {

    }
}
