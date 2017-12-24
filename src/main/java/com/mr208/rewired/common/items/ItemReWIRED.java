package com.mr208.rewired.common.items;

import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.common.ReWIREDContent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;

public class ItemReWIRED extends Item
{
	public String name;
	public String[] subnames;
	public boolean hiddenItem = false;
	public boolean[] hiddenItems;

	public ItemReWIRED(String name, String... subnames)
	{
		this.name = name;
		this.subnames = subnames;

		this.setUnlocalizedName(ReWIRED.MOD_ID+"."+name);
		this.setRegistryName(ReWIRED.MOD_ID, name);
		ForgeRegistries.ITEMS.register(this);
		this.setHasSubtypes(this.subnames.length>0);
		hiddenItems = new boolean[this.subnames.length>0?this.subnames.length:0];
		for(int i = 0; i<subnames.length-1;i++)
			hiddenItems[i] = false;

		this.setMaxDamage(0);
		this.setCreativeTab(ReWIRED.creativeTab);

		ReWIREDContent.registeredItems.add(this);
	}

	public ItemReWIRED setItemHidden()
	{
		this.hiddenItem = true;
		return this;
	}

	public ItemReWIRED setItemMetaHidden(int ... metaHidden)
	{
		for(int meta:metaHidden)
			hiddenItems[meta] = true;

		return this;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack)
	{
		int damage = itemStack.getItemDamage();
		if(damage>=subnames.length)
		{
			return super.getUnlocalizedName();
		}
		return super.getUnlocalizedName(itemStack) + "." + subnames[damage];
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if(this.isInCreativeTab(tab))
		{
			if(subnames.length==0)
			{
				items.add(new ItemStack(this));
			}
			for(int i=0; i<subnames.length; i++)
			{
				items.add(new ItemStack(this, 1, i));
			}
		}
	}
}
