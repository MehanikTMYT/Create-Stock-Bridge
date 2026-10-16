package com.tom.stockbridge.ae;

import com.simibubi.create.foundation.data.CreateDataProviders;
import com.tterrag.registrate.providers.generators.ConfiguredModel;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import com.simibubi.create.content.redstone.displayLink.LinkBulbRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;

import com.tom.stockbridge.Registration;
import com.tom.stockbridge.StockBridge;
import com.tom.stockbridge.ae.menu.AEStockBridgeMenu;
import com.tom.stockbridge.ae.menu.RemoteItemStrategy;

import appeng.api.behaviors.ContainerItemStrategies;
import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.IPatternDetailsDecoder;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.ids.AECreativeTabIds;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKeyType;
import appeng.core.AppEng;

public class AERegistration {
	private static final CreateRegistrate REGISTRATE = StockBridge.registrate();

	public static final BlockEntry<AEStockBridgeBlock> BRIDGE_BLOCK = REGISTRATE.block("ae_bridge", AEStockBridgeBlock::new)
			.initialProperties(SharedProperties::copperMetal)
			.properties(p -> p.noOcclusion())
			.tag(BlockTags.NEEDS_IRON_TOOL)
			.transform(TagGen.pickaxeOnly())
			// PORT(26.1): Registrate's 26.1 model generators, through Create's old-vocabulary adapter.
			.blockstate(CreateDataProviders.blockstate(() -> (ctx, prov) -> prov.getVariantBuilder(ctx.getEntry()).forAllStates(st -> {
				boolean pw = st.getValue(AEStockBridgeBlock.POWERED);
				return ConfiguredModel.builder()
						.modelFile(prov.models().getExistingFile(prov.modLoc(pw ? "ae_bridge_powered" : "ae_bridge")))
						.build();
			})))
			.item(LogisticallyLinkedBlockItem::new)
			.transform(b -> b.model(CreateDataProviders.itemModel(() -> (c, p) -> {
				p.withExistingParent("ae_bridge",
						p.modLoc("block/ae_bridge"));
			})).build())
			.lang("AE Stock Bridge")
			.register();

	public static final BlockEntityEntry<AEStockBridgeBlockEntity> BRIDGE_TILE = REGISTRATE
			.blockEntity("ae_bridge", AEStockBridgeBlockEntity::new)
			.renderer(() -> LinkBulbRenderer::new)
			.validBlocks(BRIDGE_BLOCK)
			.register();

	private static final ResourceKey<Registry<AEKeyType>> AE_TYPE_REGISTRY = ResourceKey.createRegistryKey(AppEng.makeId("keytypes"));
	public static final RegistryEntry<AEKeyType, RemoteItemKeyType> REMOTE_KEY_TYPE = REGISTRATE.generic("ri", AE_TYPE_REGISTRY, () -> RemoteItemKeyType.INSTANCE).register();

	public static final RegistryEntry<DataComponentType<?>, DataComponentType<AEItemKey>> VIRTUAL_PATTERN_OUT = REGISTRATE.generic("virtual_pattern", Registries.DATA_COMPONENT_TYPE,
			() -> DataComponentType.<AEItemKey>builder().persistent(AEItemKey.CODEC).build()
			).register();

	static {
		REGISTRATE.generic("ae_bridge", Registries.MENU, () -> AEStockBridgeMenu.TYPE).register();

		REGISTRATE.modifyCreativeModeTab(AECreativeTabIds.MAIN, b -> b.accept(BRIDGE_BLOCK.asItem(), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY));
		REGISTRATE.modifyCreativeModeTab(AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey(), b -> b.accept(BRIDGE_BLOCK.asItem(), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY));
	}

	public static void register() {
	}

	public static void postRegister() {
		ContainerItemStrategies.register(RemoteItemKeyType.INSTANCE, AERemoteItemKey.class, new RemoteItemStrategy());
		PatternDetailsHelper.registerDecoder(new IPatternDetailsDecoder() {

			@Override
			public boolean isEncodedPattern(ItemStack is) {
				return is.getItem() == Registration.REQUEST_ITEM.get();
			}

			@Override
			public @Nullable IPatternDetails decodePattern(AEItemKey stack, Level p1) {
				if (stack == null)return null;
				var is = stack.get(VIRTUAL_PATTERN_OUT.get());
				if (is != null) {
					return VirtualPattern.of(is);
				}
				return null;
			}
		});
	}
}
