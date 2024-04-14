package pro.paulek.listeners.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import pro.paulek.IRocketDiscord;
import pro.paulek.objects.MusicManager;
import pro.paulek.util.TimeUtils;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Optional;

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

        switch (event.getButton().getId()) {
            case "rocket-player-pause":
                this.handlePauseButton(event.getGuild(), event.getMessageChannel(), event.getButton(), event.getMember());
                break;
            case "rocket-player-previous":
                this.handlePreviousButton(event.getGuild(), event.getMessageChannel(), event.getButton(), event.getMember());
                break;
            case "rocket-player-next":
                this.handleNextButton(event.getGuild(), event.getMessageChannel(), event.getButton(), event.getMember());
                break;
            case "rocket-player-repeat":
                this.handeRepeatButton(event.getGuild(), event.getMessageChannel(), event.getButton(), event.getMember());
                break;
            case "rocket-player-stop":
                this.handleStopButton(event.getGuild(), event.getMessageChannel(), event.getButton(), event.getMember());
                break;
        }
    }

    private Optional<MusicManager> getMusicManagerForUser(Guild guild, MessageChannel channel, Member member) {
        var manager = rocketDiscord.getMusicManager(guild.getId());
        if (manager.isEmpty()) {
            return Optional.empty();
        }

        var musicPlayer = manager.get();
        var memberAudioChannel = member.getVoiceState().getChannel();
        if (!member.getVoiceState().inAudioChannel() ||  !memberAudioChannel.getId().equals(musicPlayer.getPlayingChannel().getId())) {
            channel.sendMessage(":construction: Aby kontrolować bota, musisz byc na kanale z nim!").queue();
            return Optional.empty();
        }

        return Optional.of(musicPlayer);
    }

    private void handlePreviousButton(Guild guild, MessageChannel channel, Button button, Member member) {
        var manager = rocketDiscord.getMusicManager(guild.getId());
        if (manager.isEmpty()) {
            return;
        }
        var musicPlayer = manager.get();

        channel.sendMessage(">>> ! Funkcja nie dostępna ! <<<").queue();
    }

    private void handleNextButton(Guild guild, MessageChannel channel, Button button, Member member) {
        var manager = rocketDiscord.getMusicManager(guild.getId());
        if (manager.isEmpty()) {
            return;
        }
        var musicPlayer = manager.get();

        musicPlayer.nextTrack();
        if (musicPlayer.getCurrentTrack().isEmpty()) {
            channel.sendMessage(":x: Brak utworów w kolejce").queue();
            return;
        }

        var currentTrack = musicPlayer.getCurrentTrack().get();

        var embed = new EmbedBuilder()
                .setTitle("Następnie grane")
                .setDescription(currentTrack.getInfo().title)
                .setColor(Color.GREEN)
                .addField("Kanał", channel.getName(), true)
                .addField("Czas trwania", TimeUtils.millisecondsToMinutesFormat(currentTrack.getDuration()), true)
                .addField("Przewidywany czas odtworzenia utworu", "Teraz", true)
                .addField("Pozycja w kolejne", "Teraz", true)
                .setAuthor(currentTrack.getInfo().author, currentTrack.getInfo().uri, "https://cdn.discordapp.com/attachments/885206963598819360/927269255337087026/butelka.png")
                .setTimestamp(LocalDateTime.now())
                .build();

        channel.sendMessageEmbeds(embed).queue();
    }

    private void handlePauseButton(Guild guild, MessageChannel channel, Button button, Member member) {
        var manager = rocketDiscord.getMusicManager(guild.getId());
        if (manager.isEmpty()) {
            return;
        }
        var musicPlayer = manager.get();

        channel.sendMessage(">>> ! Funkcja nie dostępna ! <<<").queue();
    }

    private void handeRepeatButton(Guild guild, MessageChannel channel, Button button, Member member) {
        var manager = rocketDiscord.getMusicManager(guild.getId());
        if (manager.isEmpty()) {
            return;
        }
        var musicPlayer = manager.get();

        musicPlayer.setRepeat(!musicPlayer.isRepeat());
        channel.sendMessage(":repeat: Powtarzanie utworu: " + (musicPlayer.isRepeat() ? "wlaczone" : "wylaczone")).queue();
    }

    private void handleStopButton(Guild guild, MessageChannel channel, Button button, Member member) {
        var manager = rocketDiscord.getMusicManager(guild.getId());
        if (manager.isEmpty()) {
            return;
        }
        var musicPlayer = manager.get();

        musicPlayer.removeAllTracks();
        musicPlayer.getAudioPlayer().stopTrack();

        channel.sendMessage(":o: Zatrzymano odtwarzanie muzyki").queue();
    }
}
