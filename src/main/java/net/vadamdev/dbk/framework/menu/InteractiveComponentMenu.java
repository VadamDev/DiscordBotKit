package net.vadamdev.dbk.framework.menu;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.requests.CompletedRestAction;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.vadamdev.dbk.framework.interactive.InteractiveComponents;
import net.vadamdev.dbk.framework.interactive.api.registry.MessageRegistry;
import net.vadamdev.dbk.framework.utils.CachedMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 17/03/2025
 */
public class InteractiveComponentMenu extends AbstractMenu {
    private final List<LayoutComponent> layoutComponents;
    private List<MessageRegistry<?>> componentsToRegister;

    private final Consumer<Message> invalidateAction;

    private Collection<MessageEmbed> embeds;
    private CachedMessage message;

    public InteractiveComponentMenu(long timeout, @Nullable TimeUnit unit, Collection<MessageEmbed> embeds, List<LayoutComponent> layoutComponents,
                                    List<MessageRegistry<?>> componentsToRegister, @Nullable Consumer<Message> invalidateAction) {
        super(timeout, unit);

        this.embeds = embeds;
        this.layoutComponents = layoutComponents;
        this.componentsToRegister = componentsToRegister;
        this.invalidateAction = invalidateAction;
    }

    @Override
    public RestAction<Message> display(MessageChannel channel) {
        return channel.sendMessageEmbeds(embeds).setComponents(layoutComponents)
                .onSuccess(this::init);
    }

    @Override
    public RestAction<Message> display(Message message) {
        InteractiveComponents.findComponentManager(message.getJDA())
                .ifPresent(manager -> manager.invalidateMessageAttachedComponents(message.getIdLong()));

        return message.editMessageEmbeds(embeds).setReplace(true).setComponents(layoutComponents)
                .onSuccess(this::init);
    }

    @Override
    public RestAction<Message> display(IReplyCallback callback, boolean ephemeral) {
        return callback.replyEmbeds(embeds).setEphemeral(ephemeral).setComponents(layoutComponents)
                .flatMap(InteractionHook::retrieveOriginal)
                .onSuccess(this::init);
    }

    protected void init(Message message) {
        this.jda = message.getJDA();
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

        InteractiveComponents.findComponentManager(jda).ifPresent(manager -> manager.invalidateMessageAttachedComponents(message.messageId()));

        if(invalidateAction != null)
            message.runIfExists(invalidateAction);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends AbstractMenu.Builder<InteractiveComponentMenu, Builder> {
        private final List<MessageEmbed> embeds;
        private final List<LayoutComponent> layoutComponents;
        private final List<MessageRegistry<?>> componentsToRegister;

        private Consumer<Message> invalidateAction;

        private Builder() {
            this.embeds = new ArrayList<>();
            this.layoutComponents = new ArrayList<>();
            this.componentsToRegister = new ArrayList<>();

            this.invalidateAction = DISABLE_COMPONENTS_ON_INVALIDATE;
        }

        public Builder addActionRow(ActionRow row) {
            layoutComponents.add(row);
            return this;
        }

        public Builder addActionRow(ActionComponent... components) {
            return addActionRow(ActionRow.of(components));
        }

        public Builder addActionRow(MessageRegistry<? extends ActionComponent>... components) {
            final ActionComponent[] newComponents = new ActionComponent[components.length];
            for(int i = 0; i < components.length; i++) {
                final MessageRegistry<? extends ActionComponent> registry = components[i];

                newComponents[i] = registry.get();
                componentsToRegister.add(registry);
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

        @Override
        public InteractiveComponentMenu build() {
            Checks.check(!embeds.isEmpty(), "There must be a least one embed in the menu!");
            Checks.check(embeds.size() <= 5, "There's a maximum of 5 embeds per message");
            Checks.check(!layoutComponents.isEmpty(), "There must be at least one layout component in the menu");

            return new InteractiveComponentMenu(timeout, unit, embeds, layoutComponents, componentsToRegister, invalidateAction);
        }
    }

    public static final Consumer<Message> DISABLE_COMPONENTS_ON_INVALIDATE = message -> {
        final List<ActionRow> newRows = new ArrayList<>();
        for(ActionRow actionRow : message.getActionRows()) {
            final List<ItemComponent> newComponents = new ArrayList<>();

            for(ItemComponent component : actionRow.getComponents()) {
                if(!(component instanceof ActionComponent actionComponent) || actionComponent.isDisabled())
                    newComponents.add(component);
                else
                    newComponents.add(actionComponent.asDisabled());
            }

            newRows.add(ActionRow.of(newComponents));
        }

        message.editMessageComponents(newRows).queue();
    };

    public static final Consumer<Message> DELETE_MESSAGE_ON_INVALIDATE = message -> message.delete().queue();
}
