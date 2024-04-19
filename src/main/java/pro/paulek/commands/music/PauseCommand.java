package pro.paulek.commands.music;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import pro.paulek.IRocketDiscord;
import pro.paulek.commands.Command;
import pro.paulek.managers.MusicManager;

import java.util.Objects;

public class PauseCommand extends Command {

    private final IRocketDiscord rocketDiscord;

    public PauseCommand(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);

        this.setName("pause");
        this.setDescription("pauses the currently playing music");
        this.setUsage("/pause");
        this.setCommandData(Commands.slash("pause", "Pauses the currently playing music"));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, TextChannel channel, Guild guild, Member member) {
        MusicManager musicPlayer = getOrCreateMusicManager(guild);

        var player = musicPlayer.getPlayer();
        if (player.isEmpty()) {
            event.reply(":x: Bot nie jest podłączony do kanału głosowego").queue();
            return;
        }

        if (player.get().isPaused()) {
            player.get().setPaused(false);
            event.reply(":arrow_forward: Wznowiłem muzykę").queue();
        } else {
            player.get().setPaused(true);
            event.reply(":pause_button: Zatrzymałem muzykę").queue();
        }
    }

    private MusicManager getOrCreateMusicManager(Guild guild) {
        var manager = rocketDiscord.getMusicManager(guild.getId());
        if (manager.isPresent()) {
            return manager.get();
        }

        MusicManager musicPlayer = new MusicManager(rocketDiscord.getAudioManager().createPlayer(), guild);
        musicPlayer.init();
        rocketDiscord.getMusicManagers().add(guild.getId(), musicPlayer);
        return musicPlayer;
    }
}