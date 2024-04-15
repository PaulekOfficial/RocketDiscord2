package pro.paulek.commands.music;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.commands.Command;
import pro.paulek.managers.MusicManager;

import java.util.Objects;

public class VolumeCommand extends Command {

    private final static Logger logger = LoggerFactory.getLogger(VolumeCommand.class);

    private final IRocketDiscord rocketDiscord;

    public VolumeCommand(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);

        this.setName("volume");
        this.setDescription("zmienia glosnosc bota");
        this.setUsage("/volume <glosnosc od 1 do 100>");
        var commandData = Commands.slash("volume", "Changes volume of music bot future");
        commandData.addOption(OptionType.INTEGER, "volume", "Percent of total volume", true);
        this.setCommandData(commandData);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, TextChannel channel, Guild guild, Member member) {
        MusicManager musicPlayer = null;

        var manager = rocketDiscord.getMusicManager(guild.getId());
        if (manager.isEmpty()) {
            logger.warn("Music manager is empty for guild {}", guild.getId());
            musicPlayer = new MusicManager(rocketDiscord.getAudioManager().createPlayer(), guild);
            musicPlayer.init();
            rocketDiscord.getMusicManagers().add(guild.getId(), musicPlayer);
        }

        if (manager.isPresent()) {
            musicPlayer = manager.get();
        }

        var memberAudioChannel = Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).getChannel();
        if ((!event.getMember().getVoiceState().inAudioChannel() ||  !Objects.requireNonNull(memberAudioChannel).getId().equals(musicPlayer.getAudioChannel().getId())) &&
                !event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.reply(":construction: Aby kontrolować bota, musisz byc na kanale z nim!").queue();
            return;
        }

        int volume = Objects.requireNonNull(event.getOption("volume")).getAsInt();
        if (volume > 200) {
            event.reply(":speaker: Przekroczono dopuszczalny poziom dzwięku, maksymalny to 200%!").queue();
            return;
        }

        musicPlayer.getPlayer().setVolume(volume);
        event.reply("Zmieniłem głośność :wink:").queue();
    }
}
