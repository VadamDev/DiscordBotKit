package net.vadamdev.dbk.framework.menu;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.requests.CompletedRestAction;
import net.dv8tion.jda.internal.utils.Checks;
import net.vadamdev.dbk.framework.interactive.InteractiveComponents;
import net.vadamdev.dbk.framework.interactive.api.registry.MessageRegistry;
import net.vadamdev.dbk.framework.utils.CachedMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 17/03/2025
 */
public class InteractiveComponentMenu extends AbstractMenu {
    protected final List<LayoutComponent> layoutComponents;
    private List<MessageRegistry<?>> componentsToRegister;

    private final Consumer<Message> invalidateAction;

    protected Collection<MessageEmbed> embeds;
    private CachedMessage message;

    protected InteractiveComponentMenu(long timeout, @Nullable TimeUnit unit, Collection<MessageEmbed> embeds, List<LayoutComponent> layoutComponents,
                                    List<MessageRegistry<?>> componentsToRegister, @Nullable Consumer<Message> invalidateAction) {
        super(timeout, unit);

        this.embeds = embeds;
        this.layoutComponents = layoutComponents;
        this.componentsToRegister = componentsToRegister;
        this.invalidateAction = invalidateAction;
    }

    @Override
    public RestAction<Message> display(Message message) {
        InteractiveComponents.findComponentManager(message.getJDA())
                .ifPresent(manager -> manager.invalidateMessageAttachedComponents(message.getIdLong()));

        return message.editMessageEmbeds(embeds).setReplace(true).setComponents(layoutComponents)
                .onSuccess(this::init);
    }

    @Override
    public RestAction<Message> display(MessageChannel channel, @Nullable String messageId) {
        if(messageId != null) {
            InteractiveComponents.findComponentManager(channel.getJDA())
                    .ifPresent(manager -> manager.invalidateMessageAttachedComponents(Long.parseLong(messageId)));

            return channel.editMessageEmbedsById(messageId, embeds).setComponents(layoutComponents)
                    .onSuccess(this::init);
        }

        return channel.sendMessageEmbeds(embeds).setComponents(layoutComponents)
                .onSuccess(this::init);
    }

    @Override
    public RestAction<Message> display(IReplyCallback callback, boolean ephemeral) {
        if(ephemeral)
            throw new UnsupportedOperationException("Ephemeral messages are not supported in InteractiveComponentMenu!");

        return callback.replyEmbeds(embeds).setComponents(layoutComponents)
                .flatMap(InteractionHook::retrieveOriginal)
                .onSuccess(this::init);
    }

    @Override
    public RestAction<Message> display(InteractionHook hook, boolean edit) {
        if(edit)
            return hook.editOriginalEmbeds(embeds).setComponents(layoutComponents).setReplace(true)
                    .onSuccess(this::init);
        else
            return hook.sendMessageEmbeds(embeds).setComponents(layoutComponents)
                    .onSuccess(this::init);
    }

    protected void init(Message message) {
        if(message.isEphemeral())
            throw new UnsupportedOperationException("Ephemeral messages are not supported in InteractiveComponentMenu!");

        jda = message.getJDA();
        this.message = new CachedMessage(message);

        componentsToRegister.forEach(registry -> registry.register(message));
        componentsToRegister = null;
    }

    public RestAction<Message> edit(@Nullable Consumer<List<LayoutComponent>> components, @Nullable MessageEmbed... embeds) {
        if(components == null && embeds == null)
            throw new IllegalArgumentException("At least one of the parameters must be not null!");

        return message.retrieveMessage()
                .flatMap(msg -> {
                    if((embeds != null && embeds.length > 0) && components != null) {
                        components.accept(layoutComponents);
                        this.embeds = Arrays.asList(embeds);

                        return msg.editMessageEmbeds(embeds).setComponents(layoutComponents);
                    }else if(embeds != null && embeds.length > 0) {
                        this.embeds = Arrays.asList(embeds);
                        return msg.editMessageEmbeds(embeds);
                    }else if(components != null) {
                        components.accept(layoutComponents);
                        return msg.editMessageComponents(layoutComponents);
                    }

                    return new CompletedRestAction<>(msg.getJDA(), null, new Error());
                });
    }

    public RestAction<Message> editEmbeds(MessageEmbed... embeds) {
        return edit(null, embeds);
    }

    public RestAction<Message> editComponents(Consumer<List<LayoutComponent>> components) {
        return edit(components);
    }

    public CachedMessage getCachedMessage() {
        return message;
    }

    @Override
    public void invalidate(JDA jda) {
        super.invalidate(jda);
        invalidateComponents(jda);

        if(invalidateAction != null)
            message.runIfExists(invalidateAction);
    }

    public void invalidateComponents(JDA jda) {
        InteractiveComponents.findComponentManager(jda).ifPresent(manager -> manager.invalidateMessageAttachedComponents(message.messageId()));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends BuilderBase<InteractiveComponentMenu, Builder> {
        private Builder() {}

        @Override
        public InteractiveComponentMenu build() {
            checkValid();
            return new InteractiveComponentMenu(timeout, unit, embeds, layoutComponents, componentsToRegister, invalidateAction);
        }
    }

    public abstract static class BuilderBase<T extends InteractiveComponentMenu, B extends BuilderBase<T, B>> extends AbstractMenu.Builder<T, B> {
        protected final List<MessageEmbed> embeds;
        protected final List<LayoutComponent> layoutComponents;
        protected final List<MessageRegistry<?>> componentsToRegister;

        protected Consumer<Message> invalidateAction;

        protected BuilderBase() {
            this.embeds = new ArrayList<>();
            this.layoutComponents = new ArrayList<>();
            this.componentsToRegister = new ArrayList<>();

            this.invalidateAction = InvalidateActions.DISABLE_COMPONENTS_ON_INVALIDATE;
        }

        public B addActionRow(ActionRow row) {
            layoutComponents.add(row);
            return (B) this;
        }

        public B addActionRow(ActionComponent... components) {
            return addActionRow(ActionRow.of(components));
        }

        public B addActionRow(MessageRegistry<? extends ActionComponent>... components) {
            final ActionComponent[] newComponents = new ActionComponent[components.length];
            for(int i = 0; i < components.length; i++) {
                final MessageRegistry<? extends ActionComponent> registry = components[i];

                newComponents[i] = registry.get();
                componentsToRegister.add(registry);
            }

            return addActionRow(newComponents);
        }

        public B addEmbed(@NotNull MessageEmbed embed, MessageEmbed... moreEmbeds) {
            embeds.add(embed);

            if(moreEmbeds.length > 0)
                embeds.addAll(List.of(moreEmbeds));

            return (B) this;
        }

        public B onInvalidate(@Nullable Consumer<Message> action) {
            if(action == null)
                invalidateAction = null;
            else {
                if(invalidateAction != null)
                    invalidateAction = invalidateAction.andThen(action);
                else
                    invalidateAction = action;
            }

            return (B) this;
        }

        protected void checkValid() {
            Checks.check(!embeds.isEmpty(), "There must be a least one embed in the menu!");
            Checks.check(embeds.size() <= 5, "There's a maximum of 5 embeds per message");
            Checks.check(!layoutComponents.isEmpty(), "There must be at least one layout component in the menu");
        }
    }
}
