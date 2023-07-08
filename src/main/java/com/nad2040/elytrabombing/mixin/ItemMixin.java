package com.nad2040.elytrabombing.mixin;

import com.nad2040.elytrabombing.ElytraBombingMod;
import net.minecraft.block.Blocks;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
	@Inject(at = @At("HEAD"), method = "use", cancellable = true)
	public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
		if (user.isFallFlying()) {
			ElytraBombingMod.LOGGER.info("right click action detected");
			Hand other_hand = (hand == Hand.MAIN_HAND) ? Hand.OFF_HAND : Hand.MAIN_HAND;
			ElytraBombingMod.LOGGER.info("hand is " + ((hand == Hand.MAIN_HAND) ? "main hand" : "off hand"));
			ElytraBombingMod.LOGGER.info("other hand is " + ((other_hand == Hand.MAIN_HAND) ? "main hand" : "off hand"));

			ItemStack usedItemStack = user.getStackInHand(hand), otherItemStack = user.getStackInHand(other_hand);
			ElytraBombingMod.LOGGER.info("used item: " + usedItemStack);
			ElytraBombingMod.LOGGER.info("other item: " + otherItemStack);

			Vec3d position = user.getPos(), velocity = user.getVelocity();
			ElytraBombingMod.LOGGER.info("player pos: " + position);
			ElytraBombingMod.LOGGER.info("player vel: " + velocity);
			if (!world.isClient && usedItemStack.isOf(Items.FLINT_AND_STEEL) && otherItemStack.isOf(Items.TNT)) {
				TntEntity tntEntity = new TntEntity(world, position.x, position.y, position.z, user);
				tntEntity.setVelocity(velocity.multiply(1.2));
				world.spawnEntity(tntEntity);
				world.playSound(null, tntEntity.getX(), tntEntity.getY(), tntEntity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
				world.emitGameEvent(user, GameEvent.PRIME_FUSE, position);
				if (!user.getAbilities().creativeMode) {
					usedItemStack.damage(1, user, p -> p.sendToolBreakStatus(hand));
					otherItemStack.decrement(1);
				}
				user.incrementStat(Stats.USED.getOrCreateStat((FlintAndSteelItem) (Object) this));
				cir.setReturnValue(TypedActionResult.success(user.getStackInHand(hand), world.isClient()));
			} else if (!world.isClient && (usedItemStack.isOf(Items.ANVIL) || usedItemStack.isOf(Items.CHIPPED_ANVIL) || usedItemStack.isOf(Items.DAMAGED_ANVIL)) && otherItemStack.isEmpty()) {
				FallingBlockEntity anvilEntity = null;
				if (usedItemStack.isOf(Items.ANVIL))
					anvilEntity = FallingBlockEntity.spawnFromBlock(world, new BlockPos(ElytraBombingMod.VEC3D_TO_3I(position)), Blocks.ANVIL.getDefaultState());
				else if (usedItemStack.isOf(Items.CHIPPED_ANVIL))
					anvilEntity = FallingBlockEntity.spawnFromBlock(world, new BlockPos(ElytraBombingMod.VEC3D_TO_3I(position)), Blocks.CHIPPED_ANVIL.getDefaultState());
				else if (usedItemStack.isOf(Items.DAMAGED_ANVIL))
					anvilEntity = FallingBlockEntity.spawnFromBlock(world, new BlockPos(ElytraBombingMod.VEC3D_TO_3I(position)), Blocks.DAMAGED_ANVIL.getDefaultState());
				assert anvilEntity != null;
				anvilEntity.setVelocity(velocity.multiply(1.2));
				world.spawnEntity(anvilEntity);
				world.playSound(null, anvilEntity.getX(), anvilEntity.getY(), anvilEntity.getZ(), SoundEvents.BLOCK_ANVIL_FALL, SoundCategory.BLOCKS, 1.0f, 1.0f);
				if (!user.getAbilities().creativeMode) {
					usedItemStack.decrement(1);
				}
			}
		}
	}
}
