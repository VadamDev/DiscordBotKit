package net.vadamdev.dbk.application.console.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link Sender} which reply to the DBKFramework logger
 *
 * @author VadamDev
 * @since 27/10/2024
 */
public final class ConsoleSender implements Sender {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleSender.class);

    @Override
    public void reply(String str) {
        LOGGER.info(str);
    }

    @Override
    public String name() {
        return "Console";
    }
}
