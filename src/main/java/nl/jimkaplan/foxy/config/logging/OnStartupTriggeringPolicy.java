package nl.jimkaplan.foxy.config.logging;

import ch.qos.logback.core.rolling.TriggeringPolicyBase;

import java.io.File;

public class OnStartupTriggeringPolicy<E> extends TriggeringPolicyBase<E> {
    private boolean trigger = true;

    @Override
    public boolean isTriggeringEvent(File activeFile, E event) {
        if (trigger) {
            trigger = false;
            return true; // Trigger rollover on first event
        }
        return false;
    }
}