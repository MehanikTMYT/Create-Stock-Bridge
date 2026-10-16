package com.tom.stockbridge;

import com.simibubi.create.foundation.data.CreateDataProviders;
import net.minecraft.world.item.Item;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;

import com.tom.stockbridge.ae.AERegistration;

public class Registration {
	private static final CreateRegistrate REGISTRATE = StockBridge.registrate();

	public static final ItemEntry<Item> REQUEST_ITEM = REGISTRATE.item("request_pattern", Item::new)
			.lang("Request Pattern")
			.model(CreateDataProviders.itemModel(() -> (c, p) -> {
				p.singleTexture("request_pattern", p.mcLoc("item/generated"), "layer0", p.mcLoc("item/barrier"));
			}))
			.register();

	public static void register() {
		AERegistration.register();

		add("gui.stockbridge.remote_items", "Create Stock System Items");
		add("tooltip.stockbridge.remote_item", "Item in Remote System");
		add("gui.createstockbridge.address", "Package Address:");
	}

	public static void postRegister() {
		AERegistration.postRegister();
	}

	public static void add(String key, String value) {
		REGISTRATE.addRawLang(key, value);
	}
}
