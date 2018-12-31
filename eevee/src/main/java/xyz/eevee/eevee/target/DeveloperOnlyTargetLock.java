package xyz.eevee.eevee.target;

import xyz.eevee.eevee.session.Session;
import xyz.eevee.munchlax.Member;

public class DeveloperOnlyTargetLock implements TargetLock {
    /**
     * This target lock will only return true if a discord member is an Eevee developer. Developers may whitelist
     * themselves in Coffee.
     *
     * @param member A Discord member.
     * @return True if a member is an Eevee developer else false.
     */
    @Override
    public boolean check(Member member) {
        return Session.getSession()
                      .getConfiguration()
                      .readStringList("tl.dev")
                      .contains(member.getUser().getId());
    }
}
