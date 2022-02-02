package pro.paulek.commands.admin;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.commands.Command;
import pro.paulek.commands.music.PlayCommand;

import java.util.Objects;

public class DeleteMessagesCommand extends Command {

    private final static Logger logger = LoggerFactory.getLogger(PlayCommand.class);

    private final IRocketDiscord rocketDiscord;

    public DeleteMessagesCommand(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);

        this.setName("delete-messages");
        this.setDescription("usuwa n ostatnich wiadomości na kanale");
        this.setUsage("/delete-messages");
        var commandData = new CommandData("delete-messages", "Deletes n last channel messages");
        commandData.addOption(OptionType.INTEGER, "to-delete", "The amount of messages to be deleted", true);
        this.setCommandData(commandData);
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event, TextChannel channel, Guild guild, Member member) {
        if (!member.hasPermission(Permission.MANAGE_CHANNEL)) {
            event.reply(":game_die: Nie posiadasz wystarczających uprawnień, aby użyć tego polecenia!").queue();
            return;
        }

        var n = event.getOption("to-delete").getAsLong();
        if (n <= 0) {
            event.reply(":rotating_light: Liczba wiadomości do usunięcia musi być większa od 0!").queue();
            return;
        }
        var messages = channel.getHistory().retrievePast((int) n).complete();

        if (messages == null || messages.isEmpty()) {
            event.reply("Wystąpił nieznany błąd").queue();
            return;
        }

        for (Message message : messages) {
            message.delete().queue();
        }

        event.reply(":moyai: Wiadomości usunięto!").queue();
    }
}
