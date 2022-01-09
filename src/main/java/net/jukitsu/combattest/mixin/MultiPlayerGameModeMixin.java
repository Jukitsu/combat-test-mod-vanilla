package net.jukitsu.combattest.mixin;


import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @Overwrite
    public boolean hasMissTime()
    {
        return false;
    }

}
