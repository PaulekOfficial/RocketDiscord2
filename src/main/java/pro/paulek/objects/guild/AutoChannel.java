package pro.paulek.objects.guild;

import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;

public class AutoChannel {
    private String id;
    private String name;
    private User owner;
    private boolean privateChannel;
    private int userLimit;
    private List<User> userWhitelist;
    private List<User> bannedUsers;

    public AutoChannel() {
    }

    public AutoChannel(String id, User owner, String name) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.privateChannel = false;
        this.userLimit = 5;
        this.userWhitelist = new ArrayList<>();
        this.bannedUsers = new ArrayList<>();
    }

    public AutoChannel(String id, String name, User owner, boolean privateChannel, int userLimit) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.privateChannel = privateChannel;
        this.userLimit = userLimit;
        this.userWhitelist = new ArrayList<>();
        this.bannedUsers = new ArrayList<>();
    }

    public AutoChannel(String id, String name, User owner, boolean privateChannel, int userLimit, List<User> userWhitelist, List<User> bannedUsers) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.privateChannel = privateChannel;
        this.userLimit = userLimit;
        this.userWhitelist = userWhitelist;
        this.bannedUsers = bannedUsers;
    }
}
