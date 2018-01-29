package com.mr208.rewired.common.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

public class VillageStructureHelper
{

	public static void spawnStructure(ResourceLocation resourceLocation, World world, StructureBoundingBox sbb, EnumFacing facing, BlockPos offSet)
	{
		TemplateManager manager = world.getSaveHandler().getStructureTemplateManager();
		MinecraftServer server = world.getMinecraftServer();
		
		if(manager != null && server != null)
		{
			Mirror mirror;
			Rotation rotation;
			
			if(facing==EnumFacing.SOUTH)
			{
				mirror = Mirror.NONE;
				rotation = Rotation.NONE;
			} else if(facing == EnumFacing.WEST)
			{
				mirror = Mirror.NONE;
				rotation = Rotation.CLOCKWISE_180;
			} else if(facing == EnumFacing.EAST)
			{
				mirror = Mirror.LEFT_RIGHT;
				rotation = Rotation.CLOCKWISE_180;
			} else
			{
				mirror = Mirror.LEFT_RIGHT;
				rotation = Rotation.NONE;
			}
			
			PlacementSettings placementSettings = new PlacementSettings().setRotation(rotation).setMirror(mirror).setBoundingBox(sbb);
			Template template = manager.getTemplate(server, resourceLocation);
			
			if(template !=null)
				template.addBlocksToWorld(world, offSet, placementSettings);
		}
	}
}
