package com.mr208.rewired.common.items.augments;

import com.mr208.rewired.common.entities.EntityRailRider;
import com.mr208.rewired.common.util.CyberwareHelper;
import flaxbeard.cyberware.api.CyberwareUpdateEvent;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemFootAugment extends ItemAugment
{
	public ItemFootAugment(String name, EnumSlot slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);
	}


	@Override
	public boolean hasMenu(ItemStack itemStack)
	{
		return itemStack.getItemDamage()==0;
	}

	@SubscribeEvent
	public void onCyberEntityUpdate(CyberwareUpdateEvent event)
	{
		EntityLivingBase entity = event.getEntityLiving();
		if(entity==null)
			return;

		if(CyberwareHelper.isAugmentAvailable(entity, new ItemStack(this,1,0)))
		{
			//If The augment is available and hte Entity is walking on a rail, mount them to the rail. Only if not riding another entity
			if(entity.isRiding())
				return;

			BlockPos entityPos = new BlockPos(MathHelper.floor(entity.posX), MathHelper.floor(entity.posY), MathHelper.floor(entity.posZ));
			IBlockState state = entity.world.getBlockState(entityPos);
			if(BlockRailBase.isRailBlock(state) && !entity.isSneaking() && !entity.world.isRemote)
			{
				BlockRailBase.EnumRailDirection railDirection = state.getBlock() instanceof BlockRailBase ? ((BlockRailBase)state.getBlock()).getRailDirection(entity.world, entityPos, state, null) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
				double offsetY = 0.1D;

				if (railDirection.isAscending())
					offsetY = 0.6D;

				EntityRailRider cart = new EntityRailRider(entity.world,
						(double)entityPos.getX() + 0.5D,
						(double)entityPos.getY() + 0.0625D + offsetY,
						(double)entityPos.getZ() + 0.5D);
				entity.world.spawnEntity(cart);
				entity.startRiding(cart);
			}

		}
		else
		{
			if(entity.isRiding() && entity.getRidingEntity() instanceof EntityRailRider)
			{
				entity.dismountRidingEntity();
			}
		}
	}
}

