package xyz.eevee.eevee.bot.command.subscription.anime.nyaa;

import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.parser.arguments.ArgumentOptions;
import xyz.eevee.eevee.parser.arguments.Arguments;
import xyz.eevee.eevee.parser.arguments.LiteralArgument;
import xyz.eevee.eevee.parser.arguments.OrArgument;
import xyz.eevee.eevee.parser.arguments.StringArgument;
import xyz.eevee.eevee.provider.UuidProvider;
import xyz.eevee.eevee.repository.model.NyaaReleaseAnnouncer;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.munchlax.NewMessageEvent;
import xyz.eevee.munchlax.Permission;

import java.util.List;

@Log4j2
public class NyaaReleaseSubscribeCommand extends Command {
    public NyaaReleaseSubscribeCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "anime.nyaa.subscribe";
    }

    @Override
    public String getLabel() {
        return "Subscribe to Nyaa Releases";
    }

    @Override
    public String getDescription() {
        return "Sets release announcements for new anime on Nyaa to be posted in the current channel. " +
            "Requires manage channel permission to use. The download option is usable only by the bot owner.";
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return ImmutableList.of(Permission.MANAGE_CHANNEL);
    }

    @Override
    public Arguments<? extends CommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("nyaa"),
            new LiteralArgument("subscribe"),
            new StringArgument("subber"),
            new StringArgument("animeName"),
            new OrArgument("quality", ImmutableList.of("480p", "720p", "1080p")).withOptions(
                ArgumentOptions.builder()
                               .required(false)
                               .defaultValue(null)
                               .build()
            )
        ), NyaaReleaseSubscribeCommandArguments.class);
    }

    @Override
    public String getExample() {
        return "ev nyaa subscribe HorribleSubs Hanebado! 1080p";
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        NyaaReleaseSubscribeCommandArguments args = (NyaaReleaseSubscribeCommandArguments) arguments;

        if (
            Session.getSession()
                   .getNyaaReleaseAnnouncerDataRepository()
                   .getAnnouncer(args.getSubber(), args.getAnimeName(), args.getQuality(), event.getChannelId())
                   .isPresent()
            ) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Oops! An error occurred.");
            embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.errorEmbedColorDecimal"));
            embedBuilder.setDescription(
                String.format(
                    "A subscription for *%s* in %s already exists for this channel.",
                    args.getAnimeName(),
                    args.getQuality()
                )
            );

            EnforcedSafetyAction.builder()
                                .build()
                                .sendEmbedMessage(e -> {
                                    log.error("Failed to send nyaa subscription update as embed.", e);
                                }, event.getChannelId(), embedBuilder.build());
            return;
        }

        NyaaReleaseAnnouncer nyaaReleaseAnnouncer = NyaaReleaseAnnouncer.builder()
                                                                        .subber(args.getSubber())
                                                                        .anime(args.getAnimeName())
                                                                        .quality(args.getQuality())
                                                                        .channelId(event.getChannelId())
                                                                        .announcerId(UuidProvider.getUuid4())
                                                                        .build();

        Session.getSession().getNyaaReleaseAnnouncerDataRepository().add(nyaaReleaseAnnouncer);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Nyaa Release Subscription Added");
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.successEmbedColorDecimal"));
        embedBuilder.setDescription(
            String.format(
                "Okay. I will announce when *%s* is released in %s by %s.",
                args.getAnimeName(),
                args.getQuality(),
                args.getSubber()
            )
        );

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.error("Failed to send Nyaa subscription update as embed.", e);
                            }, event.getChannelId(), embedBuilder.build());
    }
}