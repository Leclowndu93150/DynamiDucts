package com.leclowndu93150.modular_networks.core.attachment;

public record AttachmentTier(
        int index,
        String name,
        int filterSlots,
        int stackSize,
        int tickRate,
        int fluidDrainAmount,
        float speedMultiplier
) {

    public static final AttachmentTier BASIC = new AttachmentTier(0, "basic", 3, 1, 40, 150, 1.0F);
    public static final AttachmentTier HARDENED = new AttachmentTier(1, "hardened", 6, 16, 40, 300, 1.0F);
    public static final AttachmentTier REINFORCED = new AttachmentTier(2, "reinforced", 9, 16, 20, 600, 2.0F);
    public static final AttachmentTier SIGNALUM = new AttachmentTier(3, "signalum", 12, 64, 10, 1200, 4.0F);
    public static final AttachmentTier RESONANT = new AttachmentTier(4, "resonant", 15, 64, 5, 3000, 8.0F);

    public static final AttachmentTier[] TIERS = {BASIC, HARDENED, REINFORCED, SIGNALUM, RESONANT};

    public static AttachmentTier byIndex(int index) {
        if (index < 0 || index >= TIERS.length) return BASIC;
        return TIERS[index];
    }
}
