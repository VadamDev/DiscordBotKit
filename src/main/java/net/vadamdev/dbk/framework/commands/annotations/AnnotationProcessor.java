package net.vadamdev.dbk.framework.commands.annotations;

import net.dv8tion.jda.annotations.ForRemoval;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.vadamdev.dbk.framework.commands.api.CommandExecutor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author VadamDev
 * @since 20/03/2025
 */
@Deprecated
public final class AnnotationProcessor {
    private AnnotationProcessor() {}

    public static void processAnnotations(SlashCommandInteractionEvent event, CommandExecutor<?> command) {
        final Class<?> clazz = command.getClass();

        final String subCommandName = event.getSubcommandName();
        if(subCommandName == null)
            return;

        final String subCommandGroupName = event.getSubcommandGroup();

        for(Method method : clazz.getDeclaredMethods()) {
            final CommandProcessor processor = method.getAnnotation(CommandProcessor.class);
            if(processor == null)
                continue;

            final String subCommand = processor.subCommand();
            if(!subCommandName.equals(subCommand))
                continue;

            final String commandGroup = !processor.group().isEmpty() ? processor.group() : null;

            if((commandGroup != null && subCommandGroupName == null) || (commandGroup == null && subCommandGroupName != null))
                continue;

            if(subCommandGroupName != null && !subCommandGroupName.equals(commandGroup))
                continue;

            final CommandProcessor.Permissions permissions = method.getAnnotation(CommandProcessor.Permissions.class);
            if(permissions != null && event.isFromGuild() && !event.getMember().hasPermission(permissions.requiredPermissions())) {
                final String missingPermMessage = permissions.missingPermissionsMessage();
                if(!missingPermMessage.isEmpty())
                    event.reply(missingPermMessage).setEphemeral(true).queue();

                break;
            }

            try {
                method.setAccessible(true);
                method.invoke(command, event);
            }catch (InvocationTargetException e) {
                e.printStackTrace();
            }catch(IllegalAccessException ignored) {}

            break;
        }
    }
}
