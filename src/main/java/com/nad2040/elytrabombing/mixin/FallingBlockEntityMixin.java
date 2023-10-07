package com.nad2040.elytrabombing.mixin;

import com.nad2040.elytrabombing.ElytraBombingMod;
import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin implements ElytraBombingMod.FBEInterface {
    @Shadow
    private BlockState block;

    public void setBlock(BlockState block) {
        this.block = block;
    }
}
