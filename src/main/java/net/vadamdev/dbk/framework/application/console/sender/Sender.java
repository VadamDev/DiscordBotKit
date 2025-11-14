package net.vadamdev.dbk.framework.application.console.sender;

import net.vadamdev.dbk.framework.application.console.ConsoleCommand;

/**
 * Represents the sender of a {@link ConsoleCommand ConsoleCommand}
 *
 * @author VadamDev
 * @since 27/10/2024
 */
public interface Sender {
    /**
     * Reply text to the sender who sent the command
     *
     * @param str Text to reply
     */
    void reply(String str);

    /**
     * Return the name of the Sender (e.g., Console)
     *
     * @return The name of the sender as a String
     */
    String name();
}
