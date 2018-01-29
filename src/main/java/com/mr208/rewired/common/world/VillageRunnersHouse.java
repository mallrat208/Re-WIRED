package com.mr208.rewired.common.world;

import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.common.handlers.VillagerHandler;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class VillageRunnersHouse extends StructureVillagePieces.Village
{
	final ResourceLocation STRUCTURE = new ResourceLocation(ReWIRED.MOD_ID, "runner_house");
	
	public VillageRunnersHouse(Start villagePiece, int par2, Random rand, StructureBoundingBox structureBoundingBox, EnumFacing facing)
	{
		super(villagePiece, par2);
		this.setCoordBaseMode(facing);
		this.boundingBox = structureBoundingBox;
	}
	
	private int groundLevel = -1;
	
	public static VillageRunnersHouse buildComponent(Start startPiece, List<StructureComponent> pieces, Random random, int x, int y, int z, EnumFacing facing, int type)
	{
		StructureBoundingBox box = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 9, 6, 10, facing);
		return canVillageGoDeeper(box) && StructureComponent.findIntersecting(pieces, box) == null ? new VillageRunnersHouse(startPiece,type, random, box, facing) : null;
	}
	
	@Override
	public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
	{
		if(this.groundLevel < 0)
		{
			this.groundLevel = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
			
			if(this.groundLevel < 0)
				return true;
			
			this.boundingBox.offset(0, this.groundLevel - this.boundingBox.maxY + 6 - 1, 0);
		}
		
		
		this.fillWithAir(worldIn, structureBoundingBoxIn, 0,0,0, 9, 6, 10);
		BlockPos offSet = new BlockPos(getXWithOffset(0,0), getYWithOffset(0), getZWithOffset(0,0));
		VillageStructureHelper.spawnStructure(STRUCTURE, worldIn, structureBoundingBoxIn, getCoordBaseMode(), offSet);
		
		//Support
		for(int z=0; z < 10; z++)
			for(int x=1; x < 10; x++)
			{
				this.clearCurrentPositionBlocksUpwards(worldIn, x,6,z, structureBoundingBoxIn);
				this.replaceAirAndLiquidDownwards(worldIn, Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.GRAY), x, -1,z, structureBoundingBoxIn);
			}
		
		return true;
	}
	
	@Override
	protected VillagerProfession chooseForgeProfession(int count, VillagerProfession prof)
	{
		return VillagerHandler.cyberwareVillager;
	}
	
	@Override
	protected IBlockState getBiomeSpecificBlockState(IBlockState blockstateIn)
	{
		return super.getBiomeSpecificBlockState(blockstateIn);
	}
	
	public static class VillageCreationRunner implements VillagerRegistry.IVillageCreationHandler
	{
		
		@Override
		public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int i)
		{
			return new PieceWeight(VillageRunnersHouse.class, 15, 1);
		}
		
		@Override
		public Class<?> getComponentClass()
		{
			return VillageRunnersHouse.class;
		}
		
		@Override
		public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5)
		{
			return VillageRunnersHouse.buildComponent(startPiece, pieces, random, p1, p2, p3, facing, p5);
		}
	}
}
