package pro.paulek.data;

import net.dv8tion.jda.api.entities.Member;
import org.slf4j.Logger;
import pro.paulek.IRocketDiscord;
import pro.paulek.data.api.Cache;

//TODO Zapisujemy stan membera, za każdą zmianą, wyciągamy z bazy najmłodszy rekord który będzie aktualnym ważnym rekodem,
// przy dołączeniu do serwera zapisujemy wszystkich
public class MemberCache implements Cache<Member, String> {

    @Override
    public void init(IRocketDiscord rocketDiscord, Logger logger) {

    }

    @Override
    public Member get(String s) {
        return null;
    }

    @Override
    public void add(String s, Member member) {

    }

    @Override
    public void delete(String s) {

    }

    @Override
    public void remove(String s) {

    }

    @Override
    public void save(String s, Member member) {

    }

    @Override
    public void save(String s) {

    }
}
