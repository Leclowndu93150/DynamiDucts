package com.leclowndu93150.dynamiducts.core.attachment;

public record AttachmentTier(
        int index,
        String name,
        int filterSlots,
        int stackSize,
        int tickRate,
        int fluidDrainAmount,
        int speedBoost,
        boolean multiStack
) {

    public static final AttachmentTier BASIC = new AttachmentTier(0, "basic", 3, 8, 60, 60, 1, false);
    public static final AttachmentTier HARDENED = new AttachmentTier(1, "hardened", 6, 16, 40, 90, 1, false);
    public static final AttachmentTier REINFORCED = new AttachmentTier(2, "reinforced", 9, 32, 20, 120, 1, false);
    public static final AttachmentTier SIGNALUM = new AttachmentTier(3, "signalum", 12, 64, 10, 180, 2, true);
    public static final AttachmentTier RESONANT = new AttachmentTier(4, "resonant", 15, 64, 10, 240, 3, true);

    public static final AttachmentTier[] TIERS = {BASIC, HARDENED, REINFORCED, SIGNALUM, RESONANT};

    public static AttachmentTier byIndex(int index) {
        if (index < 0 || index >= TIERS.length) return BASIC;
        return TIERS[index];
    }
}
