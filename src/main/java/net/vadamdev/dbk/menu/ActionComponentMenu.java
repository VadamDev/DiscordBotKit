package net.vadamdev.dbk.menu;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.requests.CompletedRestAction;
import net.dv8tion.jda.internal.utils.Checks;
import net.vadamdev.dbk.components.SmartComponents;
import net.vadamdev.dbk.components.api.registry.MessageRegistry;
import net.vadamdev.dbk.utils.CachedMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 27/03/2026
 */
public class ActionComponentMenu extends AbstractMenu {
    protected Collection<MessageEmbed> embeds;

    protected final List<MessageTopLevelComponent> components;
    protected List<MessageRegistry<?>> smartComponentsToRegister;

    @Nullable private final Consumer<Message> invalidateAction;
    private CachedMessage cachedMessage;

    protected ActionComponentMenu(long timeout, @Nullable TimeUnit unit, @Nullable ScheduledExecutorService scheduler, Collection<MessageEmbed> embeds, List<MessageTopLevelComponent> components,
                                  List<MessageRegistry<?>> smartComponentsToRegister, @Nullable Consumer<Message> invalidateAction) {
        super(timeout, unit, scheduler);

        this.embeds = embeds;

        this.components = components;
        this.smartComponentsToRegister = smartComponentsToRegister;

        this.invalidateAction = invalidateAction;
    }

    protected ActionComponentMenu(Collection<MessageEmbed> embeds, List<MessageTopLevelComponent> components, List<MessageRegistry<?>> smartComponentsToRegister, @Nullable Consumer<Message> invalidateAction) {
        this.embeds = embeds;

        this.components = components;
        this.smartComponentsToRegister = smartComponentsToRegister;

        this.invalidateAction = invalidateAction;
    }

    @Override
    public RestAction<Message> display(Message message) {
        SmartComponents.findComponentManager(message.getJDA())
                .ifPresent(manager -> manager.invalidateMessageAttachedComponents(message.getId()));

        return message.editMessageEmbeds(embeds).setReplace(true).setComponents(components)
                .onSuccess(this::init);
    }

    @Override
    public RestAction<Message> display(MessageChannel channel, @Nullable String messageId) {
        if(messageId != null) {
            SmartComponents.findComponentManager(channel.getJDA())
                    .ifPresent(manager -> manager.invalidateMessageAttachedComponents(messageId));

            return channel.editMessageEmbedsById(messageId, embeds).setComponents(components)
                    .onSuccess(this::init);
        }

        return channel.sendMessageEmbeds(embeds).setComponents(components)
                .onSuccess(this::init);
    }

    @Override
    public RestAction<Message> display(IReplyCallback callback, boolean ephemeral) {
        if(ephemeral)
            throw new UnsupportedOperationException("Ephemeral messages are not supported in InteractiveComponentMenu!");

        return callback.replyEmbeds(embeds).setComponents(components)
                .flatMap(InteractionHook::retrieveOriginal)
                .onSuccess(this::init);
    }

    @Override
    public RestAction<Message> display(InteractionHook hook, boolean edit) {
        if(edit)
            return hook.editOriginalEmbeds(embeds).setComponents(components).setReplace(true)
                    .onSuccess(this::init);
        else
            return hook.sendMessageEmbeds(embeds).setComponents(components)
                    .onSuccess(this::init);
    }

    protected void init(Message message) {
        if(message.isEphemeral())
            throw new UnsupportedOperationException("Ephemeral messages are not supported in InteractiveComponentMenu!");

        jda = message.getJDA();
        cachedMessage = new CachedMessage(message);

        smartComponentsToRegister.forEach(registry -> registry.register(message));

        smartComponentsToRegister.clear();
        smartComponentsToRegister = null;
    }

