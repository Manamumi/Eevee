package common.util;

import lombok.NonNull;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    /**
     * Formats a duration into a human-readable string.
     *
     * @param duration The duration object to be formatted.
     * @return A human-readable string for the duration in days, hours, months, and minutes.
     */
    public static String durationToDdHhMmSs(@NonNull Duration duration) {
        int durationSeconds = (int) duration.getSeconds();

        int days = durationSeconds / (24 * 3600);
        int hours = (durationSeconds - (days * 24 * 3600)) / 3600;
        int minutes = (durationSeconds - (days * 24 * 3600) - (hours * 3600)) / 60;
        int seconds = durationSeconds - (days * 24 * 3600) - (hours * 3600) - (minutes * 60);

        return String.format("%s days %s hours %s minutes %s seconds", days, hours, minutes, seconds);
    }

    /**
     * Calculates the number of milliseconds in a given duration given the number of days, house, months, and minutes.
     *
     * @param days    The number of days.
     * @param hours   The number of hours.
     * @param minutes The number of minutes.
     * @param seconds The number of seconds.
     * @return A millisecond representation of the total duration of all the time units given.
     */
    public static long dhmsToMilli(double days, double hours, double minutes, double seconds) {
        return (long) (
            (days * 24 * 60 * 60 * 1000) + (hours * 60 * 60 * 1000) + (minutes * 60 * 1000) + (seconds * 1000));
    }

    /**
     * Formats a RFC822 timestamp to an instant. This should be used with RSS feeds.
     * @param timestamp A RFC822 formatted timestamp as a string.
     * @return An Instant representation of the given timestamp.
     */
    public static Instant rfc822ToInstant(String timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
        return Instant.from(formatter.parse(timestamp));
    }

    public static String offsetDateTimeToIso8601(OffsetDateTime timestamp) {
        return DateTimeFormatter.ISO_DATE_TIME.format(timestamp);
    }
}
