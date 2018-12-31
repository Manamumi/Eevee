package xyz.eevee.eevee.bot.command;

import com.google.common.collect.ImmutableList;
import common.util.RateLimiter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import xyz.eevee.eevee.parser.arguments.Arguments;
import xyz.eevee.eevee.session.Session;
import xyz.eevee.eevee.target.GenericAllPassTargetLock;
import xyz.eevee.eevee.target.TargetLock;
import xyz.eevee.eevee.util.DeprecateWithPorygonUtil;
import xyz.eevee.eevee.util.PermissionUtil;
import xyz.eevee.munchlax.Member;
import xyz.eevee.munchlax.NewMessageEvent;
import xyz.eevee.munchlax.Permission;
import xyz.eevee.munchlax.User;

import java.util.List;

@AllArgsConstructor
public abstract class Command implements Module {
    @NonNull
    @Getter
    private CommandGroup commandGroup;

    public void bootstrap() {
    }

    public List<Permission> getRequiredPermissions() {
        return ImmutableList.of();
    }

    public TargetLock getTargetLock() {
        return new GenericAllPassTargetLock();
    }

    public RateLimiter getRateLimiter() {
        return null;
    }

    public boolean canBeInvokedBy(@NonNull Member member) {
        if (PermissionUtil.isBotOwner(member)) {
            return true;
        }

        if (!getTargetLock().check(member)) {
            return false;
        }

        if (!getCommandGroup().getTargetLock().check(member)) {
            return false;
        }

        return DeprecateWithPorygonUtil.hasPermission(
            member, getRequiredPermissions()
        );
    }

    public boolean isRateLimited(@NonNull User user) {
        // Bot owner is never rate-limited.
        if (PermissionUtil.isBotOwner(user)) {
            return false;
        }

        String userId = user.getId();

        // Developers are not rate-limited.
        if (Session.getSession().getConfiguration().readStringList("eevee.tl.dev").contains(userId)) {
            return false;
        }

        return getRateLimiter() != null && !getRateLimiter().tryIncrement(userId);
    }

    public String toString() {
        return String.format("**__%s__**%n%n%s%n%n```%s```%n", getLabel(), getDescription(), getArguments());
    }

    public abstract String getShortLabel();

    public abstract String getLabel();

    public abstract String getDescription();

    public abstract String getExample();

    public abstract Arguments<? extends CommandArguments> getArguments();

    public abstract void invoke(@NonNull NewMessageEvent event, @NonNull CommandArguments arguments);
}
