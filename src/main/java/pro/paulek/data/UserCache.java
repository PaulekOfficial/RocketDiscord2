package pro.paulek.data;

import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import pro.paulek.IRocketDiscord;
import pro.paulek.data.api.Cache;
import pro.paulek.objects.CachedUser;

public class UserCache implements Cache<CachedUser, String> {

    @Override
    public void init(IRocketDiscord rocketDiscord, Logger logger) {
        
    }

    @Override
    public CachedUser get(String s) {
        return null;
    }

    @Override
    public void add(String s, CachedUser user) {

    }

    @Override
    public void delete(String s) {

    }

    @Override
    public void remove(String s) {

    }

    @Override
    public void save(String s, CachedUser user) {

    }

    @Override
    public void save(String s) {

    }
}
