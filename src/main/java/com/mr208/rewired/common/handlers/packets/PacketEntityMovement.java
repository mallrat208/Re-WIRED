package com.mr208.rewired.common.handlers.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketEntityMovement implements IMessage {

	public PacketEntityMovement(){}

	public int entityID;
	public double motX;
	public double motY;
	public double motZ;
	public float yaw;
	public float pitch;

	public PacketEntityMovement(Entity entity)
	{
		this.entityID = entity.getEntityId();
		this.motX = entity.motionX;
		this.motY = entity.motionY;
		this.motZ = entity.motionZ;
		this.yaw = entity.rotationYaw;
		this.pitch = entity.rotationPitch;
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		this.entityID = buf.readInt();
		this.motX = buf.readDouble();
		this.motY = buf.readDouble();
		this.motZ = buf.readDouble();
		this.yaw = buf.readFloat();
		this.pitch = buf.readFloat();

	}

	@Override
	public void toBytes(ByteBuf buf) {

		buf.writeInt(entityID);
		buf.writeDouble(motX);
		buf.writeDouble(motY);
		buf.writeDouble(motZ);
		buf.writeFloat(yaw);
		buf.writeFloat(pitch);

	}

	public static class Handler implements IMessageHandler<PacketEntityMovement, IMessage>
	{

		@Override
		public IMessage onMessage(PacketEntityMovement packet, MessageContext ctx) {
			if (!ctx.side.isClient())
				throw new IllegalStateException("Received PacketEntityMovement on the Server Side!");

			IThreadListener mainThread = Minecraft.getMinecraft();

			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {

					World world = Minecraft.getMinecraft().world;
					Entity entity = world.getEntityByID(packet.entityID);

					if (entity != null) {
						entity.motionX = packet.motX;
						entity.motionY = packet.motY;
						entity.motionZ = packet.motZ;
						entity.rotationYaw = packet.yaw;
						entity.rotationPitch = packet.pitch;
					}

				}
			});

			return null;
		}
	}
}
