package pro.paulek.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import pro.paulek.IRocketDiscord;

public class HelpCommand extends Command {

    public HelpCommand() {
        this.setName("help");
        this.setDescription("Pokazuje pomoc każdego polecenia");
        this.setUsage("Użycie: /help <nazwa-polecenia>");
        this.setCommandData(new CommandData("help", "Shows RocketDiscord bot help"));
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event, TextChannel channel, Guild guild, Member member) {
        event.reply(":sparkles: W celu uzyskania wskazówek dotyczących polecenia wpisz /help <nazwa polecenia> lub wpisz /command-list aby otrzymać listę poleceń :feather: , jeżeli natomiast chcesz sie dowiedzieć" +
                "co potrafię - napisz do mnie '@RocketDiscord co potrafisz' a ja ci odpowiem :D :goggles:").queue();
    }
}
