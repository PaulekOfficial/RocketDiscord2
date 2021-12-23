package pro.paulek.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SplashCommandListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getMessage().getContentDisplay().toLowerCase().contains("update splash commands")) {
            return;
        }

        event.getGuild().upsertCommand("help", "Shows RocketDiscord bot help").queue();
        event.getGuild().updateCommands().queue();
    }
}
