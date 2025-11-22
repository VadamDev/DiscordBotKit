package net.vadamdev.dbk.application.annotations;

import net.vadamdev.dbk.application.JDABot;

import java.lang.annotation.*;

/**
 * Notify DBK that this field is a {@link JDABot JDABot} ready to be started
 * <br> The field needs to be STATIC and INSTANCED before DBK launches
 *
 * @author VadamDev
 * @since 26/10/2024
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Bot {}
