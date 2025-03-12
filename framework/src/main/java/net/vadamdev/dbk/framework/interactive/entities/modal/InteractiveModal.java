package net.vadamdev.dbk.framework.interactive.entities.modal;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.internal.interactions.modal.ModalImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.vadamdev.dbk.framework.interactive.InteractiveComponents;
import net.vadamdev.dbk.framework.interactive.api.Invalidatable;
import net.vadamdev.dbk.framework.interactive.api.components.Expirable;
import net.vadamdev.dbk.framework.interactive.api.components.InteractiveComponent;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * @author VadamDev
 * @since 08/11/2024
 */
public class InteractiveModal extends ModalImpl implements InteractiveComponent<ModalInteractionEvent>, Expirable {
    /**
     * Creates a new fixed InteractiveModal which is meant to be used in a static final environment
     * You must add at least one component to a modal before building it.
     *
     * @param jda JDA instance associated with this component
     * @param  customId The custom id for this modal
     * @param  title The title for this modal
     *
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the provided customId or title are null, empty, or blank</li>
     *             <li>If the provided customId is longer than {@value MAX_ID_LENGTH} characters</li>
     *             <li>If the provided title is longer than {@value #MAX_TITLE_LENGTH} characters</li>
     *         </ul>
     *
     * @return {@link Modal.Builder Builder} instance to customize this modal further
     */
    @NotNull
    @CheckReturnValue
    public static InteractiveModal.Builder fixed(@NotNull JDA jda, @NotNull String customId, @NotNull String title) {
        return new InteractiveModal.Builder(jda, customId, title);
    }

    /**
     * Creates a new dynamic InteractiveModal which is meant to be used inside functions. ModalId is automatically assigned and unique.
     * You must add at least one component to a modal before building it.
     *
     * @param jda JDA instance associated with this component
     * @param title The title for this modal
     * @return {@link Modal.Builder Builder} instance to customize this modal further
     */
    @NotNull
    @CheckReturnValue
    public static InteractiveModal.Builder dynamic(@NotNull JDA jda, @NotNull String title) {
        return new InteractiveModal.Builder(jda, InteractiveComponent.generateComponentUID(), title).invalidateOnCatch();
    }

    private final BiConsumer<ModalInteractionEvent, Invalidatable> action;
    private final boolean shouldInvalidateOnCatch;
    private final long longevity, bornTime;

    protected InteractiveModal(String id, String title, List<LayoutComponent> components, BiConsumer<ModalInteractionEvent, Invalidatable> action, boolean shouldInvalidateOnCatch, long longevity) {
        super(id, title, components);

        this.action = action;
        this.shouldInvalidateOnCatch = shouldInvalidateOnCatch;
        this.longevity = longevity;
        this.bornTime = System.currentTimeMillis();
    }

    @Override
    public void execute(ModalInteractionEvent event) {
        action.accept(event, this);
    }

    @Override
    public boolean isValidFor(ModalInteractionEvent event) {
        return getId().equals(event.getModalId());
    }

    @Override
    public Class<ModalInteractionEvent> getClassType() {
        return ModalInteractionEvent.class;
    }

    @Override
    public boolean shouldInvalidateOnCatch() {
        return shouldInvalidateOnCatch;
    }

    @Override
    public long getLifetimeMs() {
        return System.currentTimeMillis() - bornTime;
    }

    @Override
    public long getLongevity() {
        return longevity;
    }

    /*
       Builder
     */

    public static class Builder extends Modal.Builder {
        private final JDA jda;

        private BiConsumer<ModalInteractionEvent, Invalidatable> action;
        private boolean shouldInvalidateOnCatch = false;
        private long longevity = Expirable.DEFAULT_LONGEVITY;

        protected Builder(JDA jda, String customId, String title) {
            super(customId, title);

            this.jda = jda;
        }

        @NotNull
        public Builder invalidateOnCatch() {
            shouldInvalidateOnCatch = true;
            return this;
        }

        @NotNull
        public Builder action(BiConsumer<ModalInteractionEvent, Invalidatable> action) {
            if(this.action == null)
                this.action = action;
            else
                this.action = this.action.andThen(action);

            return this;
        }

        @NotNull
        public Builder longevity(long duration, TimeUnit unit) {
            return longevity(unit.toMillis(duration));
        }

        @NotNull
        public Builder longevity(long durationMs) {
            if(durationMs < 0)
                throw new IllegalArgumentException("Longevity cannot be below zero !");

            this.longevity = durationMs;
            return this;
        }

        @Override
        @NotNull
        public Modal build() {
            final List<LayoutComponent> components = getComponents();

            Checks.check(!components.isEmpty(), "Cannot make a modal without components!");
            Checks.check(components.size() <= MAX_COMPONENTS, "Cannot make a modal with more than 5 components!");
            Checks.check(action != null, "Cannot make a Interactive Modal without an action!");

            if(shouldInvalidateOnCatch && longevity == Expirable.DEFAULT_LONGEVITY)
                longevity = 0;

            final InteractiveModal modal = new InteractiveModal(getId(), getTitle(), components, action, shouldInvalidateOnCatch, longevity);
            InteractiveComponents.registerComponent(jda, modal);

            return modal;
        }

        /*
           Override default methods to return self
         */

        @Override
        @NotNull
        public Builder setId(@NotNull String customId) {
            super.setId(customId);
            return this;
        }

        @Override
        @NotNull
        public Builder setTitle(@NotNull String title) {
            super.setTitle(title);
            return this;
        }

        @Override
        @NotNull
        public Builder addComponents(LayoutComponent @NotNull... components) {
            super.addComponents(components);
            return this;
        }

        @Override
        @NotNull
        public Builder addComponents(@NotNull Collection<? extends LayoutComponent> components) {
            super.addComponents(components);
            return this;
        }

        @Override
        @NotNull
        public Builder addActionRow(@NotNull Collection<? extends ItemComponent> components) {
            super.addActionRow(components);
            return this;
        }

        @Override
        @NotNull
        public Builder addActionRow(ItemComponent @NotNull... components) {
            super.addActionRow(components);
            return this;
        }
    }
}
