package com.nad2040.elytrabombing.mixin;

import com.nad2040.elytrabombing.ElytraBombingMod;
import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin implements ElytraBombingMod.FBEInterface {
    @Shadow
    private BlockState field_7188; // Correct field name based on mappings

    @Override
    public void setBlock(BlockState block) {
        this.field_7188 = block; // Assign to the shadowed field
    }
}