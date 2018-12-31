package xyz.eevee.eevee.bot.command.subscription.manga.mangadex;

import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.EmbedBuilder;
import xyz.eevee.eevee.action.EnforcedSafetyAction;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.parser.arguments.ArgumentOptions;
import xyz.eevee.eevee.parser.arguments.Arguments;
import xyz.eevee.eevee.parser.arguments.LiteralArgument;
import xyz.eevee.eevee.parser.arguments.StringArgument;
import xyz.eevee.eevee.repository.model.MangaDexReleaseAnnouncer;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.munchlax.NewMessageEvent;
import xyz.eevee.munchlax.Permission;

import java.util.List;
import java.util.Optional;

@Log4j2
public class MangaDexReleaseUnsubscribeCommand extends Command {
    public MangaDexReleaseUnsubscribeCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "manga.mangadex.unsubscribe";
    }

    @Override
    public String getLabel() {
        return "Unsubscribe from MangaDex Releases";
    }

    @Override
    public String getDescription() {
        return "Unsubscribe from MangaDex release announcements. MangaDex releases will no longer " +
            "be announced in this channel. Requires manage channel permission to use.";
    }

    @Override
    public String getExample() {
        return "ev manga unsubscribe Gokushufudou \"Sexy Akiba Detectives\"";
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return ImmutableList.of(Permission.MANAGE_CHANNEL);
    }

    @Override
    public Arguments<? extends CommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("manga"),
            new LiteralArgument("unsubscribe"),
            new StringArgument("mangaName"),
            new StringArgument("scanlationGroup").withOptions(
                ArgumentOptions.builder()
                               .required(false)
                               .build()
            )
        ), MangaDexReleaseUnsubscribeCommandArguments.class);
    }

    @Override
    public void invoke(NewMessageEvent event, CommandArguments arguments) {
        MangaDexReleaseUnsubscribeCommandArguments args = (MangaDexReleaseUnsubscribeCommandArguments) arguments;
        Optional<MangaDexReleaseAnnouncer> announcerOptional = Session.getSession()
                                                                      .getMangaDexReleaseAnnouncerDataRepository()
                                                                      .getAnnouncer(
                                                                          args.getMangaName(),
                                                                          args.getScanlationGroup(),
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
                                    log.error("Failed to send MangaDex subscription update as embed.", e);
                                }, event.getChannelId(), embedBuilder.build());
            return;
        }

        MangaDexReleaseAnnouncer announcer = announcerOptional.get();
        Session.getSession().getMangaDexReleaseAnnouncerDataRepository().remove(announcer);

        embedBuilder.setTitle("MangaDex Release Subscription Cancelled");
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.successEmbedColorDecimal"));
        embedBuilder.setDescription(
            String.format(
                "Okay. This channel will no longer receive announcements when *%s* is released.",
                announcer.getTitle()
            )
        );

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.error("Failed to send MangaDex subscription update as embed.", e);
                            }, event.getChannelId(), embedBuilder.build());
    }
}
