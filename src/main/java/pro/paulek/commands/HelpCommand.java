package pro.paulek.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class HelpCommand extends Command {

    public HelpCommand() {
        this.setName("help");
        this.setDescription("Pokazuje pomoc każdego polecenia");
        this.setUsage("Użycie: /help <nazwa-polecenia>");
    }

    @Override
    public void execute(@NotNull MessageReceivedEvent event, TextChannel channel, Guild guild, Member member) {
        channel.sendMessage(":satellite_orbital: There is no help for u.").queue();
    }
}
