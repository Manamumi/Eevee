package xyz.eevee.eevee.bot.command.subscription.manga.mangadex;

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
import xyz.eevee.eevee.parser.arguments.StringArgument;
import xyz.eevee.eevee.provider.UuidProvider;
import xyz.eevee.eevee.repository.model.MangaDexReleaseAnnouncer;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.munchlax.NewMessageEvent;
import xyz.eevee.munchlax.Permission;

import java.util.List;

@Log4j2
public class MangaDexReleaseSubscribeCommand extends Command {
    public MangaDexReleaseSubscribeCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "manga.mangadex.subscribe";
    }

    @Override
    public String getLabel() {
        return "Subscribe to MangaDex Releases";
    }

    @Override
    public String getDescription() {
        return "Sets release announcements to be posted in the current channel. " +
            "Requires manage channel permission to use.";
    }

    @Override
    public String getExample() {
        return "ev manga subscribe Gokushufudou \"Sexy Akiba Detectives\"";
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return ImmutableList.of(Permission.MANAGE_CHANNEL);
    }

    @Override
    public Arguments<? extends CommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("manga"),
            new LiteralArgument("subscribe"),
            new StringArgument("mangaName"),
            new StringArgument("scanlationGroup").withOptions(
                ArgumentOptions.builder()
                               .required(false)
                               .build()
            )
        ), MangaDexReleaseSubscribeCommandArguments.class);
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        MangaDexReleaseSubscribeCommandArguments args = (MangaDexReleaseSubscribeCommandArguments) arguments;

        if (
            Session.getSession()
                   .getMangaDexReleaseAnnouncerDataRepository()
                   .getAnnouncer(args.getMangaName(), args.getScanlationGroup(), event.getChannelId())
                   .isPresent()
        ) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Oops! An error occurred.");
            embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.errorEmbedColorDecimal"));
            embedBuilder.setDescription(
                String.format(
                    "A subscription for *%s* already exists for this channel.",
                    args.getMangaName()
                )
            );

            EnforcedSafetyAction.builder()
                                .build()
                                .sendEmbedMessage(e -> {
                                    log.error("Failed to send MangaDex subscription update as embed.", e);
                                }, event.getChannelId(), embedBuilder.build());
            return;
        }

        MangaDexReleaseAnnouncer mangaDexReleaseAnnouncer = MangaDexReleaseAnnouncer.builder()
                                                                                    .title(args.getMangaName())
                                                                                    .channelId(event.getChannelId())
                                                                                    .scanlator(args.getScanlationGroup())
                                                                                    .announcerId(UuidProvider.getUuid4())
                                                                                    .build();

        Session.getSession().getMangaDexReleaseAnnouncerDataRepository().add(mangaDexReleaseAnnouncer);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("MangaDex Release Subscription Added");
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.successEmbedColorDecimal"));
        embedBuilder.setDescription(
            String.format("Okay. I will announce when *%s* is released on MangaDex.", args.getMangaName())
        );

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.error("Failed to send Manga subscription update as embed.", e);
                            }, event.getChannelId(), embedBuilder.build());
    }
}
