package pro.paulek.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import pro.paulek.IRocketDiscord;

import java.util.Objects;

public class HelpCommand extends Command {

    private final IRocketDiscord rocketDiscord;

    public HelpCommand(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);

        this.setName("help");
        this.setDescription("pokazuje pomoc każdego polecenia");
        this.setUsage("/help <nazwa-polecenia>");
        this.setCommandData(Commands.slash("help", "Shows RocketDiscord bot help"));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, TextChannel channel, Guild guild, Member member) {
        if (event.getSubcommandName() == null) {
            event.reply(":sparkles: W celu uzyskania wskazówek dotyczących polecenia wpisz /help <nazwa polecenia> lub wpisz /command-list aby otrzymać listę poleceń :feather: , jeżeli natomiast chcesz sie dowiedzieć" +
                    "co potrafię - napisz do mnie '@RocketDiscord co potrafisz' a ja ci odpowiem :D :goggles:").queue();
            return;
        }

        var commandToHelp = event.getSubcommandName();
        var cmd = rocketDiscord.getCommandManager().getCommandList().get(commandToHelp);

        event.reply(":herb: Polecenie " + cmd.getName() + " " + cmd.getDescription()).queue();
        event.reply(":ear_of_rice: Aby poprawnie użyć tego polecenia, musisz zbudować taką konstrukcję: " + cmd.getUsage());
    }
}
