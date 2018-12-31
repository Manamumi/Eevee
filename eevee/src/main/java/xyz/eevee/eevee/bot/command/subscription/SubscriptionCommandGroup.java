package xyz.eevee.eevee.bot.command.subscription;

import com.google.common.collect.ImmutableList;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.bot.command.subscription.anime.horriblesubs.HsReleaseListCommand;
import xyz.eevee.eevee.bot.command.subscription.anime.horriblesubs.HsReleaseSubscribeCommand;
import xyz.eevee.eevee.bot.command.subscription.anime.horriblesubs.HsReleaseUnsubscribeCommand;
import xyz.eevee.eevee.bot.command.subscription.anime.nyaa.NyaaReleaseListCommand;
import xyz.eevee.eevee.bot.command.subscription.anime.nyaa.NyaaReleaseSubscribeCommand;
import xyz.eevee.eevee.bot.command.subscription.anime.nyaa.NyaaReleaseUnsubscribeCommand;
import xyz.eevee.eevee.bot.command.subscription.manga.mangadex.MangaDexReleaseListCommand;
import xyz.eevee.eevee.bot.command.subscription.manga.mangadex.MangaDexReleaseSubscribeCommand;
import xyz.eevee.eevee.bot.command.subscription.manga.mangadex.MangaDexReleaseUnsubscribeCommand;
import xyz.eevee.eevee.bot.command.subscription.twitter.TwitterSubscribeCommand;
import xyz.eevee.eevee.bot.command.subscription.twitter.TwitterUnsubscribeCommand;

import java.util.List;

public class SubscriptionCommandGroup extends CommandGroup {
    private final List<Command> commands = ImmutableList.of(
        new HsReleaseListCommand(this),
        new HsReleaseSubscribeCommand(this),
        new HsReleaseUnsubscribeCommand(this),
        new TwitterSubscribeCommand(this),
        new TwitterUnsubscribeCommand(this),
        new MangaDexReleaseListCommand(this),
        new MangaDexReleaseSubscribeCommand(this),
        new MangaDexReleaseUnsubscribeCommand(this),
        new NyaaReleaseListCommand(this),
        new NyaaReleaseSubscribeCommand(this),
        new NyaaReleaseUnsubscribeCommand(this)
    );

    @Override
    public String getShortLabel() {
        return "subscription";
    }

    @Override
    public String getLabel() {
        return ":newspaper: Subscription Commands";
    }

    @Override
    public String getDescription() {
        return "Commands that let you subscribe to things and get notifications in different channels.";
    }

    @Override
    public List<Command> getCommands() {
        return commands;
    }
}
