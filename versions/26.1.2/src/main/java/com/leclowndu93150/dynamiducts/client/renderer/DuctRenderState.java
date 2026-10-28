package com.leclowndu93150.dynamiducts.client.renderer;

import com.leclowndu93150.dynamiducts.client.renderer.ccl.CCModel;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class DuctRenderState extends BlockEntityRenderState {

    public DuctBlockEntityRenderer.DuctTextures textures;
    public boolean itemRender;
    public int overlay = OverlayTexture.NO_OVERLAY;
    public int connectionMask;
    public final boolean[] connected = new boolean[6];
    public final boolean[] connectionDetail = new boolean[6];
    public final String[] attachmentTextures = new String[6];
    public final CCModel[] attachmentModels = new CCModel[6];
    public TextureAtlasSprite fluidSprite;
    public int fluidColor;
    public int fluidLevel;
}
