package pro.paulek.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import pro.paulek.IRocketDiscord;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommandManager implements EventListener {

    private final IRocketDiscord rocketDiscord;
    private Map<String, Command> commandList;

    public CommandManager(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = rocketDiscord;
        this.commandList = new HashMap<>();
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
        //Not a command
        if (!(args.length > 0)) {
            return;
        }

        var channel = (TextChannel) event.getChannel();
        var command = commandList.get(args[0]);
        if (command == null) {
            channel.sendMessage(":alien: Polecenie które wprowadziłeś/aś nie istnieje :(").queue();
            return;
        }

        var guild = event.getGuild();
        var author = event.getAuthor();
        if (command.isSupremeCommand() && !rocketDiscord.getConfiguration().getAdminIds().contains(author.getId())) {
            channel.sendMessage(":crown: Polecenie administracyjne bota, brak dostępu! :no_entry:").queue();
            return;
        }
        if (command.isGuildOwnerOnly() && !guild.getOwnerId().equalsIgnoreCase(author.getId())) {
            channel.sendMessage(":no_entry: Polecenie może być jedynie używane przez właściciela serwera!").queue();
            return;
        }
        if (command.isRequirePermission() && guild.isMember(Objects.requireNonNull(event.getMember()).getUser()) && !event.getMember().hasPermission(command.getRequiredPermission())) {
            channel.sendMessage(":no_entry: Nie posiadasz wystarczających uprawnień do użycia tego polecenia!").queue();
            return;
        }

        if (command.isNSFW() && !channel.isNSFW()) {
            channel.sendMessage(":person_facepalming: Te polecenie można jedynie wykonać na kanale NSFW!").queue();
            return;
        }

        if (command.isRequireArguments() && ((args.length - 1) > command.getMaxArguments() || (args.length - 1) < command.getMinArguments())) {
            channel.sendMessage(":grimacing: Niepoprawnie użycie polecenia, polecenie należy użyć w następujący sposób:").queue();
            channel.sendMessage(command.getUsage()).queue();
            return;
        }

        command.execute(event, channel, guild, event.getMember());
    }

    public void addCommand(Command command) {
        this.commandList.put(command.getName(), command);
    }

    public void deleteCommand(String commandName) {
        this.commandList.remove(commandName);
    }

    public Map<String, Command> getCommandList() {
        return commandList;
    }
}
