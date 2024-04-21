package pro.paulek.listeners.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;

import java.util.List;
import java.util.Objects;

public class SlashCommandListener extends ListenerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(SlashCommandListener.class);

    private final IRocketDiscord rocketDiscord;

    public SlashCommandListener(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!Objects.requireNonNull(event.getMember()).isOwner() && !event.getMember().getUser().getId().equals("419203785190014987")) {
            return;
        }

        if (event.getMessage().getContentDisplay().contains("rocket, update commands now!")) {
            this.updateCommands(event);
            return;
        }

        if (event.getMessage().getContentDisplay().contains("rocket, purge commands now!")) {
            this.purgeCommands(event);
            return;
        }
    }

    private void purgeCommands(MessageReceivedEvent event) {
        logger.info(String.format("Purge slash commands on %s command used by %s", event.getGuild().getName(), event.getMember().getNickname()));

        List<Command> commands = event.getGuild().retrieveCommands().complete();
        commands.forEach(command -> {
            event.getGuild().deleteCommandById(command.getId()).queue();
        });
        event.getGuild().updateCommands().queue();

        event.getMessage().reply(":fire: :fire: :fire: Jeb z laserka :fire: :fire: :fire:").queue();
    }

    private void updateCommands(MessageReceivedEvent event) {
        logger.info(String.format("Updating slash commands on %s command used by %s", event.getGuild().getName(), event.getMember().getNickname()));

        List<Command> commands = event.getGuild().retrieveCommands().complete();
        commands.forEach(command -> {
            event.getGuild().deleteCommandById(command.getId()).queue();
        });

        rocketDiscord.getCommandManager().getCommandList().forEach((key, command) -> {
            event.getGuild().upsertCommand(command.getCommandData()).queue();
        });

        event.getGuild().updateCommands().queue();

        event.getMessage().reply(":saluting_face: It will be done my lord.").queue();
    }
}
