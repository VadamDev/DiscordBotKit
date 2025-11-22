package net.vadamdev.dbk.interactive.api.components;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 11/03/2025
 */
public interface PermissibleInteractiveComponent<T extends GenericInteractionCreateEvent> extends InteractiveComponent<T> {
    @Override
    default void execute(T event) {
        final Map<Permission, Consumer<T>> permissions = getRequiredPermissions();

        if(event.isFromGuild() && (permissions != null && !permissions.isEmpty())) {
            final Member member = event.getMember();

            for(Map.Entry<Permission, Consumer<T>> entry : permissions.entrySet()) {
                if(!member.hasPermission(entry.getKey())) {
                    entry.getValue().accept(event);
                    return;
                }
            }
        }

        executeWithRequiredPermissions(event);
    }

    void executeWithRequiredPermissions(T event);

    @Nullable
    Map<Permission, Consumer<T>> getRequiredPermissions();
}
