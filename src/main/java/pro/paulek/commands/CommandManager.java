package pro.paulek.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import pro.paulek.RocketDiscord;

import java.util.Map;
import java.util.Objects;

public class CommandManager implements EventListener {

    private RocketDiscord rocketDiscord;
    private Map<String, Command> commandList;

    public CommandManager(RocketDiscord rocketDiscord) {
        this.rocketDiscord = rocketDiscord;
    }

    //TODO log command usage and if it is disallowed
    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (!(genericEvent instanceof MessageReceivedEvent event)) {
            return;
        }
        var message = event.getMessage().getContentRaw();
        if (!message.startsWith("/")) {
            return;
        }
        if (event.getAuthor().isBot()) {
            return;
        }
        var args = message.replaceFirst("/", "").split(" ");
        if (!(args.length > 0)) {
            //TODO not a command
        }

        var command = commandList.get(args[0]);
        if (command == null) {
            //TODO command not found
            return;
        }

        var guild = event.getGuild();
        var author = event.getAuthor();
        if (command.isSupremeCommand() && !rocketDiscord.getConfiguration().getAdminIds().contains(author.getId())) {
            //TODO no permissions
            return;
        }
        if (command.isGuildOwnerOnly() && !guild.getOwnerId().equalsIgnoreCase(author.getId())) {
            //TODO no permissions
            return;
        }
        if (command.isRequirePermission() && guild.isMember(Objects.requireNonNull(event.getMember()).getUser()) && !event.getMember().hasPermission(command.getRequiredPermission())) {
            //TODO no permissions
            return;
        }

        if (command.isNSFW() && !((TextChannel) event.getChannel()).isNSFW()) {
            //TODO no NSFW erroe
            return;
        }

        if (command.isRequireArguments() && ((args.length - 1) > command.getMaxArguments() || (args.length - 1) < command.getMinArguments())) {
            //TODO view usage
            return;
        }

        command.execute(event, guild, event.getMember());
    }
}