    public RestAction<Message> edit(@Nullable Consumer<List<MessageTopLevelComponent>> components, @Nullable MessageEmbed... embeds) {
        if(components == null && embeds == null)
            throw new IllegalArgumentException("At least one of the parameters must be not null!");

        return cachedMessage.retrieveMessage()
                .flatMap(msg -> {
                    if((embeds != null && embeds.length > 0) && components != null) {
                        components.accept(this.components);
                        this.embeds = Arrays.asList(embeds);

                        return msg.editMessageEmbeds(embeds).setComponents(this.components);
                    }else if(embeds != null && embeds.length > 0) {
                        this.embeds = Arrays.asList(embeds);
                        return msg.editMessageEmbeds(embeds);
                    }else if(components != null) {
                        components.accept(this.components);
                        return msg.editMessageComponents(this.components);
                    }

                    return new CompletedRestAction<>(msg.getJDA(), null, new Error());
                });
    }

    public RestAction<Message> editEmbeds(MessageEmbed... embeds) {
        return edit(null, embeds);
    }

    public RestAction<Message> editComponents(Consumer<List<MessageTopLevelComponent>> components) {
        return edit(components);
    }

    public CachedMessage getCachedMessage() {
        return cachedMessage;
    }

    @Override
    public void invalidate(JDA jda) {
        super.invalidate(jda);
        invalidateComponents(jda);

        if(invalidateAction != null)
            cachedMessage.runIfExists(invalidateAction);
    }

    public void invalidateComponents(JDA jda) {
        SmartComponents.findComponentManager(jda).ifPresent(manager -> manager.invalidateMessageAttachedComponents(cachedMessage.messageId()));
    }

    /*
       Builder
     */

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends AbstractMenu.Builder<ActionComponentMenu, Builder> {
        protected final List<MessageEmbed> embeds;
        protected final List<MessageTopLevelComponent> components;
        protected final List<MessageRegistry<?>> smartComponentsToRegister;

        @Nullable protected Consumer<Message> invalidateAction;

        protected Builder() {
            this.embeds = new ArrayList<>();
            this.components = new ArrayList<>();
            this.smartComponentsToRegister = new ArrayList<>();

            this.invalidateAction = MenuInvalidateActions.DISABLE_COMPONENTS;
        }

        public Builder addActionRow(ActionRow row) {
            components.add(row);
            return this;
        }

        public Builder addActionRow(ActionRowChildComponent... components) {
            return addActionRow(ActionRow.of(Arrays.asList(components)));
        }

        public Builder addActionRow(MessageRegistry<? extends ActionRowChildComponent>... components) {
            final ActionRowChildComponent[] newComponents = new ActionRowChildComponent[components.length];
            for(int i = 0; i < components.length; i++) {
                final MessageRegistry<? extends ActionRowChildComponent> registry = components[i];

                newComponents[i] = registry.get();
                smartComponentsToRegister.add(registry);
            }

            return addActionRow(newComponents);
        }

        public Builder addEmbed(@NotNull MessageEmbed embed, MessageEmbed... moreEmbeds) {
            embeds.add(embed);

            if(moreEmbeds.length > 0)
                embeds.addAll(List.of(moreEmbeds));

            return this;
        }

        public Builder onInvalidate(@Nullable Consumer<Message> action) {
            if(action == null)
                invalidateAction = null;
            else {
                if(invalidateAction != null)
                    invalidateAction = invalidateAction.andThen(action);
                else
                    invalidateAction = action;
            }

            return this;
        }

        protected void checkValid() {
            Checks.check(!embeds.isEmpty(), "There must be a least one embed in the menu!");
            Checks.check(embeds.size() <= 5, "There's a maximum of 5 embeds per message");
            Checks.check(!components.isEmpty(), "There must be at least one layout component in the menu");
        }

        @Override
        public ActionComponentMenu build() {
            checkValid();
            return new ActionComponentMenu(timeout, unit, scheduler, embeds, components, smartComponentsToRegister, invalidateAction);
        }
    }
}
