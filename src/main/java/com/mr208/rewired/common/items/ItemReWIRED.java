package com.mr208.rewired.common.items;

import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.common.ReWIREDContent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ItemReWIRED extends Item
{
	public String name;
	public String[] subNames;
	public boolean registerSubModels = false;
	public boolean[] hiddenItems;
	

	public ItemReWIRED(String name, String... subnames)
	{
		this.name = name;
		this.subNames = subnames;
		this.setUnlocalizedName(ReWIRED.MOD_ID+"."+name);
		this.setHasSubtypes(subnames!=null&&subnames.length>0);
		this.setCreativeTab(ReWIRED.creativeTab);
		this.setMaxStackSize(64);
		this.hiddenItems = new boolean[this.subNames.length>0?this.subNames.length:1];
		this.setRegistryName(ReWIRED.MOD_ID, name);
		ReWIREDContent.registeredItems.add(this);
		ForgeRegistries.ITEMS.register(this);
	}
	
	public String[] getSubNames()
	{
		return subNames;
	}
	
	public ItemReWIRED setMetaHidden(int ... metaHidden)
	{
		for(int meta:metaHidden)
			if(meta >= 0 && meta < this.hiddenItems.length) this.hiddenItems[meta]=true;
		
		return this;
	}
	
	public ItemReWIRED setMetaUnhidden(int ... metaHidden)
	{
		for(int meta:metaHidden)
			if(meta >= 0 && meta < this.hiddenItems.length) this.hiddenItems[meta]=false;
		
		return this;
	}
	
	public boolean isMetaHidden(int meta)
	{
		return this.hiddenItems[Math.max(0, Math.min(meta, this.hiddenItems.length))];
	}
	
	public ItemReWIRED setRegisterSubModels(boolean registerSubModels)
	{
		this.registerSubModels = registerSubModels;
		
		return this;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		int damage = itemstack.getItemDamage();
		if (damage >= subNames.length)
		{
			return super.getUnlocalizedName();
		}
		return super.getUnlocalizedName(itemstack) + "." + subNames[damage];
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if(this.isInCreativeTab(tab))
		{
			if(getSubNames().length>0)
			{
				for(int i = 0; i<getSubNames().length; i++)
				{
					if(!isMetaHidden(i)) items.add(new ItemStack(this, 1, i));
				}
			}
			else
			{
				if(!isMetaHidden(0)) items.add(new ItemStack(this));
			}
		}
	}
}
