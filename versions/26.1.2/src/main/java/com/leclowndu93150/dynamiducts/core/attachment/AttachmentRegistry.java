package com.leclowndu93150.dynamiducts.core.attachment;

import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.ValueInput;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class AttachmentRegistry {

    private static final Map<Identifier, AttachmentEntry> REGISTRY = new LinkedHashMap<>();

    public static void register(Identifier id, AttachmentFactory factory) {
        REGISTRY.put(id, new AttachmentEntry(id, factory));
    }

    public static void register(Identifier id, BiFunction<DuctBlockEntity, Direction, Attachment> factory) {
        register(id, (parent, side, input) -> factory.apply(parent, side));
    }

    public static Attachment create(Identifier id, DuctBlockEntity parent, Direction side, ValueInput input) {
        AttachmentEntry entry = REGISTRY.get(id);
        if (entry == null) return null;
        return entry.factory.create(parent, side, input);
    }

    public static Map<Identifier, AttachmentEntry> getAll() {
        return Collections.unmodifiableMap(REGISTRY);
    }

    @FunctionalInterface
    public interface AttachmentFactory {
        Attachment create(DuctBlockEntity parent, Direction side, ValueInput input);
    }

    public record AttachmentEntry(Identifier id, AttachmentFactory factory) {
    }
}
