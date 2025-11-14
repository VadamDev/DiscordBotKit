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
    private final String label;
    private String[] aliases;

    private String description;

    protected ConsoleCommand(String label) {
        this.label = label;
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
    protected final boolean match(String label) {
        for(String alias : aliases) {
            if(!label.equals(alias))
                continue;

            return true;
        }

        return label.equals(this.label);
    }

    /*
       Getters & Setters
     */

    public String getLabel() {
        return label;
    }

    @Nullable
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
        return Objects.equals(label, that.label) && Objects.deepEquals(aliases, that.aliases) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, Arrays.hashCode(aliases), description);
    }

    /*
       Fluent Pattern console commands
     */

    public static ConsoleCommand of(String label, @Nullable String description, BiConsumer<Sender, String[]> action) {
        return new Impl(label, description, action);
    }

    public static ConsoleCommand of(String label, BiConsumer<Sender, String[]> action) {
        return of(label, null, action);
    }

    private static final class Impl extends ConsoleCommand {
        private final BiConsumer<Sender, String[]> action;

        private Impl(String label, @Nullable String description, BiConsumer<Sender, String[]> action) {
            super(label);
            setDescription(description);

            this.action = action;
        }

        @Override
        public void execute(Sender sender, String label, String[] args) {
            action.accept(sender, args);
        }
    }
}
