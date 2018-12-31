package xyz.eevee.eevee.bot.command.subscription.anime.horriblesubs;

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
import xyz.eevee.eevee.parser.arguments.StringArgument;
import xyz.eevee.eevee.repository.model.HsReleaseAnnouncer;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.munchlax.NewMessageEvent;
import xyz.eevee.munchlax.Permission;

import java.util.List;
import java.util.Optional;

@Log4j2
public class HsReleaseUnsubscribeCommand extends Command {
    public HsReleaseUnsubscribeCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "anime.hs.unsubscribe";
    }

    @Override
    public String getLabel() {
        return "Unsubscribe from HorribleSubs Releases";
    }

    @Override
    public String getDescription() {
        return "Unsubscribe from HorribleSubs releases. Release announcements will " +
            "no longer be posted in the current channel. This command requires manage channel permission.";
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return ImmutableList.of(Permission.MANAGE_CHANNEL);
    }

    @Override
    public Arguments<HsReleaseUnsubscribeCommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("hs"),
            new LiteralArgument("unsubscribe"),
            new StringArgument("animeName"),
            new StringArgument("quality")
        ), HsReleaseUnsubscribeCommandArguments.class);
    }

    @Override
    public String getExample() {
        return "ev hs unsubscribe Hanebado! 1080p";
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        HsReleaseUnsubscribeCommandArguments args = (HsReleaseUnsubscribeCommandArguments) arguments;
        Optional<HsReleaseAnnouncer> announcerOptional = Session.getSession()
                                                                .getHsReleaseAnnouncerDataRepository()
                                                                .getAnnouncer(
                                                                    args.getAnimeName(),
                                                                    args.getQuality(),
                                                                    event.getChannelId()
                                                                );

        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (!announcerOptional.isPresent()) {
            embedBuilder.setTitle("Oops! An error occurred.");
            embedBuilder.setDescription("The requested subscription does not exist.");
            embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.errorEmbedColorDecimal"));

            EnforcedSafetyAction.builder()
                                .build()
                                .sendEmbedMessage(e -> {
                                    log.error("Failed to send HS subscription update as embed.", e);
                                }, event.getChannelId(), embedBuilder.build());
            return;
        }

        HsReleaseAnnouncer announcer = announcerOptional.get();
        Session.getSession().getHsReleaseAnnouncerDataRepository().remove(announcer);

        embedBuilder.setTitle("HorribleSubs Release Subscription Cancelled");
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.successEmbedColorDecimal"));
        embedBuilder.setDescription(
            String.format(
                "Okay. This channel will no longer receive announcements when *%s* is released in %s.",
                announcer.getAnime(),
                announcer.getQuality()
            )
        );

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.error("Failed to send HS subscription update as embed.", e);
                            }, event.getChannelId(), embedBuilder.build());
    }
}
