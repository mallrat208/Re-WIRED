package com.mr208.rewired.client;

import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.client.model.ModelTechVisor;
import com.mr208.rewired.client.render.ReWIREDMeshDefinition;
import com.mr208.rewired.client.render.RenderCyberSkeleton;
import com.mr208.rewired.client.render.item.CustomItemModelFactory;
import com.mr208.rewired.common.CommonProxy;
import com.mr208.rewired.common.ReWIREDContent;
import com.mr208.rewired.common.blocks.BlockECG;
import com.mr208.rewired.common.entities.EntityCyberSkeleton;
import com.mr208.rewired.common.items.ItemReWIRED;
import com.mr208.rewired.common.items.augments.ItemAugment;
import flaxbeard.cyberware.api.item.ICyberware.Quality;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class ClientProxy extends CommonProxy
{
	
	
	public static final String[] CUSTOM_RENDERS = new String[]{"visor"};
	
	@Override
	public void onPreInit()
	{
		super.onPreInit();
		
		MinecraftForge.EVENT_BUS.register(this);
		
		for(Block block: ReWIREDContent.registeredBlocks)
			registerRenders(block);
		
		for(Item item: ReWIREDContent.registeredItems)
			registerRenders(item);
		
		RenderingRegistry.registerEntityRenderingHandler(EntityCyberSkeleton.class, RenderCyberSkeleton::new);
	}

	private void registerRenders(Block block)
	{
			Item item=Item.getItemFromBlock(block);
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
	}
	
	private void registerRenders(Item item)
	{
		if (item instanceof ItemAugment)
		{
			ItemAugment ware = (ItemAugment) item;
			List<ModelResourceLocation> models = new ArrayList<ModelResourceLocation>();
			if (ware.subNames.length > 0)
			{
				for (int i = 0; i < ware.subNames.length; i++)
				{
					String name = ware.getRegistryName() + "_" + ware.subNames[i];
					for (Quality q : Quality.qualities)
					{
						if (q.getSpriteSuffix() != null && ware.canHoldQuality(new ItemStack(ware, 1, i), q))
						{
							models.add(new ModelResourceLocation(name + "_" + q.getSpriteSuffix(), "inventory"));
						}
					}
					models.add(new ModelResourceLocation(name, "inventory"));
				}
			}
			else
			{
				String name = ware.getRegistryName()+"";
				
				for (Quality q : Quality.qualities)
				{
					if (q.getSpriteSuffix() != null && ware.canHoldQuality(new ItemStack(ware), q))
					{
						models.add(new ModelResourceLocation(name + "_" + q.getSpriteSuffix(), "inventory"));
					}
				}
				models.add(new ModelResourceLocation(name, "inventory"));
				
			}
			ModelLoader.registerItemVariants(ware, models.toArray(new ModelResourceLocation[models.size()]));
			ModelLoader.setCustomMeshDefinition(ware, new ReWIREDMeshDefinition());
		}
		else if (item instanceof ItemReWIRED)
		{
			ItemReWIRED base = ((ItemReWIRED) item);
			if (base.subNames.length > 0)
			{
				for (int i = 0; i < base.subNames.length; i++)
				{
					ModelLoader.setCustomModelResourceLocation(item,
							i, new ModelResourceLocation(item.getRegistryName() + "_" + base.subNames[i], "inventory"));
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
	
	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event)
	{
		for(String itemRender : CUSTOM_RENDERS)
		{
			ModelResourceLocation model = new ModelResourceLocation("rewired:" + itemRender, "inventory");
			Object obj = event.getModelRegistry().getObject(model);
			
			if(obj != null)
			{
				event.getModelRegistry().putObject(model, new CustomItemModelFactory((IBakedModel)obj));
			}
		}
	}
}
