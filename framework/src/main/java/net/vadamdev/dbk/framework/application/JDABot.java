package net.vadamdev.dbk.framework.application;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.hooks.EventListener;
import net.vadamdev.dbk.framework.commands.CommandHandler;
import net.vadamdev.dbk.framework.commands.SlashCommand;
import net.vadamdev.dbk.framework.commands.api.CommandExecutor;
import net.vadamdev.dbk.framework.interactive.InteractiveComponentManager;
import net.vadamdev.dbk.framework.interactive.InteractiveComponents;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * @author VadamDev
 * @since 26/10/2024
 */
public abstract class JDABot {
    private Supplier<JDABuilder> supplier;
    protected JDA jda;

    private CommandHandler commandHandler;

    private String avatarUrl, appName;

    public JDABot(Supplier<JDABuilder> supplier) {
        this.supplier = supplier;
    }

    protected abstract void onStart();
    protected abstract void onStop();

    public final void start() throws InterruptedException {
        jda = supplier.get().build();
        supplier = null;

        jda.awaitReady();

        commandHandler = new CommandHandler(jda);

        InteractiveComponents.registerManager(jda, new InteractiveComponentManager(jda));

        final SelfUser selfUser = jda.getSelfUser();
        avatarUrl = selfUser.getAvatarUrl();
        appName = selfUser.getName();

        onStart();

        commandHandler.registerCommandsAndClose();
    }

    public final void stop(boolean force) {
        InteractiveComponents.unregisterManager(jda);
        InteractiveComponents.shutdown();

        try {
            onStop();
        }catch (Exception e) {
            e.printStackTrace();
        }

        if(!force)
            jda.shutdown();
        else
            jda.shutdownNow();
    }

    /**
     * Register a JDA {@link EventListener}
     *
     * @param listeners Listeners that we want to register
     */
    protected void registerListeners(@NotNull Object... listeners) {
        jda.addEventListener(listeners);
    }

    /**
     * Register a variable amount of {@link SlashCommand}
     *
     * @param commands The variable number of {@link SlashCommand} to register
     */
    protected void registerCommands(@NotNull CommandExecutor<?>... commands) {
        for(CommandExecutor<?> executor : commands)
            commandHandler.registerCommand(executor);
    }

    /*
       Getters
     */

    public JDA getJDA() {
        return jda;
    }

    public String getAvatarURL() {
        return avatarUrl;
    }

    public String getAppName() {
        return appName;
    }
}
