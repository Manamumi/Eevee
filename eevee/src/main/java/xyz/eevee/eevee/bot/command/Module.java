package xyz.eevee.eevee.bot.command;

import lombok.NonNull;
import xyz.eevee.eevee.target.TargetLock;
import xyz.eevee.munchlax.Member;

public interface Module {
    String getShortLabel();

    String getLabel();

    String getDescription();

    TargetLock getTargetLock();

    boolean canBeInvokedBy(@NonNull Member member);
}
