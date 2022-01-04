package net.jukitsu.combattest.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class Cleaving extends Enchantment {
   public Cleaving() {
        super(Rarity.UNCOMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    //placeholder for until the identifier issue gets resolved

    public int getMinCost(int i) {
        return 5 + (i - 1) * 20;
    }

    public int getMaxCost(int i) {
        return this.getMinCost(i) + 20;
    }

    public int getMaxLevel() {
        return 3;
    }

    public float getDamageBonus(int i, LivingEntity livingEntity) {
        return (float)(1 + i);
    }

    public boolean checkCompatibility(Enchantment enchantment) {
        return !(enchantment instanceof DamageEnchantment) && super.checkCompatibility(enchantment);
    }
}
