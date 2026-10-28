package com.leclowndu93150.dynamiducts.compat.jade;

import com.leclowndu93150.dynamiducts.block.DuctBlock;
import com.leclowndu93150.dynamiducts.blockentity.DuctBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class DDJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(DuctComponentProvider.INSTANCE, DuctBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(DuctComponentProvider.INSTANCE, DuctBlock.class);
        registration.registerBlockIcon(DuctComponentProvider.INSTANCE, DuctBlock.class);
    }
}
