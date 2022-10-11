package pro.paulek.commands.admin;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import pro.paulek.IRocketDiscord;
import pro.paulek.commands.Command;

import java.util.Objects;

public class DeleteMessagesCommand extends Command {

    private final IRocketDiscord rocketDiscord;

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
        if (!member.isOwner()) {
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

        // Remove chat history
        channel.getHistoryAfter(latestMessage, messagesToDelete - 1).submit().thenAccept(history -> {
            var messageList = history.getRetrievedHistory();
            channel.deleteMessages(messageList).queue();
            channel.deleteMessageById(latestMessage).queue();

            event.reply(String.format(":wink: Usunięto %d widomości", messageList.size() + 1)).queue();
        });
    }
}
