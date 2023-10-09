package pro.paulek.listeners.fun;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class RandomFunctionsListeners extends ListenerAdapter {

    private final Random random = new Random();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        twoPlusTwo(event);
        placeArticle(event);
        sixAndNine(event);
        xdReaction(event);
    }

    private void sixAndNine(MessageReceivedEvent event) {
        if (!event.getMessage().getContentDisplay().toLowerCase().contains("69")) {
            return;
        }

        event.getChannel().sendMessage(":dizzy_face:").queue();
    }

    private void twoPlusTwo(MessageReceivedEvent event) {
        if (!event.getMessage().getContentDisplay().toLowerCase().contains("2+2")) {
            return;
        }

        event.getChannel().sendMessage(":llama: O nie! To jest najgorsze").queue();
    }

    private void placeArticle(MessageReceivedEvent event) {
        if (!event.getMessage().getContentDisplay().toLowerCase().contains("pip")) {
            return;
        }

        event.getChannel().sendMessage(":shopping_cart: Umieść artykuł :shopping_bags: w strefie pakowania :man_walking:").queue();
    }

    private void xdReaction(MessageReceivedEvent event) {
        if (!event.getMessage().getContentDisplay().toLowerCase().contains("xd")) {
            return;
        }
        if (random.nextInt(3) != 0) {
            return;
        }

        event.getMessage().reply("iks de").queue();
    }

}
