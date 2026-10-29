package net.vadamdev.dbk.components.api;

import net.dv8tion.jda.api.JDA;

/**
 * Represents an object that can be invalidated
 *
 * @author VadamDev
 * @since 24/03/2026
 */
public interface Invalidatable {
    void invalidate(JDA jda);
}
