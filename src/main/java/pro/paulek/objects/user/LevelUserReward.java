package pro.paulek.objects.user;

import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public class LevelUserReward {
    private int level;
    private int economyGive;
    private int economyTake;
    private List<Role> giveRoles;
    private List<Role> takeRoles;
}
