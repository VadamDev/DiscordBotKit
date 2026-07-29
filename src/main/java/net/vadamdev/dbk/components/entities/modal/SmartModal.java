package net.vadamdev.dbk.components.entities.modal;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.ModalTopLevelComponent;
import net.dv8tion.jda.api.components.tree.ComponentTree;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.internal.utils.Checks;
import net.vadamdev.dbk.components.SmartComponents;
import net.vadamdev.dbk.components.api.IAutoExpirable;
import net.vadamdev.dbk.components.api.Invalidatable;
import net.vadamdev.dbk.components.api.SmartComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * @author VadamDev
 * @since 26/03/2026
 */
public class SmartModal implements SmartComponent<ModalInteractionEvent>, IAutoExpirable {
    /*
       Class
     */

    private final String modalId;

    private final BiConsumer<ModalInteractionEvent, Invalidatable> action;
    private final boolean invalidateOnCatch;
    private final long longevity, bornTime;

    protected SmartModal(String modalId, BiConsumer<ModalInteractionEvent, Invalidatable> action, boolean invalidateOnCatch, long longevity) {
        this.modalId = modalId;

        this.action = action;
        this.invalidateOnCatch = invalidateOnCatch;
        this.longevity = longevity;
        this.bornTime = System.currentTimeMillis();
    }

    @Override
    public void execute(ModalInteractionEvent event) {
        action.accept(event, this);
    }

    @Override
    public boolean isValidFor(ModalInteractionEvent event) {
        return event.getModalId().equals(modalId);
    }

    @Override
    public Class<ModalInteractionEvent> getClassType() {
        return ModalInteractionEvent.class;
    }

    @Override
    public boolean shouldInvalidateOnCatch() {
        return invalidateOnCatch;
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

    public static Builder builder(String title) {
        return new Builder(title);
    }

    public static class Builder {
        private final Modal.Builder modalBuilder;

        private BiConsumer<ModalInteractionEvent, Invalidatable> action;
        private boolean invalidateOnCatch = true;
        private long longevity = IAutoExpirable.DEFAULT_LONGEVITY;

        protected Builder(String title) {
            this.modalBuilder = Modal.create(SmartComponent.newComponentUID(), title);
        }

        public Builder action(BiConsumer<ModalInteractionEvent, Invalidatable> action) {
            this.action = action;
            return this;
        }

        public Builder dontInvalidateOnCatch() {
            invalidateOnCatch = false;
            return this;
        }

        public Builder longevity(long duration, TimeUnit unit) {
            return longevity(unit.toMillis(duration));
        }

        public Builder longevity(long durationMs) {
            Checks.notNegative(durationMs, "Duration");

            this.longevity = durationMs;
            return this;
        }

        public Builder id(String modalId) {
            modalBuilder.setId(modalId);
            return this;
        }

        public Builder addComponents(@NotNull ModalTopLevelComponent... components) {
            modalBuilder.addComponents(components);
            return this;
        }

        public Builder addComponents(@NotNull Collection<? extends ModalTopLevelComponent> components) {
            modalBuilder.addComponents(components);
            return this;
        }

        public Builder addComponents(@NotNull ComponentTree<? extends ModalTopLevelComponent> tree) {
            modalBuilder.addComponents(tree);
            return this;
        }

        public Modal build(JDA jda) {
            Checks.notNull(action, "Action");

            final Modal modal = modalBuilder.build();

            if(invalidateOnCatch && longevity == IAutoExpirable.DEFAULT_LONGEVITY)
                longevity = 0;

            SmartComponents.registerComponent(jda, new SmartModal(modal.getId(), action, invalidateOnCatch, longevity));

            return modal;
        }
    }
}
