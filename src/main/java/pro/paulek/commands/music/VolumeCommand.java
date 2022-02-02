package pro.paulek.commands.music;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.commands.Command;
import pro.paulek.objects.MusicManager;

import java.util.Objects;

public class VolumeCommand extends Command {

    private final static Logger logger = LoggerFactory.getLogger(VolumeCommand.class);

    private final IRocketDiscord rocketDiscord;

    public VolumeCommand(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);

        this.setName("volume");
        this.setDescription("zmienia glosnosc bota");
        this.setUsage("/volume <glosnosc od 1 do 100>");
        var commandData = new CommandData("volume", "Changes volume of music bot future");
        commandData.addOption(OptionType.INTEGER, "volume", "Percent of total volume", true);
        this.setCommandData(commandData);
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event, TextChannel channel, Guild guild, Member member) {
        var musicPlayer = rocketDiscord.getMusicManager(guild.getId());

        if (musicPlayer == null) {
            musicPlayer = new MusicManager(rocketDiscord.getAudioManager().createPlayer(), guild);
            musicPlayer.init();
            rocketDiscord.getMusicManagers().add(guild.getId(), musicPlayer);
        }

        musicPlayer.getAudioPlayer().setVolume((int) Objects.requireNonNull(event.getOption("volume")).getAsLong());
        event.reply("Zmieniłem głośność :wink:").queue();
    }
}
