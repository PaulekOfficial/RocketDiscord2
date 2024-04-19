package pro.paulek.listeners.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pro.paulek.IRocketDiscord;
import pro.paulek.managers.MusicManager;
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
        String buttonId = event.getButton().getId();
        if (buttonId == null || !buttonId.startsWith("rocket-player-")) {
            return;
        }

        var manager = rocketDiscord.getMusicManager(event.getGuild().getId());
        if (manager.isEmpty()) {
            event.reply(":x: Bot nie jest podłączony do kanału głosowego").queue();
            return;
        }
        var musicPlayer = manager.get();

        switch (buttonId) {
            case "rocket-player-pause":
            case "rocket-player-previous":
                event.reply(":rocket: >>> ! Funkcja nie dostępna ! <<< :rocket:").queue();
                break;
            case "rocket-player-next":
                handleNextTrack(event, musicPlayer);
                break;
            case "rocket-player-repeat":
                handleRepeatTrack(event, musicPlayer);
                break;
            case "rocket-player-stop":
                handleStopTrack(event, musicPlayer);
                break;
        }
    }

    private void handleNextTrack(ButtonInteractionEvent event, MusicManager musicPlayer) {
        var skipped = musicPlayer.skipTrack();

        try {
            if (!skipped.get()) {
                event.reply(":x: Nie można pominąć utworu").queue();
                return;
            }
        } catch (Exception e) {
            event.reply(":x: Nie można pominąć utworu").queue();
            return;
        }

        var audioTrack = musicPlayer.getNowPlayingTrack();
        if (audioTrack.isEmpty()) {
            event.reply(":x: Nie można pominąć utworu").queue();
            return;
        }

        var embed = createEmbed(event, audioTrack.get());
        event.replyEmbeds(embed).queue();
    }

    private void handleRepeatTrack(ButtonInteractionEvent event, MusicManager musicPlayer) {
//        musicPlayer.setRepeat(!musicPlayer.isRepeat());
//        event.reply(":repeat: Powtarzanie utworu: " + (musicPlayer.isRepeat() ? "wlaczone" : "wylaczone")).queue();
        event.reply(":rocket: >>> ! Funkcja nie dostępna ! <<< :rocket:").queue();
    }

    private void handleStopTrack(ButtonInteractionEvent event, MusicManager musicPlayer) {
        var stopped = musicPlayer.stopTrack();

        try {
            if (!stopped.get()) {
                event.reply(":x: Nie można zatrzymać odtwarzania").queue();
                return;
            }
        } catch (Exception e) {
            event.reply(":x: Nie można zatrzymać odtwarzania").queue();
            return;
        }
    }

    private MessageEmbed createEmbed(ButtonInteractionEvent event, AudioTrack currentTrack) {
        return new EmbedBuilder()
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
    }
}