package net.vadamdev.dbk.commands.annotations;

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.vadamdev.dbk.commands.api.CommandExecutor;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author VadamDev
 * @since 22/11/2025
 */
public class AnnotatedCommandDispatcher {
    private final CommandExecutor<?> command;
    private final List<CommandMethod> methods;

    public AnnotatedCommandDispatcher(CommandExecutor<?> command) {
        this.command = command;
        this.methods = new ArrayList<>();

        discoverCommands(command.getClass());
    }

    private void discoverCommands(Class<?> commandClass) {
        for(Method method : commandClass.getDeclaredMethods()) {
            final SubCommand subCommand = method.getAnnotation(SubCommand.class);
            if(subCommand == null)
                continue;

            final SubCommand.Permission permission = method.getAnnotation(SubCommand.Permission.class);
            methods.add(CommandMethod.of(method, subCommand, permission));
        }
    }

    public boolean onCommand(GenericCommandInteractionEvent event) {
        final CommandMethod commandMethod = findCommand(event.getSubcommandGroup(), event.getSubcommandName());
        if(commandMethod == null)
            return false;

        if(!commandMethod.hasPermission(event.getMember())) {
            final String missingPermMessage = commandMethod.permission().missingPermissionsMessage();
            if(missingPermMessage != null)
                event.reply(missingPermMessage).setEphemeral(true).queue();

            return true;
        }

        commandMethod.invoke(command, event);
        return true;
    }

    @Nullable
    private CommandMethod findCommand(@Nullable String commandGroup, String commandName) {
        for(CommandMethod subMethod : methods) {
            if(!commandName.equals(subMethod.name()))
                continue;

            final String group = subMethod.group();
            if((group != null && commandGroup == null) || (group == null && commandGroup != null))
                continue;

            if(commandGroup != null && !commandGroup.equals(group))
                continue;

            return subMethod;
        }

        return null;
    }
}
