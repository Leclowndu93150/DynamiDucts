package com.leclowndu93150.dynamiducts.init;

import com.leclowndu93150.dynamiducts.attachment.filter.FilterFluid;
import com.leclowndu93150.dynamiducts.attachment.filter.FilterItem;
import com.leclowndu93150.dynamiducts.attachment.relay.Relay;
import com.leclowndu93150.dynamiducts.attachment.retriever.RetrieverFluid;
import com.leclowndu93150.dynamiducts.attachment.retriever.RetrieverItem;
import com.leclowndu93150.dynamiducts.attachment.servo.ServoFluid;
import com.leclowndu93150.dynamiducts.attachment.servo.ServoItem;
import com.leclowndu93150.dynamiducts.core.attachment.AttachmentRegistry;
import com.leclowndu93150.dynamiducts.core.attachment.AttachmentTier;
import net.minecraft.world.level.storage.ValueInput;

public final class DDAttachments {

    private static boolean initialized;

    private DDAttachments() {
    }

    public static void bootstrap() {
        if (initialized) {
            return;
        }
        initialized = true;

        AttachmentRegistry.register(Relay.ID, (parent, side, input) -> new Relay(parent, side));

        AttachmentRegistry.register(ServoItem.ID, (parent, side, input) -> new ServoItem(parent, side, tierFromInput(input)));
        AttachmentRegistry.register(ServoFluid.ID, (parent, side, input) -> new ServoFluid(parent, side, tierFromInput(input)));
        AttachmentRegistry.register(FilterItem.ID, (parent, side, input) -> new FilterItem(parent, side, tierFromInput(input)));
        AttachmentRegistry.register(FilterFluid.ID, (parent, side, input) -> new FilterFluid(parent, side, tierFromInput(input)));
        AttachmentRegistry.register(RetrieverItem.ID, (parent, side, input) -> new RetrieverItem(parent, side, tierFromInput(input)));
        AttachmentRegistry.register(RetrieverFluid.ID, (parent, side, input) -> new RetrieverFluid(parent, side, tierFromInput(input)));
    }

    private static AttachmentTier tierFromInput(ValueInput input) {
        return input.getInt("TierIndex").map(AttachmentTier::byIndex).orElse(AttachmentTier.BASIC);
    }
}
