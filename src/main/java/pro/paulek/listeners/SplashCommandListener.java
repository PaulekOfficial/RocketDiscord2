package pro.paulek.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;

import java.util.Objects;

public class SplashCommandListener extends ListenerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(SplashCommandListener.class);

    private final IRocketDiscord rocketDiscord;

    public SplashCommandListener(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!Objects.requireNonNull(event.getMember()).isOwner()) {
            return;
        }

        if (!event.getMessage().getContentDisplay().toLowerCase().contains("update splash commands")) {
            return;
        }
        logger.info(String.format("Updating splash commands on %s command used by %s", event.getGuild().getName(), event.getMember().getNickname()));

        rocketDiscord.getJda().retrieveCommands().complete().forEach(cmd -> {
            event.getGuild().deleteCommandById(cmd.getId()).queue();
        });
        event.getGuild().updateCommands().queue();

        rocketDiscord.getCommandManager().getCommandList().values().forEach(command -> {
            event.getGuild().upsertCommand(command.getCommandData()).queue();
            logger.debug("Added " + command.getName() + " command to guild " + event.getGuild().getName());
        });
        event.getGuild().updateCommands().queue();

        event.getMessage().reply(":wink:").queue();
    }
}
