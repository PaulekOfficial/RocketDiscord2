package pro.paulek.commands.fun;

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
import pro.paulek.commands.music.PlayCommand;
import pro.paulek.objects.Rule34Posts;
import pro.paulek.util.RuleUtil;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Rule34Command extends Command {

    private final static Logger logger = LoggerFactory.getLogger(PlayCommand.class);
    private static final Random random = new Random();

    private static List<String> BLOCKED_TAGS = Arrays.asList("loli", "shota", "child", "childs");

    private final IRocketDiscord rocketDiscord;

    public Rule34Command(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);

        this.setName("rule34");
        this.setDescription("pokazuje obrazki z rule34");
        this.setUsage("/rule34");
        var commandData = Commands.slash("rule34", "Shows pictures from rule34");
        commandData.addOption(OptionType.STRING, "tags", "Tags to filter rule34 response");
        this.setCommandData(commandData);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, TextChannel channel, Guild guild, Member member) {
        if (!channel.isNSFW()) {
            event.reply("\uD83D\uDD1E Te polecenie może wyświetlać treści erotyczne, tego polecenia należy używać na kanałach NSFW - pamiętaj musisz mieć 18 lat aby tego używać.").queue();
            return;
        }

        if (event.getOption("tags") == null) {
            try {
                String ruleUrl = RuleUtil.Rule34Url(1, true);
                String response = RuleUtil.Rule34Response(ruleUrl);
                Rule34Posts posts = RuleUtil.UnmarshallRule34Post(response);
                event.reply(":small_orange_diamond: Wysyłam losowy obrazek").queue();

                channel.sendMessage(posts.getAllTags().get(random.nextInt(Integer.parseInt(posts.getCount()))).getFileUrl()).delay(Duration.ofSeconds(2)).queue();
                return;
            } catch (Exception exception) {
                logger.error("Error while getting rule34", exception);
                event.reply(":interrobang: Wystąpił błąd podczas wyszukiwania.").queue();
                return;
            }
        }

        String[] tags = Objects.requireNonNull(event.getOption("tags")).getAsString().split(" ");

        // Check if child content is present
        if (BLOCKED_TAGS.stream().anyMatch(tag -> Arrays.stream(tags).anyMatch(tagg -> tagg.equalsIgnoreCase(tag)))) {
            event.reply(":nauseated_face: OBRZYDLIWE! :man_police_officer: Treści tego typu są surowo zakanaze w naszym społeczeństwie. Ponowna próba zakończy się banem.").queue();
            return;
        }

        String ruleUrl = RuleUtil.Rule34Url(100, false, tags);
        try {
            String response = RuleUtil.Rule34Response(ruleUrl);
            Rule34Posts posts = RuleUtil.UnmarshallRule34Post(response);
            event.reply(String.format(":fountain: Znaleziono %s obrazków wysyłam losowy...", posts.getCount())).queue();

            channel.sendMessage(posts.getAllTags().get(random.nextInt(Integer.parseInt(posts.getCount()))).getFileUrl()).delay(Duration.ofSeconds(2)).queue();
        } catch (Exception exception) {
            logger.error("Error while getting rule34", exception);
            event.reply(":interrobang: Wystąpił błąd podczas wyszukiwania.").queue();
        }
    }
}
