package xyz.eevee.eevee.util;

import common.util.RateLimiter;
import lombok.NonNull;
import xyz.eevee.munchlax.Permission;
import xyz.eevee.munchlax.User;

import java.util.List;
import java.util.stream.Collectors;

public class Formatter {
    /**
     * Given a Discord user, return their full Discord tag in the format of name#discrim.
     *
     * @param user A Discord user.
     * @return The user's Discord tag in the format of name#discrim.
     */
    public static String formatTag(@NonNull User user) {
        return String.format("%s#%s", user.getUsername(), user.getDiscriminator());
    }

    /**
     * Returns "Yes" or "No" depending on the value of a given boolean.
     *
     * @param bool A boolean value.
     * @return "Yes" or "No" depending on whether a boolean is true or false.
     */
    public static String formatBoolean(boolean bool) {
        return bool ? "Yes" : "No";
    }

    /**
     * Returns a string representation of a rate limiter. The format of the returned string is of "X every Y seconds"
     *
     * @param rateLimiter A rate limiter.
     * @return "X every Y seconds" with "X" and "Y" substituted for the max hits of duration of the given rate limiter.
     */
    public static String formatRateLimit(RateLimiter rateLimiter) {
        if (rateLimiter == null) {
            return "None";
        }

        return String.format("%s every %s seconds", rateLimiter.getMaxHits(), rateLimiter.getDuration() / 1000);
    }

    /**
     * Returns a string representation of a list of Discord permissions.
     *
     * @param permissions A list of Discord permissions.
     * @return A string representation of a list of Discord permissions. If a given list is empty then "None" will
     * be returned instead.
     */
    public static String formatPermissions(@NonNull List<Permission> permissions) {
        if (permissions.isEmpty()) {
            return "None";
        }

        return permissions.stream().map(
            p -> p.getValueDescriptor().getName()
        ).collect(Collectors.joining(", "));
    }

    /**
     * Given a string for some Twitter user, format it to be proper (always start with @).
     *
     * @param user A string representation of a Twitter username.
     * @return The given username string with "@" prepended if it is not already.
     */
    public static String formatTwitterUser(@NonNull String user) {
        if (user.charAt(0) != '@') {
            return String.format("@%s", user);
        }

        return user;
    }
}
