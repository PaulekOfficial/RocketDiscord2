package pro.paulek.commands.music;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.commands.Command;
import pro.paulek.managers.MusicManager;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class LeaveCommand extends Command {

    private final static Logger logger = LoggerFactory.getLogger(PlayCommand.class);

    private final IRocketDiscord rocketDiscord;

    public LeaveCommand(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);

        this.setName("leave");
        this.setDescription("rozkazuje botu wyjście z kanału głosowego i zakończenie muzyki");
        this.setUsage("/leave");
        var commandData = Commands.slash("leave", "Orders bot to leave voice channel");
        this.setCommandData(commandData);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, TextChannel channel, Guild guild, Member member) {
        var manager = rocketDiscord.getMusicManager(guild.getId());
        if (manager.isEmpty()) {
            logger.warn("Music manager is empty for guild {}", guild.getId());
            var musicPlayer = new MusicManager(rocketDiscord.getAudioManager().createPlayer(), guild);
            musicPlayer.init();
            rocketDiscord.getMusicManagers().add(guild.getId(), musicPlayer);

            manager = rocketDiscord.getMusicManager(guild.getId());
        }

        if (manager.isEmpty()) {
            event.reply(":confused: Coś poszło nie tak, spróbuj ponownie").queue();
            return;
        }
        var musicPlayer = manager.get();

        var memberAudioChannel = event.getMember().getVoiceState().getChannel();
//        if (!event.getMember().getVoiceState().inAudioChannel() || !memberAudioChannel.getId().equals(musicPlayer.getAudioChannel())) {
//            event.reply(":construction: Aby kontrolować bota, musisz byc na kanale z nim!").queue();
//            return;
//        }

        if (!guild.getAudioManager().isConnected()) {
            event.reply(":confused: Ale ja przecież nie jestem połączony z chatem głosowym").queue();
            return;
        }

        var stopped = musicPlayer.stopTrack();
        var leaved = musicPlayer.leaveChannel();
        try {
            if ((stopped.isDone() && !stopped.get()) || (leaved.isDone() && !leaved.get())){
                event.reply(":confused: Coś poszło nie tak, spróbuj ponownie").queue();
                return;
            }

            event.reply(":boomerang: Wyszedłem z kanału").queue();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
