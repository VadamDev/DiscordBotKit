package net.vadamdev.dbk.framework.application.annotations;

import java.lang.annotation.*;

/**
 * Notify DBKFramework that this field is a {@link net.vadamdev.dbk.framework.application.JDABot JDABot} ready to be started
 * <br> The field needs to be static and instanced before DBKFramework launches
 *
 * @author VadamDev
 * @since 26/10/2024
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Bot {}
