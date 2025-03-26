package net.vadamdev.dbk.framework.commands.annotations;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.*;

/**
 * @author VadamDev
 * @since 20/03/2025
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandProcessor {
    String subCommand();

    String group() default "";

    @Documented
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Permissions {
        Permission[] requiredPermissions();
        String missingPermissionsMessage() default "You dont have enough permissions to execute this command";
    }
}
