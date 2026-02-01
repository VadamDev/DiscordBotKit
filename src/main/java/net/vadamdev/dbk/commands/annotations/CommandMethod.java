package net.vadamdev.dbk.commands.annotations;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.vadamdev.dbk.commands.api.CommandExecutor;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author VadamDev
 * @since 22/11/2025
 */
public record CommandMethod(Method method, @Nullable String group, String name, @Nullable MethodPermission permission) {
    public static CommandMethod of(Method method, SubCommand subCommand, @Nullable SubCommand.Permission permission) {
        return new CommandMethod(
                method,
                !subCommand.group().isBlank() ? subCommand.group() : null,
                subCommand.name(),
                MethodPermission.of(permission)
        );
    }

    public void invoke(CommandExecutor<?> command, Object... args) {
        try {
            method.setAccessible(true);
            method.invoke(command, args);
        }catch(IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public boolean hasPermission(@Nullable Member member) {
        if(permission == null || member == null)
            return true;

        return member.hasPermission(permission.permissions());
    }

    public record MethodPermission(Permission[] permissions, @Nullable String missingPermissionsMessage) {
        public static MethodPermission of(@Nullable SubCommand.Permission permission) {
            if(permission == null)
                return null;

            return new MethodPermission(
                    permission.requiredPermissions(),
                    !permission.missingPermissionsMessage().isBlank() ? permission.missingPermissionsMessage() : null
            );
        }
    }
}
