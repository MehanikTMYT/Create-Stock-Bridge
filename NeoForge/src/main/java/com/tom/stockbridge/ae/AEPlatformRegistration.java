package com.tom.stockbridge.ae;

import com.simibubi.create.foundation.capability.CreateCapabilities;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import appeng.api.AECapabilities;

public class AEPlatformRegistration {
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		// PORT(26.1): item capability is a ResourceHandler; Create's adapter hands the same StockBridgeInventory
		// back to Create's packager, so its BridgeInventory check keeps working.
		event.registerBlockEntity(Capabilities.Item.BLOCK, AERegistration.BRIDGE_TILE.get(), (be, side) -> CreateCapabilities.items(be.getInv()));
		event.registerBlockEntity(AECapabilities.IN_WORLD_GRID_NODE_HOST, AERegistration.BRIDGE_TILE.get(), (be, _v) -> be);
	}
}
