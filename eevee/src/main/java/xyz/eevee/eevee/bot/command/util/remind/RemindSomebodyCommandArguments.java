package xyz.eevee.eevee.bot.command.util.remind;

import lombok.Getter;
import xyz.eevee.eevee.bot.command.CommandArguments;
import xyz.eevee.munchlax.Member;

import java.util.List;

public class RemindSomebodyCommandArguments extends CommandArguments {
    @Getter
    private Member person;
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
