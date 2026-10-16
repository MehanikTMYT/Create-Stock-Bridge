package com.tom.stockbridge.ae;

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import com.tom.stockbridge.ae.menu.AEStockBridgeMenu;
import com.tom.stockbridge.ae.menu.AEStockBridgeScreen;

import appeng.client.InitScreens;

public class AEClientPlatformRegistration {

	public static void registerScreens(RegisterMenuScreensEvent e) {
		InitScreens.register(e, AEStockBridgeMenu.TYPE, AEStockBridgeScreen::new, "/screens/csb_bridge.json");
	}
}
