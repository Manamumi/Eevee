package xyz.eevee.eevee.bot.command.dev.build;

import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.parser.arguments.Arguments;
import xyz.eevee.eevee.parser.arguments.LiteralArgument;
import xyz.eevee.eevee.session.BuildInfo;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.munchlax.NewMessageEvent;

@Log4j2
public class BuildInfoCommand extends Command {
    public BuildInfoCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "build.info";
    }

    @Override
    public String getLabel() {
        return "Show Build Info";
    }

    @Override
    public String getDescription() {
        return "Shows information about the current Eevee build.";
    }

    @Override
    public String getExample() {
        return "ev build info";
    }

    @Override
    public Arguments<? extends CommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("build"),
            new LiteralArgument("info")
        ), BuildInfoCommandArguments.class);
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        final BuildInfo buildInfo = Session.getSession().getBuildInfo();
        final EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));
        embedBuilder.setAuthor(
            String.format(
                "%s (@%s)",
                buildInfo.getBuiltByName(),
                buildInfo.getBuiltBy()
            ),
            null,
            String.format(
                Session.getSession().getConfiguration().readString("eevee.gitlabAvatarTemplateURL"),
                buildInfo.getBuiltById()
            )
        );
        embedBuilder.setDescription(
            String.format(
                "%s%n%n*This build was built at %s UTC.*",
                buildInfo.getCiCommitMessage(),
                buildInfo.getBuildTime()
            )
        );
        embedBuilder.setFooter(
            String.format(
                "Build #%s | SHA: %s", buildInfo.getCiJobId(), buildInfo.getCiCommitSha()
            ),
            null
        );

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.warn("Failed to send build info in message.", e);
                            }, event.getChannelId(), embedBuilder.build());
    }
}
