package com.mr208.rewired.client;

import com.mr208.rewired.common.CommonProxy;
import com.mr208.rewired.common.ReWIREDContent;
import com.mr208.rewired.common.items.ItemReWIRED;
import com.mr208.rewired.common.items.augments.ItemAugment;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.ICyberware;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import java.util.ArrayList;
import java.util.List;

public class ClientProxy extends CommonProxy
{

	@Override
	public void onPreInit()
	{
		super.onPreInit();

		for(Block block: ReWIREDContent.registeredBlocks)
			registerRenders(block);

		for(Item item: ReWIREDContent.registeredItems)
			registerRenders(item);


	}

	@Override
	public void registerRenders(Block block)
	{
		Item item = Item.getItemFromBlock(block);
		ModelLoader.setCustomModelResourceLocation(item,
				0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
	}

	@Override
	public void registerRenders(Item item)
	{
		if(item instanceof ItemAugment)
		{
			ItemAugment augment = (ItemAugment) item;
			List<ModelResourceLocation> models = new ArrayList<>();
			if (augment.subnames.length > 0)
			{
				for (int i = 0; i < augment.subnames.length; i++)
				{
					String name = augment.getRegistryName() + "_" + augment.subnames[i];
					for (ICyberware.Quality q : ICyberware.Quality.qualities)
					{
						if (q.getSpriteSuffix() != null && augment.canHoldQuality(new ItemStack(augment, 1, i), q))
						{
							models.add(new ModelResourceLocation(name + "_" + q.getSpriteSuffix(), "inventory"));
						}
					}
					models.add(new ModelResourceLocation(name, "inventory"));
				}
			} else
			{
				String name = augment.getRegistryName() + "";

				for (ICyberware.Quality q : ICyberware.Quality.qualities)
				{
					if (q.getSpriteSuffix() != null && augment.canHoldQuality(new ItemStack(augment), q))
					{
						models.add(new ModelResourceLocation(name + "_" + q.getSpriteSuffix(), "inventory"));
					}
				}
				models.add(new ModelResourceLocation(name, "inventory"));
			}
			ModelLoader.registerItemVariants(item, models.toArray(new ModelResourceLocation[0]));
			ModelLoader.setCustomMeshDefinition(item, stack -> {
				ItemStack test = stack.copy();
				if (!test.isEmpty() && test.hasTagCompound())
				{
					test.getTagCompound().removeTag(CyberwareAPI.QUALITY_TAG);
				}

				ItemAugment augment1 = (ItemAugment) stack.getItem();
				String added = "";
				if (augment1.subnames.length > 0)
				{
					int i = Math.min(augment1.subnames.length - 1, stack.getItemDamage());
					added = "_" + augment1.subnames[i];
				}

				ICyberware.Quality q = CyberwareAPI.getCyberware(stack).getQuality(stack);

				if (q != null && CyberwareAPI.getCyberware(test).getQuality(test) != q && q.getSpriteSuffix() != null)
				{
					return new ModelResourceLocation(augment1.getRegistryName() + added + "_" + q.getSpriteSuffix(), "inventory");
				} else
				{
					return new ModelResourceLocation(augment1.getRegistryName() + added, "inventory");
				}
			});
		}
		else if(item instanceof ItemReWIRED)
		{
			ItemReWIRED base = ((ItemReWIRED) item);
			if(base.subnames.length>0)
			{
				for(int i = 0; i < base.subnames.length; i++)
				{
					ModelLoader.setCustomModelResourceLocation(item,
							i, new ModelResourceLocation( item.getRegistryName() + "_" + base.subnames[i], "inventory"));
				}
			}
			else
			{
				ModelLoader.setCustomModelResourceLocation(item,
						0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
			}
		}
		else
		{
			ModelLoader.setCustomModelResourceLocation(item,
					0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
		}

	}
}
