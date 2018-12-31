package xyz.eevee.eevee.target;

import lombok.NonNull;
import xyz.eevee.munchlax.Member;

public class GenericAllPassTargetLock implements TargetLock {
    /**
     * This is the default target lock check for all commands. By default, commands are available
     * to all users so this returns true.
     *
     * @param member A Discord member.
     * @return True
     */
    @Override
    public boolean check(@NonNull Member member) {
        return true;
    }
}
