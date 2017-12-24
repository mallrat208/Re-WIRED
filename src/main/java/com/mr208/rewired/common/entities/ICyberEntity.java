package com.mr208.rewired.common.entities;

import flaxbeard.cyberware.api.CyberwareUserDataImpl;
import flaxbeard.cyberware.common.CyberwareContent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public interface ICyberEntity {

	NBTTagCompound writeToNBT(NBTTagCompound compound);
	void readFromNBT(NBTTagCompound compound);
	<T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing);
	boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing);
	boolean getHasWare(EntityLivingBase entityLivingBase);
	void setHasWare(boolean bool);
	default List<CyberwareContent.ZombieItem> getCyberEntityItems()
	{
		return CyberwareContent.zombieItems;
	}
}

