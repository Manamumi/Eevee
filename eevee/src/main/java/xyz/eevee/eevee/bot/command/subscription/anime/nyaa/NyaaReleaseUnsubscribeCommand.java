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
import xyz.eevee.eevee.repository.model.NyaaReleaseAnnouncer;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.munchlax.NewMessageEvent;
import xyz.eevee.munchlax.Permission;

import java.util.List;
import java.util.Optional;

@Log4j2
public class NyaaReleaseUnsubscribeCommand extends Command {
    public NyaaReleaseUnsubscribeCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "anime.nyaa.unsubscribe";
    }

    @Override
    public String getLabel() {
        return "Unsubscribe from Nyaa Releases";
    }

    @Override
    public String getDescription() {
        return "Unsubscribe from Nyaa releases. Release announcements will " +
            "no longer be posted in the current channel. This command requires manage channel permission.";
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return ImmutableList.of(Permission.MANAGE_CHANNEL);
    }

    @Override
    public Arguments<? extends CommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("nyaa"),
            new LiteralArgument("unsubscribe"),
            new StringArgument("subber"),
            new StringArgument("animeName"),
            new OrArgument("quality", ImmutableList.of("480p", "720p", "1080p")).withOptions(
                ArgumentOptions.builder()
                               .required(false)
                               .defaultValue(null)
                               .build()
            )
        ), NyaaReleaseUnsubscribeCommandArguments.class);
    }

    @Override
    public String getExample() {
        return "ev nyaa unsubscribe HorribleSubs Hanebado! 1080p";
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        NyaaReleaseUnsubscribeCommandArguments args = (NyaaReleaseUnsubscribeCommandArguments) arguments;
        Optional<NyaaReleaseAnnouncer> announcerOptional = Session.getSession()
                                                                  .getNyaaReleaseAnnouncerDataRepository()
                                                                  .getAnnouncer(
                                                                      args.getSubber(),
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
                                    log.error("Failed to send nyaa subscription update as embed.", e);
                                }, event.getChannelId(), embedBuilder.build());
            return;
        }

        NyaaReleaseAnnouncer announcer = announcerOptional.get();
        Session.getSession().getNyaaReleaseAnnouncerDataRepository().remove(announcer);

        embedBuilder.setTitle("Nyaa Release Subscription Cancelled");
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.successEmbedColorDecimal"));
        embedBuilder.setDescription(
            String.format(
                "Okay. This channel will no longer receive announnyaacements when *%s* is released in %s by %s.",
                announcer.getAnime(),
                announcer.getQuality(),
                announcer.getSubber()
            )
        );

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.error("Failed to send Nyaa subscription update as embed.", e);
                            }, event.getChannelId(), embedBuilder.build());
    }
}
