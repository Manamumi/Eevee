package xyz.eevee.eevee.bot.command.util.remind;

import lombok.Getter;
import xyz.eevee.eevee.bot.command.CommandArguments;

import java.util.List;

public class RemindCommandArguments extends CommandArguments {
    @Getter
    private double days;
    @Getter
    private double hours;
    @Getter
    private double minutes;
    @Getter
    private double seconds;
    @Getter
    private List<String> action;

    private String daysLabel;
    private String hoursLabel;
    private String minutesLabel;
    private String secondsLabel;
}
