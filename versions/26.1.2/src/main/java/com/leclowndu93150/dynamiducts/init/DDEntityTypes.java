package com.leclowndu93150.dynamiducts.init;

import com.leclowndu93150.dynamiducts.DynamiDucts;
import com.leclowndu93150.dynamiducts.duct.transport.TransportEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DDEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, DynamiDucts.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<TransportEntity>> TRANSPORT =
            ENTITY_TYPES.register("transport", key -> EntityType.Builder.<TransportEntity>of(TransportEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .noSummon()
                    .fireImmune()
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, key)));
}
