package net.vadamdev.dbk.framework.application.annotations;

import java.lang.annotation.*;

/**
 * Notify DBK that this field is a {@link net.vadamdev.dbk.framework.application.JDABot JDABot} ready to be started
 * <br> The field needs to be STATIC and INSTANCED before DBK launches
 *
 * @author VadamDev
 * @since 26/10/2024
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Bot {}
