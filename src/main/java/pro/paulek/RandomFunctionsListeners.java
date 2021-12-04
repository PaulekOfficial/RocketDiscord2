package pro.paulek;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class RandomFunctionsListeners extends ListenerAdapter {

    private final Random random = new Random();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getMessage().getContentDisplay().toLowerCase().contains("xd")) {
            return;
        }
        if (random.nextInt(3) != 0) {
            return;
        }

        event.getMessage().reply("iks de").queue();
    }
}
