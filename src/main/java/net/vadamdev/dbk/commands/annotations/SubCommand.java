package net.vadamdev.dbk.commands.annotations;

import java.lang.annotation.*;

/**
 * @author VadamDev
 * @since 20/03/2025
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubCommand {
    String group() default "";
    String name();

    @Documented
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Permission {
        net.dv8tion.jda.api.Permission[] requiredPermissions();
        String missingPermissionsMessage() default "You dont have enough permissions to execute this command";
    }
}
