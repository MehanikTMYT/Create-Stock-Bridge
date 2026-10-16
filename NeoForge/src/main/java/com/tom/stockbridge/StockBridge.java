package com.tom.stockbridge;

import org.slf4j.Logger;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;

import com.tom.stockbridge.ae.AEPlatformRegistration;
import com.tom.stockbridge.client.ClientRegistration;
import com.tom.stockbridge.network.NetworkHandler;

@Mod(StockBridge.MODID)
public class StockBridge {
	public static final String MODID = "createstockbridge";
	public static final Logger LOGGER = LogUtils.getLogger();

	private static CreateRegistrate registrate;

	public StockBridge(ModContainer mc, IEventBus bus) {
		// Register the setup method for modloading
		bus.addListener(this::setup);
		// Register the doClientStuff method for modloading
		bus.addListener(this::doClientStuff);
		bus.addListener(this::registerCapabilities);
		if (FMLEnvironment.getDist() == Dist.CLIENT)StockBridgeClient.preInit(mc, bus);
		bus.register(NetworkHandler.class);

		registrate = CreateRegistrate.create(MODID).registerEventListeners(bus);

		Registration.register();
	}

	private void setup(final FMLCommonSetupEvent event) {
		LOGGER.info("Create Stock Bridge starting");
		Registration.postRegister();
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		event.enqueueWork(ClientRegistration::register);
	}

	public static CreateRegistrate registrate() {
		return registrate;
	}

	private void registerCapabilities(RegisterCapabilitiesEvent event) {
		AEPlatformRegistration.registerCapabilities(event);
	}
}
