package net.jukitsu.combattest.mixin;


import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    public PlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(EntityType.PLAYER, level);
    }

    @Shadow public abstract float getAttackStrengthScale(float f);
    @Shadow public abstract void resetAttackStrengthTicker();


    @ModifyVariable(method="attack", at=@At("STORE"), ordinal=2)
    private float cancelAttackStrengthScale(float x) {
        return 1.0F;
    }

    @ModifyVariable(method="attack", at=@At("STORE"), ordinal=3)
    private boolean doSweepAttack(boolean x) {
        return x && getAttackStrengthScale(1.0F) > 0.95F && EnchantmentHelper.getSweepingDamageRatio(this) > 0.0F;
    }

    @Redirect(method="attack", at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/player/Player;resetAttackStrengthTicker()V"))
    public void cancelAttackReset(Player player) {

    }

    @Inject(method="attack", at = @At("TAIL"))
    public void resetAttackTime(Entity entity, CallbackInfo info) {
        this.resetAttackStrengthTicker();
    }

}
