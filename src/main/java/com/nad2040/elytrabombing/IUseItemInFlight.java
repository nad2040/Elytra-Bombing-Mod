package com.nad2040.elytrabombing;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public interface IUseItemInFlight {
    TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand);
}
