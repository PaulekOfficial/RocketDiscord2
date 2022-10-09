package pro.paulek.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommandManager extends ListenerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(CommandManager.class);

    private final IRocketDiscord rocketDiscord;
    private final Map<String, Command> commandList;

    public CommandManager(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = rocketDiscord;
        this.commandList = new HashMap<>();
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        if (Objects.requireNonNull(event.getMember()).getUser().isBot()) {
            return;
        }

        var guildConfiguration = rocketDiscord.getGuildConfigurations().get(event.getGuild().getId());

        var channel = (TextChannel) event.getChannel();
        if (!event.getMember().hasPermission(Permission.MANAGE_CHANNEL) && (guildConfiguration.isCommandsChannelsWhitelistMode() && !guildConfiguration.getCommandChannels().contains(channel.getId())
        || !guildConfiguration.isCommandsChannelsWhitelistMode() && guildConfiguration.getCommandChannels().contains(channel.getId()))) {
            event.reply(":man_detective: Nie możesz używać poleceń na tym kanale!").queue();
            return;
        }
        var command = commandList.get(event.getName());
        if (command == null) {
            event.reply(":alien: Polecenie które wprowadziłeś/aś nie istnieje :(").queue();
            return;
        }

        var guild = event.getGuild();
        var author = event.getMember();
        if (command.isSupremeCommand() && !rocketDiscord.getConfiguration().getAdminIds().contains(author.getId())) {
            event.reply(":crown: Polecenie administracyjne bota, brak dostępu! :no_entry:").queue();
            return;
        }
        if (command.isGuildOwnerOnly() && !guild.getOwnerId().equalsIgnoreCase(author.getId())) {
            event.reply(":no_entry: Polecenie może być jedynie używane przez właściciela serwera!").queue();
            return;
        }
        if (command.isRequirePermission() && guild.isMember(Objects.requireNonNull(event.getMember()).getUser()) && !event.getMember().hasPermission(command.getRequiredPermission())) {
            event.reply(":no_entry: Nie posiadasz wystarczających uprawnień do użycia tego polecenia!").queue();
            return;
        }

        if (command.isNSFW() && !channel.isNSFW()) {
            event.reply(":person_facepalming: Te polecenie można jedynie wykonać na kanale NSFW!").queue();
            return;
        }

        if ((command.isSubCommandName() && event.getSubcommandName() == null) || (command.isSubCommandGroup() && event.getSubcommandGroup() == null)) {
            event.reply(":grimacing: Niepoprawnie użycie polecenia, wpisz /help " + event.getName() + " aby uzyskac pomoc.").queue();
            event.reply(command.getUsage()).queue();
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
