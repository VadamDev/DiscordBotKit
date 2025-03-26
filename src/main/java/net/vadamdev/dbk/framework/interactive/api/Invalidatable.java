package net.vadamdev.dbk.framework.interactive.api;

import net.dv8tion.jda.api.JDA;

/**
 * @author VadamDev
 * @since 08/11/2024
 */
public interface Invalidatable {
    void invalidate(JDA jda);
}
