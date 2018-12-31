package xyz.eevee.eevee.bot.command.subscription.anime.horriblesubs;

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
import xyz.eevee.eevee.parser.arguments.BooleanArgument;
import xyz.eevee.eevee.parser.arguments.LiteralArgument;
import xyz.eevee.eevee.parser.arguments.OrArgument;
import xyz.eevee.eevee.parser.arguments.StringArgument;
import xyz.eevee.eevee.provider.UuidProvider;
import xyz.eevee.eevee.repository.model.HsReleaseAnnouncer;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.eevee.util.PermissionUtil;
import xyz.eevee.munchlax.NewMessageEvent;
import xyz.eevee.munchlax.Permission;

import java.util.List;

@Log4j2
public class HsReleaseSubscribeCommand extends Command {
    public HsReleaseSubscribeCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "anime.hs.subscribe";
    }

    @Override
    public String getLabel() {
        return "Subscribe to HorribleSubs Releases";
    }

    @Override
    public String getDescription() {
        return "Sets release announcements to be posted in the current channel. " +
            "Requires manage channel permission to use. The download option is usable only by the bot owner.";
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return ImmutableList.of(Permission.MANAGE_CHANNEL);
    }

    @Override
    public Arguments<HsReleaseSubscribeCommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("hs"),
            new LiteralArgument("subscribe"),
            new StringArgument("animeName"),
            new OrArgument("quality", ImmutableList.of("480p", "720p", "1080p")),
            new BooleanArgument("download").withOptions(
                ArgumentOptions.builder()
                               .required(false)
                               .defaultValue(false)
                               .build()
            )
        ), HsReleaseSubscribeCommandArguments.class);
    }

    @Override
    public String getExample() {
        return "ev hs subscribe Hanebado! 1080p";
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        HsReleaseSubscribeCommandArguments args = (HsReleaseSubscribeCommandArguments) arguments;

        if (
            Session.getSession()
                   .getHsReleaseAnnouncerDataRepository()
                   .getAnnouncer(args.getAnimeName(), args.getQuality(), event.getChannelId())
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
                                    log.error("Failed to send HS subscription update as embed.", e);
                                }, event.getChannelId(), embedBuilder.build());
            return;
        }

        HsReleaseAnnouncer hsReleaseAnnouncer = HsReleaseAnnouncer.builder()
                                                                  .anime(args.getAnimeName())
                                                                  .quality(args.getQuality())
                                                                  .channelId(event.getChannelId())
                                                                  .lastEpisode(-1)
                                                                  .announcerId(UuidProvider.getUuid4())
                                                                  .download(
                                                                      PermissionUtil.isBotOwner(event.getAuthor()) &&
                                                                          args.isDownload()
                                                                  )
                                                                  .build();

        Session.getSession().getHsReleaseAnnouncerDataRepository().add(hsReleaseAnnouncer);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("HorribleSubs Release Subscription Added");
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.successEmbedColorDecimal"));
        embedBuilder.setDescription(
            String.format("Okay. I will announce when *%s* is released in %s.", args.getAnimeName(), args.getQuality())
        );

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.error("Failed to send HS subscription update as embed.", e);
                            }, event.getChannelId(), embedBuilder.build());
    }
}