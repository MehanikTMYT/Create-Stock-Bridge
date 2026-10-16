package com.tom.stockbridge.data;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.neoforged.neoforge.common.CommonHooks;
import java.util.function.BiConsumer;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import com.tom.stockbridge.StockBridge;
import com.tom.stockbridge.client.ClientRegistration;
import com.tterrag.registrate.providers.ProviderType;

import net.createmod.ponder.api.client.PonderIndex;

// PORT(26.1): GatherDataEvent is fired per mod and split into Client/Server runs; like Registrate and Create,
// everything (client assets and server data) is generated from the client run.
@EventBusSubscriber(modid = StockBridge.MODID)
public class DataGenerators {

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void gatherDataLang(GatherDataEvent.Client event) {
		if (StockBridge.MODID.equals(event.getModContainer().getModId())) {
			bindDefaultItemComponents(event.getLookupProvider());
			StockBridge.registrate().addDataGenerator(ProviderType.LANG, provider -> {
				BiConsumer<String, String> langConsumer = provider::add;
				providePonderLang(langConsumer);
			});
		}
	}

	/**
	 * PORT(26.1): default item components are bound only when server resources load, which a data run never does,
	 * so building an ItemStack (Ponder's scene/tag icons) throws "Components not bound yet". Same fix as Create's
	 * CreateDatagen: bind them once from the datagen registry lookup.
	 */
	private static void bindDefaultItemComponents(CompletableFuture<HolderLookup.Provider> lookupProvider) {
		CommonHooks.markComponentClassAsValid(HolderSet.emptyNamed(new HolderOwner<BannerPattern>() {},
				TagKey.create(Registries.BANNER_PATTERN, Identifier.fromNamespaceAndPath(StockBridge.MODID, "datagen"))).getClass());
		BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(lookupProvider.join())
				.forEach(DataComponentInitializers.PendingComponents::apply);
	}

	private static void providePonderLang(BiConsumer<String, String> consumer) {
		// Register this since FMLClientSetupEvent does not run during datagen
		ClientRegistration.register();
		PonderIndex.getLangAccess().provideLang(StockBridge.MODID, consumer);
	}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		if (!StockBridge.MODID.equals(event.getModContainer().getModId())) {
			return;
		}
		event.getGenerator().addProvider(true, new SBRecipes.Runner(event.getGenerator().getPackOutput(), event.getLookupProvider()));
	}
}
