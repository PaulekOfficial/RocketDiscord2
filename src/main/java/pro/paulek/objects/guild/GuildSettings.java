package pro.paulek.objects.guild;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.List;

public class GuildSettings {
    private boolean enableLevels;
    private int maximumUserLevel;


    private BotCommandsBehaviourType commandsBehaviourType;
    private List<TextChannel> botCommandsBlacklist;
    private List<TextChannel> botCommandsWhitelist;

    private List<TextChannel> welcomeChannels;
    private List<TextChannel> farewellChannels;

    private List<VoiceChannel> autoVoiceChannels;

    private List<TextChannel> nsfwChannels;
    private List<TextChannel> mediaChannels;
    private List<TextChannel> modLogChannels;

    private List<Role> musicRoles;
    private List<Role> botAdminRoles;
    private List<Role> botModeratorRoles;

    public enum BotCommandsBehaviourType {
        WHITELIST,
        BLACKLIST
    }
}
