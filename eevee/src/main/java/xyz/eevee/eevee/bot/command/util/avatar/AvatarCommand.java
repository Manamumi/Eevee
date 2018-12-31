package xyz.eevee.eevee.bot.command.util.avatar;

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
import xyz.eevee.eevee.parser.arguments.MemberArgument;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.eevee.util.Formatter;
import xyz.eevee.munchlax.NewMessageEvent;

@Log4j2
public class AvatarCommand extends Command {
    public AvatarCommand(CommandGroup commandGroup) {
        super(commandGroup);
    }

    @Override
    public String getShortLabel() {
        return "avatar";
    }

    @Override
    public String getLabel() {
        return "Get User Avatar";
    }

    @Override
    public String getDescription() {
        return "Returns a user's avatar. If no user is specified your own avatar is returned.";
    }

    @Override
    public String getExample() {
        return "ev avatar @Somebody";
    }

    @Override
    public Arguments<AvatarCommandArguments> getArguments() {
        return new Arguments<>(ImmutableList.of(
            new LiteralArgument("avatar"),
            new MemberArgument("mentionedUser").withOptions(
                ArgumentOptions.builder()
                               .required(false)
                               .build()
            )
        ), AvatarCommandArguments.class);
    }

    @Override
    public void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments) {
        AvatarCommandArguments args = (AvatarCommandArguments) arguments;
        String userName;
        String avatarUrl;

        if (args.getMentionedUser() == null) {
            userName = Formatter.formatTag(event.getAuthor());
            avatarUrl = event.getAuthor().getAvatar();
        } else {
            userName = Formatter.formatTag(args.getMentionedUser());
            avatarUrl = args.getMentionedUser().getAvatar();
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(String.format("User Avatar for %s", userName));
        embedBuilder.setImage(avatarUrl + "?size=512");
        embedBuilder.setColor(Session.getSession().getConfiguration().readInt("eevee.defaultEmbedColorDecimal"));

        EnforcedSafetyAction.builder()
                            .build()
                            .sendEmbedMessage(e -> {
                                log.warn("Failed to send user avatar as embed.", e);
                            }, event.getChannelId(), embedBuilder.build());
    }
}
