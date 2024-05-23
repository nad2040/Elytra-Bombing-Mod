package com.nad2040.elytrabombing.mixin;

import com.nad2040.elytrabombing.ElytraBombingMod;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
	@Inject(at = @At("TAIL"), method = "use", cancellable = true)
	public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
		if (user.isFallFlying()) {
			Hand other_hand = (hand == Hand.MAIN_HAND) ? Hand.OFF_HAND : Hand.MAIN_HAND;
			ItemStack usedItemStack = user.getStackInHand(hand), otherItemStack = user.getStackInHand(other_hand);
			Vec3d position = user.getPos(), velocity = user.getVelocity();
			if (ElytraBombingMod.SHOULD_LOG && !world.isClient) {
				ElytraBombingMod.log(hand,other_hand,usedItemStack,otherItemStack,position,velocity);
			}
			if (usedItemStack.isOf(Items.FLINT_AND_STEEL) && otherItemStack.isOf(Items.TNT)) {
				TntEntity tntEntity = new TntEntity(world, position.x, position.y, position.z, user);
				tntEntity.setVelocity(velocity.multiply(1.2));
				world.spawnEntity(tntEntity);
				world.playSound(null, tntEntity.getX(), tntEntity.getY(), tntEntity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
				world.emitGameEvent(user, GameEvent.PRIME_FUSE, position);
				if (!user.getAbilities().creativeMode) {
					usedItemStack.damage(1, user, EquipmentSlot.MAINHAND);
					otherItemStack.decrement(1);
				}
				user.incrementStat(Stats.USED.getOrCreateStat((FlintAndSteelItem) (Object) this));
				cir.setReturnValue(TypedActionResult.success(user.getStackInHand(hand), world.isClient()));
			} else if ((usedItemStack.isOf(Items.ANVIL) || usedItemStack.isOf(Items.CHIPPED_ANVIL) || usedItemStack.isOf(Items.DAMAGED_ANVIL)) && otherItemStack.isEmpty()) {
				FallingBlockEntity anvilEntity = new FallingBlockEntity(EntityType.FALLING_BLOCK, world);
				anvilEntity.setPosition(position);
				anvilEntity.setVelocity(velocity.multiply(1.2));
				if (usedItemStack.isOf(Items.ANVIL)) 		 ((ElytraBombingMod.FBEInterface) anvilEntity).setBlock(Blocks.ANVIL.getDefaultState());
				if (usedItemStack.isOf(Items.CHIPPED_ANVIL)) ((ElytraBombingMod.FBEInterface) anvilEntity).setBlock(Blocks.CHIPPED_ANVIL.getDefaultState());
				if (usedItemStack.isOf(Items.DAMAGED_ANVIL)) ((ElytraBombingMod.FBEInterface) anvilEntity).setBlock(Blocks.DAMAGED_ANVIL.getDefaultState());
				anvilEntity.setFallingBlockPos(anvilEntity.getBlockPos());
				world.spawnEntity(anvilEntity);
				world.playSound(null, anvilEntity.getX(), anvilEntity.getY(), anvilEntity.getZ(), SoundEvents.BLOCK_ANVIL_FALL, SoundCategory.BLOCKS, 1.0f, 1.0f);
				if (!user.getAbilities().creativeMode) {
					usedItemStack.decrement(1);
				}
				cir.setReturnValue(TypedActionResult.success(user.getStackInHand(hand), world.isClient()));
			}
		}
	}
}
