package pro.paulek.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pro.paulek.IRocketDiscord;

import java.util.Objects;

public class SplashCommandListener extends ListenerAdapter {

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

        rocketDiscord.getCommandManager().getCommandList().values().forEach(command -> event.getGuild().upsertCommand(command.getCommandData()).queue());
        event.getGuild().updateCommands().queue();
    }
}
