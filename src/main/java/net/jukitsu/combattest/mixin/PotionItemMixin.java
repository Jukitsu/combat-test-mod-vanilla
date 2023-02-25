package net.jukitsu.combattest.mixin;

import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(PotionItem.class)
public class PotionItemMixin {

    @Overwrite
    public int getUseDuration(ItemStack stack) {
        return 20;
    }
}
