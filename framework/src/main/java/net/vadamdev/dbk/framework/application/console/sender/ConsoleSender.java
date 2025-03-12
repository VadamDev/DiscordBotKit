package net.vadamdev.dbk.framework.application.console.sender;

import net.vadamdev.dbk.framework.DBKFramework;

/**
 * Default implementation of {@link Sender} which reply to the DBKFramework logger
 *
 * @author VadamDev
 * @since 27/10/2024
 */
public final class ConsoleSender implements Sender {
    @Override
    public void reply(String str) {
        DBKFramework.get().getLogger().info(str);
    }

    private static final String NAME = "Console";

    @Override
    public String getName() {
        return NAME;
    }
}
