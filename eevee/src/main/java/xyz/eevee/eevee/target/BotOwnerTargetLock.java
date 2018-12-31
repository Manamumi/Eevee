package xyz.eevee.eevee.target;

import lombok.NonNull;
import xyz.eevee.eevee.util.PermissionUtil;
import xyz.eevee.munchlax.Member;

public class BotOwnerTargetLock implements TargetLock {
    /**
     * Only returns true if a member is the bot owner. The bot owner is defined in Coffee.
     *
     * @param member A Discord member.
     * @return True if a given member is the bot owner.
     */
    @Override
    public boolean check(@NonNull Member member) {
        return PermissionUtil.isBotOwner(member);
    }
}
