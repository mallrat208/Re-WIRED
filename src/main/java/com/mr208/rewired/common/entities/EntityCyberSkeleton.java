package com.mr208.rewired.common.entities;

import com.mr208.rewired.common.Content;
import com.mr208.rewired.common.handlers.ConfigHandler.Equipment;
import com.mr208.rewired.common.util.CyberwareHelper;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUserDataImpl;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.CyberwareContent.ZombieItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityCyberSkeleton extends EntitySkeleton implements ICyberEntity
{

	private static final DataParameter<Integer> CYBER_VARIANT = EntityDataManager.<Integer>createKey(EntityCyberSkeleton.class, DataSerializers.VARINT);

	public boolean hasWare;
	private CyberwareUserDataImpl cyberware;
	protected EntityAINearestAttackableTarget aiNearestAttackableTarget;

	public EntityCyberSkeleton(World worldIn)
	{
		super(worldIn);
		cyberware = new CyberwareUserDataImpl();
		hasWare = false;
	}

	protected void entityInit()
	{
		super.entityInit();
		this.dataManager.register(CYBER_VARIANT, Integer.valueOf(0));
	}

	@Override
	public void onLivingUpdate()
	{

		if (!this.hasWare && !this.world.isRemote)
		{
			CyberwareHelper.addRandomCyberware(this);
			this.setHealth(this.getMaxHealth());
			hasWare = true;
		}

		super.onLivingUpdate();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound = super.writeToNBT(compound);
		compound.setBoolean("hasRandomWare", hasWare);

		if (hasWare)
		{
			NBTTagCompound comp = cyberware.serializeNBT();
			compound.setTag("ware", comp);
		}

		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);

		this.hasWare = compound.getBoolean("hasRandomWare");
		if (compound.hasKey("ware"))
		{
			cyberware.deserializeNBT(compound.getCompoundTag("ware"));
		}
	}

	@Override
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing)
	{
		if (capability == CyberwareAPI.CYBERWARE_CAPABILITY)
		{
			return (T) cyberware;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing)
	{
		return capability == CyberwareAPI.CYBERWARE_CAPABILITY || super.hasCapability(capability, facing);
	}

	//Based heavily off of Cyberware's EntityCyberZombie
	@Override
	protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier)
	{
		super.dropEquipment(wasRecentlyHit, lootingModifier);

		if (CyberwareConfig.KATANA && !this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).isEmpty() && this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem() == CyberwareContent.katana)
		{
			ItemStack itemStack = this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).copy();
			if (itemStack.isItemStackDamageable())
			{
				int i = Math.max(itemStack.getMaxDamage() - 25, 1);
				int j = itemStack.getMaxDamage() - this.rand.nextInt(this.rand.nextInt(i) + 1);
				if (j > i)
				{
					j = i;
				}
				if (j < i)
				{
					j = 1;
				}
				itemStack.setItemDamage(j);
			}

			this.entityDropItem(itemStack, 0.0F);
		}

		if (hasWare)
		{
			float rarity = Math.min(100, CyberwareConfig.DROP_RARITY + lootingModifier * 5F);
			if (world.rand.nextFloat() < (rarity / 100F))
			{
				List<ItemStack> allWares = new ArrayList<>();
				for (ICyberware.EnumSlot slot : ICyberware.EnumSlot.values())
				{
					NonNullList<ItemStack> stuff = cyberware.getInstalledCyberware(slot);
					for (ItemStack stack : stuff)
					{
						if (!stack.isEmpty())
							allWares.add(stack);
					}
				}

				allWares.removeAll(Collections.singleton(null));

				ItemStack drop = null;
				int count = 0;
				while (count < 50 && (drop == null || drop.getItem() == CyberwareContent.creativeBattery || drop.getItem() == CyberwareContent.bodyPart))
				{
					int random = world.rand.nextInt(allWares.size());
					drop = allWares.get(random).copy();
					drop = CyberwareAPI.sanitize(drop);
					drop = CyberwareAPI.getCyberware(drop).setQuality(drop, CyberwareAPI.QUALITY_SCAVENGED);
					drop.setCount(1);
					count++;
				}

				if (count < 50)
				{
					this.entityDropItem(drop, 0.0F);
				}
			}
		}
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty)
	{
		//super.setEquipmentBasedOnDifficulty(difficulty);
		float chance = this.world.rand.nextFloat();
		if (chance > 0.0F && chance <= 0.25F)
		{
			if (CyberwareConfig.KATANA && this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).isEmpty())
			{
				this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, setRandomDamage(new ItemStack(CyberwareContent.katana).copy()));
				this.setDropChance(EntityEquipmentSlot.MAINHAND, 0.1F);
			}
		}

		if (this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).isEmpty())
		{
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, setRandomDamage(new ItemStack(Items.IRON_AXE).copy()));
		}

		chance = this.world.rand.nextFloat();

		//One Third of Skeletons should have a shield
		if (chance > 0.0F && chance <= .34F && this.getHeldItem(EnumHand.OFF_HAND).isEmpty())
		{
			ItemStack shield = new ItemStack(Items.SHIELD).copy();
			
			if(Equipment.shields.enableShields)
			{
				float shieldType = this.world.rand.nextFloat();
				
				if(shieldType > 0.85F)
					shield = setRandomDamage(new ItemStack(Content.itemShieldPlasteel).copy());
				if(shieldType < 0.85F && shieldType > 0.75F)
					shield = setRandomDamage(new ItemStack(Content.itemShieldCarbon).copy());
				if(shieldType < 0.75F && shieldType > 0.5F)
					shield = setRandomDamage(new ItemStack(Content.itemShieldPolymer).copy());
			}
			
			this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, shield);
			this.setDropChance(EntityEquipmentSlot.OFFHAND, 0.25F);
		}
	}
	
	@Override
	public List<ZombieItem> getCyberEntityItems()
	{
		return Content.cyberSkeletonItems;
	}
	
	@Override
	public boolean getHasWare(EntityLivingBase entityLivingBase)
	{
		return this.hasWare;
	}

	@Override
	public void setHasWare(boolean bool)
	{
		this.hasWare = bool;
	}
	
	protected ItemStack setRandomDamage(ItemStack stack)
	{
		int damage = (int) (stack.getMaxDamage() * rand.nextFloat());

		ItemStack newStack = stack.copy();
		newStack.setItemDamage(damage);
		return newStack;
	}
}