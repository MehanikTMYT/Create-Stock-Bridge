package com.tom.stockbridge.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import com.tom.stockbridge.IDataReceiver;

public class NetworkHandler {
	@SubscribeEvent
	public static void onPayloadRegister(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar("1");
		// PORT(26.1): DirectionalPayloadHandler is gone; server and client handlers are passed separately.
		registrar.playBidirectional(DataPacket.ID, DataPacket.STREAM_CODEC, NetworkHandler::handleDataServer, NetworkHandler::handleDataClient);
	}

	public static void handleDataServer(DataPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			ServerPlayer sender = (ServerPlayer) context.player();
			if(sender.containerMenu instanceof IDataReceiver) {
				((IDataReceiver)sender.containerMenu).receive(packet.tag());
			}
		});
	}

	public static void handleDataClient(DataPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if(Minecraft.getInstance().screen instanceof IDataReceiver) {
				((IDataReceiver)Minecraft.getInstance().screen).receive(packet.tag());
			}
		});
	}

	public static void sendDataToServer(CompoundTag tag) {
		ClientPacketDistributor.sendToServer(new DataPacket(tag));
	}

	public static void sendTo(ServerPlayer pl, CompoundTag tag) {
		PacketDistributor.sendToPlayer(pl, new DataPacket(tag));
	}
}
