package net.vadamdev.dbk.framework;

import net.vadamdev.dbk.framework.application.JDABot;
import net.vadamdev.dbk.framework.application.annotations.AppConfig;
import net.vadamdev.dbk.framework.application.annotations.Bot;
import net.vadamdev.dbk.framework.application.console.ConsoleCommandManager;
import net.vadamdev.dbk.framework.config.Configuration;
import net.vadamdev.dbk.framework.config.ConfigurationLoader;
import net.vadamdev.dbk.framework.tuple.ImmutablePair;
import net.vadamdev.dbk.framework.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author VadamDev
 * @since 26/10/2024
 */
public final class DBKFramework {
    private static final DBKFramework INSTANCE = new DBKFramework();

    public static void launch(Class<?> mainClass, @Nullable Logger log) {
        INSTANCE.internalLaunch(mainClass, log);
    }

    public static void stop() {
        INSTANCE.internalStop();
    }

    public static ScheduledExecutorService getScheduledExecutorMonoThread() {
        return INSTANCE.monoScheduledExecutor;
    }

    public static Logger getLogger() {
        return INSTANCE.getInternalLogger();
    }

    private static final Logger FALLBACK_LOGGER = LoggerFactory.getLogger(DBKFramework.class);
    private Logger logger;

    private boolean launched;

    private final ConsoleCommandManager consoleManager;
    private List<JDABot> bots;

    private ScheduledExecutorService monoScheduledExecutor;

    private DBKFramework() {
        this.launched = false;

        this.consoleManager = new ConsoleCommandManager(System.in);
        this.consoleManager.addDefaultCommands();
    }

    private void internalLaunch(Class<?> mainClass, @Nullable Logger log) {
        Thread.currentThread().setName("Main");

        logger = log != null ? log : FALLBACK_LOGGER;

        if(launched) {
            logger.error("Failed to start. DBKFramework is already running!");
            return;
        }

        logger.info(DBKConstants.HEADER);

        //Init executors
        monoScheduledExecutor = Executors.newSingleThreadScheduledExecutor();

        try {
            loadAppConfiguration(mainClass);
        } catch (IllegalAccessException | IOException e) {
            logger.error("An error occurred while loading app configuration !", e);
            System.exit(-1);

            return;
        }

        logger.info("Looking for JDABots...");

        try {
            bots = findJDABots(mainClass);
        }catch (IllegalAccessException e) {
            logger.error("-> An error occurred while collecting JDABots!", e);
            System.exit(-1);

            return;
        }

        if(!bots.isEmpty())
            logger.info("-> Found " + bots.size() + " JDA Bots !");
        else {
            monoScheduledExecutor.shutdown();
            System.exit(0);
            return;
        }

        launched = true;
        startBots(bots);

        consoleManager.start();

        logger.info("Loaded successfully! Type \"help\" to see a list of available commands.");
    }

    private void internalStop() {
        final Logger logger = getInternalLogger();

        if(!launched) {
            logger.warn("Failed to stop. DBKFramework is not running !");
            return;
        }

        consoleManager.stop();

        for(JDABot bot : bots) {
            try {
                bot.shutdown();
            }catch(Exception e) {
                final String botClassName = bot.getClass().getName();
                logger.error("An error occurred while stopping " + botClassName + ": ", e);
            }
        }

        //Shutdown executors
        monoScheduledExecutor.shutdown();

        launched = false;
        System.exit(0);
    }

    /*
       App Configuration
     */

    private void loadAppConfiguration(@NotNull Class<?> clazz) throws IllegalAccessException, NullPointerException, IOException {
        logger.info("Loading app configuration...");

        final Pair<Field, AppConfig> result = findAppConfig(clazz);
        if(result.isEmpty()) {
            logger.info("-> No app config where found!");
            return;
        }

        final Field field = result.getLeft();
        field.setAccessible(true);

        if(!(field.get(null) instanceof Configuration config)) {
            logger.error("-> Field " + field.getName() + " must be a instance of net.vadamdev.dbk.framework.config.Configuration");
            return;
        }

        final boolean existedBefore = config.getYamlFile().exists();
        ConfigurationLoader.loadConfiguration(config);

        logger.info("-> App configuration loaded successfully !");

        final AppConfig appConfig = result.getRight();
        if(!existedBefore && appConfig.shouldExitOnFirstLaunch()) {
            final String exitMessage = appConfig.exitMessage();
            if(exitMessage != null && !exitMessage.isBlank())
                logger.info(exitMessage);

            System.exit(0);
        }
    }

    private Pair<Field, AppConfig> findAppConfig(Class<?> clazz) {
        for(Field field : clazz.getDeclaredFields()) {
            final AppConfig appConfig = field.getAnnotation(AppConfig.class);
            if(appConfig == null)
                continue;

            return ImmutablePair.of(field, appConfig);
        }

        return ImmutablePair.empty();
    }

    /*
       Bots
     */

    private void startBots(List<JDABot> bots) {
        int started = 0, failed = 0;

        for(JDABot bot : bots) {
            try {
                logger.info("Starting " + bot.getClass().getSimpleName() + "...");

                bot.start();
                started++;
            }catch(Exception e) {
                logger.error("An error occurred while starting " + bot.getClass().getName() + ":", e);
                failed++;
            }
        }

        if(started == 0) {
            logger.info("No bots where found !");
            DBKFramework.stop();
        }else
            logger.info("-> Started " + started + " of " + (started + failed) + " discord bot(s) !");
    }

    private List<JDABot> findJDABots(Class<?> clazz) throws IllegalAccessException {
        final List<JDABot> result = new ArrayList<>();

        for(Field field : clazz.getDeclaredFields()) {
            if((field.getModifiers() & Modifier.STATIC) == 0)
                continue;

            if(!JDABot.class.isAssignableFrom(field.getType()) || !field.isAnnotationPresent(Bot.class))
                continue;

            field.setAccessible(true);
            result.add((JDABot) field.get(null));
        }

        return result;
    }

    /*
       Getters
     */

    public Logger getInternalLogger() {
        return logger != null ? logger : FALLBACK_LOGGER;
    }
}
