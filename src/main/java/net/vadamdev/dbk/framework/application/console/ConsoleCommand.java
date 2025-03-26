package net.vadamdev.dbk.framework.application.console;

import net.vadamdev.dbk.framework.application.console.sender.Sender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Represents an abstract console command, see {@link net.vadamdev.dbk.framework.application.console.defaults.StopCommand StopCommand} for implementation example
 *
 * @author VadamDev
 * @since 27/10/2024
 */
public abstract class ConsoleCommand {
    private final String name;
    private String[] aliases;

    private String description;

    protected ConsoleCommand(String name) {
        this.name = name;
        this.aliases = new String[0];

        this.description = null;
    }

    /**
     * Executes the command with the given sender, label and arguments
     *
     * @param sender The sender of the command
     * @param label The label of the command
     * @param args The arguments of the command
     */
    public abstract void execute(Sender sender, String label, String[] args);

    /**
     * Tests if the given label matches the command's name or any of its aliases.
     *
     * @param label The label to test
     * @return {@code true} if the label matches the command's name or any of its aliases, {@code false} otherwise
     */
    final boolean test(String label) {
        for(String alias : aliases) {
            if(label.equals(alias))
                return true;
        }

        return label.equals(name);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    protected void setAliases(@NotNull String... aliases) {
        this.aliases = aliases;
    }

    protected void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConsoleCommand that)) return false;
        return Objects.equals(name, that.name) && Objects.deepEquals(aliases, that.aliases) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, Arrays.hashCode(aliases), description);
    }

    /*
       Fluent Pattern console commands
     */

    public static ConsoleCommand of(String name, String description, BiConsumer<Sender, String[]> action) {
        return new Impl(name, description, action);
    }

    public static ConsoleCommand of(String name, BiConsumer<Sender, String[]> action) {
        return of(name, null, action);
    }

    /*
       Fluent pattern implementation of ConsoleCommand
     */

    private static final class Impl extends ConsoleCommand {
        private final BiConsumer<Sender, String[]> action;

        private Impl(String name, String description, BiConsumer<Sender, String[]> action) {
            super(name);
            setDescription(description);

            this.action = action;
        }

        @Override
        public void execute(Sender sender, String label, String[] args) {
            action.accept(sender, args);
        }
    }
}
