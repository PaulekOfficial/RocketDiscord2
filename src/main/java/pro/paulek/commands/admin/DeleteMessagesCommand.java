package pro.paulek.commands.admin;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.commands.Command;
import pro.paulek.commands.music.QueueCommand;

import java.util.Objects;

public class DeleteMessagesCommand extends Command {

    private final IRocketDiscord rocketDiscord;

    private final static Logger logger = LoggerFactory.getLogger(DeleteMessagesCommand.class);

    public DeleteMessagesCommand(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);

        this.setName("delete-messages");
        this.setDescription("usuwa n wiadomosci na kanale, od góry do dołu");
        this.setUsage("/delete-messages <amount-to-delete>");
        var commandData = Commands.slash("delete-messages", "Deletes n chat messages on specific channel");
        commandData.addOption(OptionType.INTEGER, "messages-to-delete", "Amount of messages to delete", true);
        this.setCommandData(commandData);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, TextChannel channel, Guild guild, Member member) {
        // Check member permissions
        if (!member.hasPermission(Permission.MANAGE_CHANNEL)) {
            event.reply(":cloud_lightning: Heh. Nie masz uprawnień do wykonania tego polecenia").queue();
            return;
        }

        // Get option value
        var messagesToDelete = Objects.requireNonNull(event.getOption("messages-to-delete")).getAsInt();
        if (messagesToDelete <= 0) {
            event.reply(String.format(":bug: Nie mogę usunąć %d wiadomosci", messagesToDelete)).queue();
            return;
        }

        // Get n messages from above
        var latestMessage = channel.getLatestMessageId();
        if (latestMessage.isEmpty()) {
            event.reply(":bug: Nie ma wczesniejszych wiadomosci do usunięcia.").queue();
            return;
        }

        event.reply(":saluting_face: Jasne już usuwam!").queue();

        // Remove chat history
        channel.getHistoryBefore(latestMessage, messagesToDelete - 1).submit().thenAccept(history -> {
            var messageList = history.getRetrievedHistory();
            messageList.forEach(message -> {
                logger.info(String.format("Deleting message %s on channel %s", message.getId(), message.getChannel().getId()));
                message.delete().queue();
            });
            logger.info(String.format("Deleting latest message %s on channel %s", latestMessage, channel.getId()));
            channel.deleteMessageById(latestMessage).queue();
            channel.sendMessage(String.format(":tools: Usunięto %d widomości", messageList.size() + 1)).queue();
        });
    }
}
