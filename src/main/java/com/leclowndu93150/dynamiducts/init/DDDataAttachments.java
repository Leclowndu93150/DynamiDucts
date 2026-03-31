package com.leclowndu93150.dynamiducts.init;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;
import java.util.function.Supplier;

public class DDDataAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, DynamiDucts.MODID);

    public static final Supplier<AttachmentType<Optional<TransportTarget>>> TRANSPORT_TARGET =
            ATTACHMENT_TYPES.register("transport_target", () -> AttachmentType.<Optional<TransportTarget>>builder(
                    () -> Optional.empty()
            ).serialize(TransportTarget.OPTIONAL_CODEC).build());

    public record TransportTarget(BlockPos origin, BlockPos destination) {

        public static final Codec<TransportTarget> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPos.CODEC.fieldOf("origin").forGetter(TransportTarget::origin),
                BlockPos.CODEC.fieldOf("destination").forGetter(TransportTarget::destination)
        ).apply(instance, TransportTarget::new));

        public static final Codec<Optional<TransportTarget>> OPTIONAL_CODEC = CODEC.optionalFieldOf("target")
                .codec()
                .xmap(
                        opt -> opt,
                        opt -> opt
                );
    }
}
