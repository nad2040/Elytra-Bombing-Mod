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
			ItemStack item = user.getStackInHand(hand);
			Hand other_hand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
			Vec3d position,velocity;
			if (item.isOf(Items.FLINT_AND_STEEL)) {
				ItemStack tnt = user.getStackInHand(other_hand);
				if (!tnt.isOf(Items.TNT)) cir.setReturnValue(TypedActionResult.pass(user.getStackInHand(hand)));
				position = user.getPos();
				velocity = user.getVelocity();
				TntEntity tntEntity = new TntEntity(world, position.x, position.y, position.z, user);
				tntEntity.setVelocity(velocity.multiply(1.2));
				world.spawnEntity(tntEntity);
				world.playSound(null, tntEntity.getX(), tntEntity.getY(), tntEntity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
				world.emitGameEvent(user, GameEvent.PRIME_FUSE, position);
				if (!user.getAbilities().creativeMode) {
					item.damage(1, user, p -> p.sendToolBreakStatus(hand));
					tnt.decrement(1);
				}
				user.incrementStat(Stats.USED.getOrCreateStat((FlintAndSteelItem)(Object)this));
				cir.setReturnValue(TypedActionResult.success(user.getStackInHand(hand), world.isClient()));
			}
			else if (item.isOf(Items.ANVIL) || item.isOf(Items.CHIPPED_ANVIL) || item.isOf(Items.DAMAGED_ANVIL)) {
				if (!user.getStackInHand(other_hand).isEmpty()) cir.setReturnValue(TypedActionResult.pass(user.getStackInHand(hand)));
				position = user.getPos();
				velocity = user.getVelocity();
				FallingBlockEntity anvilEntity = null;
				if (item.isOf(Items.ANVIL)) anvilEntity = FallingBlockEntity.spawnFromBlock(world, new BlockPos(position), Blocks.ANVIL.getDefaultState());
				else if (item.isOf(Items.CHIPPED_ANVIL)) anvilEntity = FallingBlockEntity.spawnFromBlock(world, new BlockPos(position), Blocks.CHIPPED_ANVIL.getDefaultState());
				else if (item.isOf(Items.DAMAGED_ANVIL)) anvilEntity = FallingBlockEntity.spawnFromBlock(world, new BlockPos(position), Blocks.DAMAGED_ANVIL.getDefaultState());
				assert anvilEntity != null;
				anvilEntity.setVelocity(velocity.multiply(1.2));
				world.spawnEntity(anvilEntity);
				world.playSound(null, anvilEntity.getX(), anvilEntity.getY(), anvilEntity.getZ(), SoundEvents.BLOCK_ANVIL_FALL, SoundCategory.BLOCKS, 1.0f, 1.0f);
				if (!user.getAbilities().creativeMode) {
					item.decrement(1);
				}
			}
		}
		cir.setReturnValue(TypedActionResult.pass(user.getStackInHand(hand)));
	}
}
