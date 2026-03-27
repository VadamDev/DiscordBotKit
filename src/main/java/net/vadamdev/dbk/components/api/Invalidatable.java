package net.vadamdev.dbk.components.api;

import net.dv8tion.jda.api.JDA;

/**
 * @author VadamDev
 * @since 24/03/2026
 */
public interface Invalidatable {
    void invalidate(JDA jda);
}
