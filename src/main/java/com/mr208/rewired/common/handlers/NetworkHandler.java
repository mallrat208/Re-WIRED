package com.mr208.rewired.common.handlers;

import com.mr208.rewired.ReWIRED;
import com.mr208.rewired.common.handlers.packets.PacketEntityMovement;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler
{
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ReWIRED.MOD_ID);

	public static int packet = 0;

	public static void init()
	{
		INSTANCE.registerMessage(PacketEntityMovement.Handler.class, PacketEntityMovement.class, packet++, Side.CLIENT);
	}
}
