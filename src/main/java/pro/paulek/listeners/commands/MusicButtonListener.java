package pro.paulek.listeners.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pro.paulek.IRocketDiscord;
import pro.paulek.util.TimeUtils;

import java.awt.*;
import java.time.LocalDateTime;

public class MusicButtonListener extends ListenerAdapter {
    private final IRocketDiscord rocketDiscord;

    public MusicButtonListener(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = rocketDiscord;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getButton().getId() == null) {
            return;
        }

        if (!event.getButton().getId().startsWith("rocket-player-")) {
            return;
        }

        var manager = rocketDiscord.getMusicManager(event.getGuild().getId());
        if (manager.isEmpty()) {
            event.reply(":x: Bot nie jest podłączony do kanału głosowego").queue();
            return;
        }
        var musicPlayer = manager.get();

        switch (event.getButton().getId()) {
            case "rocket-player-pause":
                event.reply(":rocket: >>> ! Funkcja nie dostępna ! <<< :rocket:").queue();
                break;
            case "rocket-player-previous":
                event.reply(":rocket: >>> ! Funkcja nie dostępna ! <<< :rocket:").queue();
                break;
            case "rocket-player-next":
                musicPlayer.nextTrack();
                if (musicPlayer.getNowPlayingTrack().isEmpty()) {
                    event.reply(":x: Brak utworów w kolejce").queue();
                    return;
                }

                var currentTrack = musicPlayer.getNowPlayingTrack().get();

                var embed = new EmbedBuilder()
                        .setTitle("Następnie grane")
                        .setDescription(currentTrack.getInfo().title)
                        .setColor(Color.GREEN)
                        .addField("Kanał", event.getChannel().getName(), true)
                        .addField("Czas trwania", TimeUtils.millisecondsToMinutesFormat(currentTrack.getDuration()), true)
                        .addField("Przewidywany czas odtworzenia utworu", "Teraz", true)
                        .addField("Pozycja w kolejne", "Teraz", true)
                        .setAuthor(currentTrack.getInfo().author, currentTrack.getInfo().uri, "https://cdn.discordapp.com/attachments/885206963598819360/927269255337087026/butelka.png")
                        .setTimestamp(LocalDateTime.now())
                        .build();

                event.replyEmbeds(embed).queue();
            case "rocket-player-repeat":
                musicPlayer.setRepeat(!musicPlayer.isRepeat());
                event.reply(":repeat: Powtarzanie utworu: " + (musicPlayer.isRepeat() ? "wlaczone" : "wylaczone")).queue();
                break;
            case "rocket-player-stop":
                musicPlayer.removeAllTracks();
                musicPlayer.getPlayer().stopTrack();

                event.reply(":o: Zatrzymano odtwarzanie muzyki").queue();
                break;
        }
    }
}
