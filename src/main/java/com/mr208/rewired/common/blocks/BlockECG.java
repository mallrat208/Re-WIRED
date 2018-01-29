package com.mr208.rewired.common.blocks;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.common.ReWIREDContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BlockECG extends Block
{
	private static final EnumFacing[] facingsHorizontal = {EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH};
	private static final EnumFacing[] facingsVertical = {EnumFacing.DOWN,EnumFacing.UP};
	private static final EnumFacing[] facings = ArrayUtils.addAll(facingsVertical, facingsHorizontal);
	
	private List<BlockPos> turnOff = Lists.newArrayList();
	private List<BlockPos> turnOn = Lists.newArrayList();
	private final Set<BlockPos> updatedGlass = Sets.newLinkedHashSet();
	
	private static final Vec3i[] surroundingBlocksOffset;
	static {
		Set<Vec3i> set = Sets.newLinkedHashSet();
		for (EnumFacing facing : facings) {
			set.add(facing.getDirectionVec());
		}
		for (EnumFacing facing1 : facings) {
			Vec3i v1 = facing1.getDirectionVec();
			for (EnumFacing facing2 : facings) {
				Vec3i v2 = facing2.getDirectionVec();
				set.add(new Vec3i(v1.getX() + v2.getX(), v1.getY() + v2.getY(), v1.getZ() + v2.getZ()));
			}
		}
		set.remove(new Vec3i(0, 0, 0));
		surroundingBlocksOffset = set.toArray(new Vec3i[set.size()]);
	}
	
	public static final PropertyInteger POWER = PropertyInteger.create("power",0,15);
	
	private boolean canProvidePower = false;
	
	private EnumDyeColor enumDyeColor = null;
	
	public BlockECG(EnumDyeColor color)
	{
		super(Material.GLASS);
		
		String name = "ecglass" + (color == null ? "" : "_" + color.getUnlocalizedName());
		
		setEnumDyeColor(color);
		
		this.setRegistryName(new ResourceLocation(ReWIRED.MOD_ID,name));
		this.setUnlocalizedName(ReWIRED.MOD_ID+"."+name);
		this.setSoundType(SoundType.GLASS);
		this.setDefaultState(this.blockState.getBaseState().withProperty(POWER, 0));
		this.setCreativeTab(ReWIRED.creativeTab);
		
		ItemBlock itemBlock = new ItemBlock(this);
		itemBlock.setRegistryName(this.getRegistryName());
		
		ForgeRegistries.BLOCKS.register(this);
		ForgeRegistries.ITEMS.register(itemBlock);
		
		ReWIREDContent.registeredItems.add(itemBlock);
		ReWIREDContent.registeredBlocks.add(this);
	}
	
	private void setEnumDyeColor(EnumDyeColor color)
	{
		this.enumDyeColor = color;
	}
	
	private EnumDyeColor getEnumDyeColor(BlockECG block)
	{
		return block.enumDyeColor;
	}
	
	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public boolean isTranslucent(IBlockState state)
	{
		return true;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return state.getValue(POWER) == 0? 0 : 255;
	}
	
	private void updateSurroundingGlass(World worldIn, BlockPos pos) {
		
		calculateCurrentChanges(worldIn, pos);
		Set<BlockPos> blocksNeedingUpdate = Sets.newLinkedHashSet();
		
		for (BlockPos posi : updatedGlass) {
			addBlocksNeedingUpdate(worldIn, posi, blocksNeedingUpdate);
		}
		
		Iterator<BlockPos> it = Lists.newLinkedList(updatedGlass).descendingIterator();
		while (it.hasNext()) {
			addAllSurroundingBlocks(it.next(), blocksNeedingUpdate);
		}
		
		blocksNeedingUpdate.removeAll(updatedGlass);
		updatedGlass.clear();
		
		for (BlockPos posi : blocksNeedingUpdate) {
			worldIn.notifyNeighborsOfStateChange(posi,this, true);
		}
	}

	private void calculateCurrentChanges(World worldIn, BlockPos position)
	{
		if (worldIn.getBlockState(position).getBlock() instanceof BlockECG) {
			turnOff.add(position);
		} else {
			checkSurroundingGlass(worldIn, position);
		}
		
		while (!turnOff.isEmpty()) {
			BlockPos pos = turnOff.remove(0);
			IBlockState state = worldIn.getBlockState(pos);
			int oldPower = state.getValue(POWER);
			this.canProvidePower = false;
			int blockPower = worldIn.isBlockIndirectlyGettingPowered(pos);
			this.canProvidePower = true;
			int wirePower = getSurroundingGlassPower(worldIn, pos);
			
			wirePower--;
			int newPower = Math.max(blockPower, wirePower);
			
			if (newPower < oldPower) {
				
				if (blockPower > 0 && !turnOn.contains(pos)) {
					turnOn.add(pos);
				}
				
				setGlassState(worldIn, pos, state, 0);
				
			} else if (newPower > oldPower) {
				
				setGlassState(worldIn, pos, state, newPower);
			}
			checkSurroundingGlass(worldIn, pos);
			worldIn.notifyLightSet(pos);
		}
		
		while (!turnOn.isEmpty()) {
			BlockPos pos = turnOn.remove(0);
			IBlockState state = worldIn.getBlockState(pos);
			int oldPower = state.getValue(POWER);
			this.canProvidePower = false;
			int blockPower = worldIn.isBlockIndirectlyGettingPowered(pos);
			this.canProvidePower = true;
			int wirePower = getSurroundingGlassPower(worldIn, pos);
		
			wirePower--;
			int newPower = Math.max(blockPower, wirePower);
			
			if (newPower > oldPower) {
				setGlassState(worldIn, pos, state, newPower);
			}
			checkSurroundingGlass(worldIn, pos);
			worldIn.notifyLightSet(pos);
		}
		turnOff.clear();
		turnOn.clear();
	}
	
	private void addGlassToList(World worldIn, BlockPos pos, int otherPower)
	{
		IBlockState state = worldIn.getBlockState(pos);
		if (state.getBlock() instanceof BlockECG)
		{
			int power = state.getValue(POWER);
			if (power < (otherPower-1) && !turnOn.contains(pos))
			{
				turnOn.add(pos);
			}
			
			if (power > otherPower && !turnOff.contains(pos))
			{
				turnOff.add(pos);
			}
		}
	}

	private void checkSurroundingGlass(World worldIn, BlockPos pos)
	{
		IBlockState state = worldIn.getBlockState(pos);
		int ownPower = 0;
		if (state.getBlock() instanceof BlockECG)
		{
			ownPower = state.getValue(POWER);
		}
		for (EnumFacing facing : facingsHorizontal)
		{
			BlockPos offsetPos = pos.offset(facing);
			if (facing.getAxis().isHorizontal())
			{
				addGlassToList(worldIn, offsetPos, ownPower);
			}
		}
		for (EnumFacing facingVertical : facingsVertical)
		{
			BlockPos offsetPos = pos.offset(facingVertical);
			for (EnumFacing facingHorizontal : facingsHorizontal)
			{
					addGlassToList(worldIn, offsetPos.offset(facingHorizontal), ownPower);
			}
			
			addGlassToList(worldIn, offsetPos, ownPower);
		}
		
	}
	
	private int getSurroundingGlassPower(World worldIn, BlockPos pos) {
		int glassPower = 0;
		for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
		{
			BlockPos offsetPos = pos.offset(enumfacing);
			
			glassPower = this.getMaxCurrentStrength(worldIn, offsetPos, glassPower);
			
			glassPower = this.getMaxCurrentStrength(worldIn, offsetPos.up(), glassPower);
			
			glassPower = this.getMaxCurrentStrength(worldIn, offsetPos.down(), glassPower);
		}
		
		glassPower = this.getMaxCurrentStrength(worldIn, pos.up(), glassPower);
		
		glassPower = this.getMaxCurrentStrength(worldIn, pos.down(), glassPower);
		
		return glassPower;
	}

	private void addBlocksNeedingUpdate(World worldIn, BlockPos pos, Set<BlockPos> set)
	{
		List<EnumFacing> connectedSides = getSidesToPower(worldIn, pos);
		
		for (EnumFacing facing : facings) {
			BlockPos offsetPos = pos.offset(facing);
	
			if (connectedSides.contains(facing.getOpposite()) || facing == EnumFacing.DOWN || (facing.getAxis().isHorizontal() && canConnectTo(worldIn.getBlockState(offsetPos), facing))) {
				if (canBlockBePoweredFromSide(worldIn.getBlockState(offsetPos), facing, true)) set.add(offsetPos);
			}
		}
		
		for (EnumFacing facing : facings) {
			BlockPos offsetPos = pos.offset(facing);
			if (connectedSides.contains(facing.getOpposite()) || facing == EnumFacing.DOWN) {
				if (worldIn.getBlockState(offsetPos).isNormalCube()) {
					for (EnumFacing facing1 : facings) {
						if (canBlockBePoweredFromSide(worldIn.getBlockState(offsetPos.offset(facing1)), facing1, false)) set.add(offsetPos.offset(facing1));
					}
				}
			}
		}
	}

	private boolean canBlockBePoweredFromSide(IBlockState state, EnumFacing side, boolean isWire)
	{
		if (state.getBlock() instanceof BlockRedstoneDiode && state.getValue(BlockRedstoneDiode.FACING) != side.getOpposite())
		{
			return isWire&&state.getBlock() instanceof BlockRedstoneComparator&&state.getValue(BlockRedstoneComparator.FACING).getAxis()!=side.getAxis()&&side.getAxis().isHorizontal();
		}
		if (state.getBlock() instanceof BlockRedstoneTorch) {
			return !isWire&&state.getValue(BlockRedstoneTorch.FACING)==side;
		}
		if (state.getBlock() instanceof BlockECG)
			return true;
		
		return false;
	}
	
	private List<EnumFacing> getSidesToPower(World worldIn, BlockPos pos)
	{
		List<EnumFacing> retval = new ArrayList<>();
		for (EnumFacing facing : facingsHorizontal) {
			if (isPowerSourceAt(worldIn, pos, facing)) retval.add(facing);
		}
		if (retval.isEmpty()) return Lists.newArrayList(facingsHorizontal);
		boolean northsouth = retval.contains(EnumFacing.NORTH) || retval.contains(EnumFacing.SOUTH);
		boolean eastwest = retval.contains(EnumFacing.EAST) || retval.contains(EnumFacing.WEST);
		if (northsouth) {
			retval.remove(EnumFacing.EAST);
			retval.remove(EnumFacing.WEST);
		}
		if (eastwest) {
			retval.remove(EnumFacing.NORTH);
			retval.remove(EnumFacing.SOUTH);
		}
		return retval;
	}
	

	private void addAllSurroundingBlocks(BlockPos pos, Set<BlockPos> set)
	{
		for (Vec3i vect : surroundingBlocksOffset) {
			set.add(pos.add(vect));
		}
	}

	private void setGlassState(World worldIn, BlockPos pos, IBlockState state, int power)
	{
		state = state.withProperty(POWER, power);
		worldIn.setBlockState(pos, state, 2);
		updatedGlass.add(pos);
	}
	
	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		if(!worldIn.isRemote) {
			this.updateSurroundingGlass(worldIn, pos);
			for (Vec3i vec : surroundingBlocksOffset) {
				worldIn.notifyNeighborsOfStateChange(pos.add(vec), this, true);
			}
		}
	}
	
	@Override
	public void breakBlock(@Nonnull World worldIn,@Nonnull BlockPos pos,@Nonnull IBlockState state) {
		super.breakBlock(worldIn, pos, state);
		if(!worldIn.isRemote) {
			this.updateSurroundingGlass(worldIn, pos);
			for (Vec3i vec : surroundingBlocksOffset) {
				worldIn.notifyNeighborsOfStateChange(pos.add(vec), this, true);
			}
		}
	}
	
	private int getMaxCurrentStrength(World worldIn, BlockPos pos, int strength) {
		if(!(worldIn.getBlockState(pos).getBlock() instanceof BlockECG)) {
			return strength;
		} else {
			int i = worldIn.getBlockState(pos).getValue(POWER);
			return i > strength?i:strength;
		}
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		if(!worldIn.isRemote)
		{
			if(this.canPlaceBlockAt(worldIn, pos))
			{
				this.updateSurroundingGlass(worldIn,pos);
			}
			else
			{
				this.dropBlockAsItem(worldIn, pos, state, 0);
				worldIn.setBlockToAir(pos);
			}
		}
	}
	
	@Override
	public boolean canPlaceBlockAt(World worldIn,@Nonnull BlockPos pos)
	{
		return true;
	}
	
	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return !this.canProvidePower?0:blockState.getWeakPower(blockAccess, pos, side);
	}
	
	/*
	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		if(!this.canProvidePower) {
			return 0;
		} else {
			// Changed implementation to use getSidesToPower() to avoid duplicate implementation
			if (side == EnumFacing.UP || getSidesToPower((World)blockAccess, pos).contains(side)) {
				return blockState.getValue(POWER);
			} else {
				return 0;
			}
		}
	}
	*/
	
	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		if(!this.canProvidePower) {
			return 0;
		} else {
			// Changed implementation to use getSidesToPower() to avoid duplicate implementation
			if (side == EnumFacing.UP || getSidesToPower((World)blockAccess, pos).contains(side) && ((World)blockAccess).getBlockState(pos.offset(side.getOpposite())).getBlock() == this) {
				return blockState.getValue(POWER);
			} else {
				return 0;
			}
		}
	}
	
	@Override
	public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return super.shouldCheckWeakPower(state, world, pos, side);
	}
	
	private boolean isPowerSourceAt(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		BlockPos blockpos = pos.offset(side);
		IBlockState iblockstate = worldIn.getBlockState(blockpos);
		boolean flag = iblockstate.isNormalCube();
		boolean flag1 = worldIn.getBlockState(pos.up()).isNormalCube();
		return !flag1&&flag&&canConnectUpwardsTo(worldIn, blockpos.up())||(canConnectTo(iblockstate, side)||(iblockstate.getBlock()==Blocks.POWERED_REPEATER&&iblockstate.getValue(BlockRedstoneDiode.FACING)==side||!flag&&canConnectUpwardsTo(worldIn, blockpos.down())));
	}
	
	private static boolean canConnectUpwardsTo(IBlockAccess worldIn, BlockPos pos) {
		return canConnectUpwardsTo(worldIn.getBlockState(pos));
	}
	
	private static boolean canConnectUpwardsTo(IBlockState state) {
		return canConnectTo(state, null);
	}

	private static boolean canConnectTo(IBlockState blockState, @Nullable EnumFacing side) {
		Block block = blockState.getBlock();
		if(block instanceof BlockECG)
		{
			return true;
		} else if(block == Blocks.REDSTONE_WIRE) {
			return true;
		} else if(Blocks.UNPOWERED_REPEATER.isSameDiode(blockState)) {
			EnumFacing enumfacing =blockState.getValue(BlockRedstoneRepeater.FACING);
			return enumfacing == side || enumfacing.getOpposite() == side;
		} else {
			return blockState.canProvidePower() && side != null;
		}
	}
	
	@Override
	public boolean canProvidePower(IBlockState state) {
		return this.canProvidePower;
	}
	
	@Override
	@Nonnull
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}
	
	@Override
	@Nonnull
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(POWER, meta);
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(POWER);
	}
	
	@Override
	@Nonnull
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, POWER);
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockState blockState,@Nonnull IBlockAccess blockAccess,@Nonnull BlockPos pos, EnumFacing side)
	{
		IBlockState adjacentState = blockAccess.getBlockState(pos.offset(side));
		
		if(blockState.getBlock() != adjacentState.getBlock())
		{
			return true;
		}
		
		if(blockState.getValue(POWER) == 0 && blockState.getValue(POWER) == 0)
		{
			return false;
		}
		
		if(blockState.getValue(POWER) == 0 && adjacentState.getValue(POWER) != 0)
		{
			return true;
		}
		
		if(blockState.getValue(POWER) != 0 && adjacentState.getValue(POWER) != 0)
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean hasComparatorInputOverride(IBlockState state)
	{
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
	{
		return blockState.getValue(POWER);
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced)
	{
		tooltip.add(ChatFormatting.DARK_GRAY + I18n.format(this.getUnlocalizedName()+".tooltip"));
		super.addInformation(stack, player, tooltip, advanced);
	}
}
