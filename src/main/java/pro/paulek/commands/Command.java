package pro.paulek.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Command extends ListenerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(Command.class);

    private String name;
    private String description;
    private String usage;
    private boolean supremeCommand;
    private boolean guildOwnerOnly;
    private boolean requirePermission;
    private Permission requiredPermission;
    private boolean requireArguments;
    private boolean subCommandGroup;
    private boolean subCommandName;
    private boolean NSFW;

    public abstract void execute(@NotNull SlashCommandEvent event, TextChannel channel, Guild guild, Member member);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public boolean isSupremeCommand() {
        return supremeCommand;
    }

    public void setSupremeCommand(boolean supremeCommand) {
        this.supremeCommand = supremeCommand;
    }

    public boolean isGuildOwnerOnly() {
        return guildOwnerOnly;
    }

    public void setGuildOwnerOnly(boolean guildOwnerOnly) {
        this.guildOwnerOnly = guildOwnerOnly;
    }

    public boolean isRequirePermission() {
        return requirePermission;
    }

    public void setRequirePermission(boolean requirePermission) {
        this.requirePermission = requirePermission;
    }

    public Permission getRequiredPermission() {
        return requiredPermission;
    }

    public void setRequiredPermission(Permission requiredPermission) {
        this.requiredPermission = requiredPermission;
    }

    public boolean isRequireArguments() {
        return requireArguments;
    }

    public void setRequireArguments(boolean requireArguments) {
        this.requireArguments = requireArguments;
    }

    public boolean isNSFW() {
        return NSFW;
    }

    public void setNSFW(boolean NSFW) {
        this.NSFW = NSFW;
    }

    public boolean isSubCommandGroup() {
        return subCommandGroup;
    }

    public void setSubCommandGroup(boolean subCommandGroup) {
        this.subCommandGroup = subCommandGroup;
    }

    public boolean isSubCommandName() {
        return subCommandName;
    }

    public void setSubCommandName(boolean subCommandName) {
        this.subCommandName = subCommandName;
    }
}
