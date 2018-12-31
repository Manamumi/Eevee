package xyz.eevee.eevee.bot.command.util;

import com.google.common.collect.ImmutableList;
import xyz.eevee.eevee.bot.command.Command;
import xyz.eevee.eevee.bot.command.CommandGroup;
import xyz.eevee.eevee.bot.command.util.avatar.AvatarCommand;
import xyz.eevee.eevee.bot.command.util.help.HelpCommand;
import xyz.eevee.eevee.bot.command.util.invite.InviteCommand;
import xyz.eevee.eevee.bot.command.util.jisho.JishoCommand;
import xyz.eevee.eevee.bot.command.util.remind.RemindCommand;
import xyz.eevee.eevee.bot.command.util.remind.RemindSomebodyCommand;
import xyz.eevee.eevee.bot.command.util.remind.RemindToggleCommand;
import xyz.eevee.eevee.bot.command.util.stats.StatsCommand;
import xyz.eevee.eevee.bot.command.util.translate.TranslateCommand;
import xyz.eevee.eevee.bot.command.util.welcome.RemoveWelcomeCommand;
import xyz.eevee.eevee.bot.command.util.welcome.WelcomeCommand;

import java.util.List;

public class UtilityCommandGroup extends CommandGroup {
    private final List<Command> commands = ImmutableList.of(
        new AvatarCommand(this),
        new HelpCommand(this),
        new JishoCommand(this),
        new RemindCommand(this),
        new RemindSomebodyCommand(this),
        new RemindToggleCommand(this),
        new StatsCommand(this),
        new TranslateCommand(this),
        new InviteCommand(this),
        new WelcomeCommand(this),
        new RemoveWelcomeCommand(this)
    );

    @Override
    public String getShortLabel() {
        return "util";
    }

    @Override
    public String getLabel() {
        return ":tools: Utility Commands";
    }

    @Override
    public String getDescription() {
        return "Commands that provide some sort of utility like translations and reminders.";
    }

    @Override
    public List<Command> getCommands() {
        return commands;
    }
}
