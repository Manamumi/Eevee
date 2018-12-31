package xyz.eevee.eevee.target;

import xyz.eevee.munchlax.Member;

public interface TargetLock {
    /**
     * Given a Discord member, this method should check whether or not the member passes some check.
     *
     * @param user A Discord user.
     * @return True or false depending on whether or not a given user passes some check.
     */
    boolean check(Member member);
}
