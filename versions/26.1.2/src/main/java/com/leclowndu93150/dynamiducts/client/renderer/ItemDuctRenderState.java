package com.leclowndu93150.dynamiducts.client.renderer;

import net.minecraft.client.renderer.item.ItemStackRenderState;

import java.util.ArrayList;
import java.util.List;

public class ItemDuctRenderState extends DuctRenderState {

    public final List<TravelingItem> travelingItems = new ArrayList<>();
    public float spinAngle;

    public record TravelingItem(ItemStackRenderState item, float offsetX, float offsetY, float offsetZ) {
    }
}
