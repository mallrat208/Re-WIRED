package com.mr208.rewired.common.handlers.packets;

import com.mr208.rewired.ReWIRED;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGUI implements IMessage
{
	public PacketGUI(){}
	
	public int ID;
	public int posX;
	public int posY;
	public int posZ;
	
	public PacketGUI(int ID, int x, int y, int z)
	{
		this.ID = ID;
		this.posX = x;
		this.posY = y;
		this.posZ = z;
	}
	
	public PacketGUI(int ID, BlockPos pos)
	{
		this.ID = ID;
		this.posX = pos.getX();
		this.posY = pos.getY();
		this.posZ = pos.getZ();
	}
	
	public PacketGUI(int ID, EntityPlayer player)
	{
		this.ID = ID;
		this.posX = player.getPosition().getX();
		this.posY = player.getPosition().getY();
		this.posZ = player.getPosition().getZ();
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.ID = buf.readInt();
		this.posX = buf.readInt();
		this.posY = buf.readInt();
		this.posZ = buf.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(ID);
		buf.writeInt(posX);
		buf.writeInt(posY);
		buf.writeInt(posZ);
	}
	
	public static class Handler implements IMessageHandler<PacketGUI, IMessage>
	{
		
		@Override
		public IMessage onMessage(PacketGUI message, MessageContext ctx)
		{
			if(!ctx.side.isServer())
				throw new IllegalStateException("Received PacketGUI on the Client Side!");
			
			EntityPlayerMP playerMP = ctx.getServerHandler().player;
			DimensionManager.getWorld(playerMP.getEntityWorld().provider.getDimension()).addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
						EntityPlayerMP playerMP = ctx.getServerHandler().player;
						playerMP.openGui(ReWIRED.INSTANCE, message.ID, playerMP.world, message.posX, message.posY, message.posZ);
				}
			});
			
			return null;
		}
		
	}
}
