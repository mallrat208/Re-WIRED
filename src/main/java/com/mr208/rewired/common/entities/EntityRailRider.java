package com.mr208.rewired.common.entities;

import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EntityRailRider extends EntityMinecartEmpty
{
	int derailCount = 0;

	public EntityRailRider(World worldIn)
	{
		super(worldIn);
	}

	public EntityRailRider(World worldIn, BlockPos pos)
	{
		super(worldIn, pos.getX(), pos.getY(), pos.getZ());
	}

	public EntityRailRider(World worldIn, double x, double y, double z)
	{
		super(worldIn, x, y, z);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if(!world.isRemote){
			if (!this.isBeingRidden())
				this.setDead();

			int j = MathHelper.floor(this.posX);
			int k = MathHelper.floor(this.posY);
			int l = MathHelper.floor(this.posZ);

			IBlockState blockState = world.getBlockState(new BlockPos(j, k, l));

			if (!BlockRailBase.isRailBlock(blockState))
			{
				derailCount++;
			} else
			{
				derailCount = 0;
			}

			if (derailCount > 10)
			{
				this.removePassengers();
			}
		}
	}
	
	@Nullable
	@Override
	public EntityItem entityDropItem(ItemStack stack, float offsetY)
	{
		return null;
	}
	
	@Override
	protected void moveDerailedMinecart()
	{
		super.moveDerailedMinecart();
	}

	@Override
	public void fall(float distance, float damageMultiplier)
	{
		super.fall(distance, damageMultiplier);
		this.removePassengers();
	}

	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	@Override
	public boolean shouldRiderSit()
	{
		return false;
	}

	@Override
	public double getMountedYOffset()
	{
		return 0.2D;
	}

	@Override
	public boolean shouldRenderInPass(int pass)
	{
		return false;
	}

	@Override
	public Type getType()
	{
		return Type.RIDEABLE;
	}

	@Override
	public boolean shouldDismountInWater(Entity rider)
	{
		return true;
	}
}
