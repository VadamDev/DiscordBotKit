package net.vadamdev.dbk;

import net.vadamdev.dbk.application.JDABot;
import net.vadamdev.dbk.application.annotations.AppConfig;
import net.vadamdev.dbk.application.annotations.Bot;
import net.vadamdev.dbk.application.console.ConsoleCommandManager;
import net.vadamdev.dbk.config.Configuration;
import net.vadamdev.dbk.config.loader.ConfigurationLoader;
import net.vadamdev.dbk.utils.Callable;
import org.jetbrains.annotations.Nullable;
import org.simpleyaml.configuration.file.YamlFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Core class of DBK, start and stop bots, initiate console commands, etc...
 *
 * @author VadamDev
 * @since 05/11/2025
 */
public final class DBKApplication {
    /**
     * Starts DBK for the given mainClass, and returns the DBKApplication instance. If logger is null, a default logger will be used.
     *
     * @param mainClass The main class of your program, where bots instances a present as static fields annotated with @Bot
     * @param logger The logger to use for logging, if null a default logger will be used
     * @return The created DBKApplication instance
     */
    public static DBKApplication run(Class<?> mainClass, @Nullable Logger logger) {
        if(logger == null)
            logger = LoggerFactory.getLogger(DBKApplication.class);

        final DBKApplication application = new DBKApplication(mainClass, logger);
        application.start();

        return application;
    }

    private final Class<?> mainClass;
    private final Logger logger;

    private List<JDABot> bots;
    private final ConsoleCommandManager consoleManager;

    private ScheduledExecutorService scheduledExecutorService;

    private boolean launched;
    @Nullable private Callable onShutdown;

    private DBKApplication(Class<?> mainClass, Logger logger) {
        this.mainClass = mainClass;
        this.logger = logger;

        this.consoleManager = new ConsoleCommandManager(System.in);
        this.consoleManager.addDefaultCommands(this);

        this.launched = false;
    }

    void start() {
        if(launched)
            throw new IllegalStateException("DBK Application is already running!");

        // Log DBK Header
        logger.info(DBKConstants.HEADER);

        // Load App Config
        try {
            loadAppConfiguration();
        }catch (Exception e) {
            logger.error("An error occurred while loading application config!", e);
            System.exit(0);
        }

        // Collect JDA Bots from main class
        try {
            bots = collectJDABots();
        }catch (IllegalAccessException | IllegalArgumentException e) {
            logger.error("An error occurred while collecting JDABots!", e);
        }finally {
            if(bots == null || bots.isEmpty()) {
                logger.error("-> Failed to find any JDABot!");
                System.exit(0);
            }
        }

        // Start mono thread scheduled executor
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        // Actually start JDA Bots
        launched = true;

        final boolean success = startBots(); // return false if 0 bots started
        if(!success) {
            scheduledExecutorService.shutdownNow();
            System.exit(0);
        }

        // Start console commands manager
        consoleManager.start();

        logger.info("Loaded successfully! Type \"help\" to see a list of available commands.");
    }

    /**
     * Stops the application, shutting down all bots and the console command manager.
     *
     * @throws IllegalStateException If the application is not running
     */
    public void stop() {
        if(!launched)
            throw new IllegalStateException("DBK Application is not running!");

        if(consoleManager.isRunning())
            consoleManager.stop();

        shutdownBots();

        if(onShutdown != null)
            onShutdown.run();

        scheduledExecutorService.shutdown();

        launched = false;
        System.exit(0);
    }

    /*
       Start Sequence
     */

    private void loadAppConfiguration() throws Exception {
        logger.info("Looking for app configurations...");

        boolean shouldExit = false, found = false;
        final StringBuilder exitReasons = new StringBuilder();

        for(Field field : mainClass.getDeclaredFields()) {
            // App config should be static
            if(!Modifier.isStatic(field.getModifiers()))
                continue;

            final AppConfig appConfig = field.getAnnotation(AppConfig.class);
            if(appConfig == null)
                continue;

            field.setAccessible(true);
            if(!(field.get(null) instanceof Configuration config))
                throw new IllegalArgumentException("Field " + field.getName() + " is annotated with @AppConfig but is not of type Configuration!");

            // Load app config
            final YamlFile yamlFile = config.getYamlFile();
            final boolean existedBefore = yamlFile.exists();
            ConfigurationLoader.loadConfiguration(config);

            logger.info("-> Loaded app configuration: " + yamlFile.getConfigurationFile().getName());

            // If it's the first time the app is launched, and the app config is set to exit on first launch, we exit the app after loading the config
            if(!existedBefore && appConfig.shouldExitOnFirstLaunch()) {
                exitReasons.append(appConfig.exitMessage() + "\n");
                shouldExit = true;
            }

            found = true;
        }

        if(!found)
            logger.info("-> No app configurations where found!");

        if(shouldExit) {
            logger.info(exitReasons.toString());
            System.exit(0);
        }
    }

    private List<JDABot> collectJDABots() throws IllegalAccessException, IllegalArgumentException {
        logger.info("Looking for JDABots...");

        final List<JDABot> result = new ArrayList<>();

        for(Field field : mainClass.getDeclaredFields()) {
            // Bot field should be static and annotated with @Bot
            if(!Modifier.isStatic(field.getModifiers()) || !field.isAnnotationPresent(Bot.class))
                continue;

            field.setAccessible(true);
            if(!(field.get(null) instanceof JDABot bot))
                throw new IllegalArgumentException("Field " + field.getName() + " is annotated with @Bot while not being an instance of JDABot !");

            result.add(bot);
        }

        return result;
    }

    private boolean startBots() {
        int started = 0, failed = 0;

        for(JDABot bot : bots) {
            final String botName = bot.getClass().getName();

            logger.info("-> Starting " + botName + "...");

            try {
                bot.start(this);
                started++;
            }catch(Exception e) {
                logger.error("An error occurred while starting " + botName + " :", e);
                failed++;
            }
        }

        logger.info("-> Started " + started + " of " + (started + failed) + " discord bot(s)");
        return started > 0;
    }

    /*
       Stop Sequence
     */

    private void shutdownBots() {
        for(JDABot bot : bots) {
            final String botClassName = bot.getClass().getName();

            try {
                logger.info("Stopping " + botClassName + "...");
                bot.shutdown();
            }catch(Exception e) {
                logger.error("An error occurred while stopping " + botClassName + ":", e);
            }
        }
    }

    /*
       Setters
     */

    /**
     * Sets the action to be executed when DBK is shutting down. This can be called multiple times to add multiple actions.
     * Note that the actions will be executed AFTER bots where shut down by DBK
     *
     * @param action The code to execute when DBK is shutting down
     * @return The current DBKApplication instance
     */
    public DBKApplication onShutdown(Callable action) {
        if(onShutdown == null)
            onShutdown = action;
        else
            onShutdown = onShutdown.andThen(action);

        return this;
    }

    /*
       Getters
     */

    public Logger getLogger() {
        return logger;
    }

    public ConsoleCommandManager getConsoleManager() {
        return consoleManager;
    }

    public boolean isLaunched() {
        return launched;
    }

    public ScheduledExecutorService getScheduledExecutorMonoThread() {
        return scheduledExecutorService;
    }
}
