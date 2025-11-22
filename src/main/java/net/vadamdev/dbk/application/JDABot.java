package net.vadamdev.dbk.application;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.vadamdev.dbk.DBKApplication;
import net.vadamdev.dbk.commands.CommandDispatcher;
import net.vadamdev.dbk.commands.SlashCommand;
import net.vadamdev.dbk.commands.api.CommandExecutor;
import net.vadamdev.dbk.interactive.InteractiveComponentManager;
import net.vadamdev.dbk.interactive.InteractiveComponents;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * @author VadamDev
 * @since 26/10/2024
 */
public abstract class JDABot {
    private Supplier<JDABuilder> jdaBuilder;
    protected JDA jda;

    private CommandDispatcher commandDispatcher;
    protected InteractiveComponentManager interactiveComponentManager;

    private String avatarUrl, appName;

    protected JDABot(Supplier<JDABuilder> jdaBuilder) {
        this.jdaBuilder = jdaBuilder;
    }

    protected abstract void onStart() throws Exception;
    protected abstract void onStop();

    public final void start(DBKApplication application) throws Exception {
        jda = jdaBuilder.get().build();
        jdaBuilder = null;

        jda.awaitReady();
        jda.listenOnce(ShutdownEvent.class).subscribe(event -> stop());

        commandDispatcher = new CommandDispatcher(jda);

        InteractiveComponents.registerManager(jda, interactiveComponentManager != null ? interactiveComponentManager : (interactiveComponentManager = new InteractiveComponentManager(jda, application)));

        final SelfUser selfUser = jda.getSelfUser();
        avatarUrl = selfUser.getAvatarUrl();
        appName = selfUser.getName();

        onStart();

        commandDispatcher.registerCommandsAndClose();
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
        if(jda != null)
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
     * Register a {@link CommandExecutor}
     *
     * @param command The {@link CommandExecutor} to register
     */
    protected void registerCommand(@NotNull CommandExecutor<?> command) {
        commandDispatcher.registerCommand(command);
    }

    /**
     * Register a variable amount of {@link CommandExecutor}
     *
     * @param commands The variable number of {@link SlashCommand} to register
     */
    protected void registerCommands(@NotNull CommandExecutor<?>... commands) {
        commandDispatcher.registerCommands(commands);
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
