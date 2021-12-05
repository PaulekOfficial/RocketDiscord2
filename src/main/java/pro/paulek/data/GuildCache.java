package pro.paulek.data;

import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import pro.paulek.IRocketDiscord;
import pro.paulek.data.api.Cache;

//TODO podobnie ja membery, zapisujemy każde zmiany - najmłodszy rekord to aktualny rekord
public class GuildCache implements Cache<Guild, String> {

    @Override
    public void init(IRocketDiscord rocketDiscord, Logger logger) {

    }

    @Override
    public Guild get(String s) {
        return null;
    }

    @Override
    public void add(String s, Guild guild) {

    }

    @Override
    public void delete(String s) {

    }

    @Override
    public void remove(String s) {

    }

    @Override
    public void save(String s, Guild guild) {

    }

    @Override
    public void save(String s) {

    }
}
