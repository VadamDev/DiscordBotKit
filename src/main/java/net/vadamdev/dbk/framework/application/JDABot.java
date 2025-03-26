package net.vadamdev.dbk.framework.application;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
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
    protected InteractiveComponentManager interactiveComponentManager;

    private String avatarUrl, appName;

    protected JDABot(Supplier<JDABuilder> supplier) {
        this.supplier = supplier;
    }

    protected abstract void onStart() throws Exception;
    protected abstract void onStop();

    public void start() throws Exception {
        jda = supplier.get().build();
        supplier = null;

        jda.awaitReady();

        commandHandler = new CommandHandler(jda);

        InteractiveComponents.registerManager(jda, interactiveComponentManager != null ? interactiveComponentManager : (interactiveComponentManager = new InteractiveComponentManager(jda)));

        final SelfUser selfUser = jda.getSelfUser();
        avatarUrl = selfUser.getAvatarUrl();
        appName = selfUser.getName();

        onStart();

        commandHandler.registerCommandsAndClose();

        jda.listenOnce(ShutdownEvent.class).subscribe(event -> stop());
    }

    private void stop() {
        try {
            InteractiveComponents.unregisterManager(jda);

            onStop();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void shutdown() {
        jda.shutdown();
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
