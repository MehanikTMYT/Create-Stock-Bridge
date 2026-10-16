package com.tom.stockbridge.data;

import java.util.concurrent.CompletableFuture;

import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;

import com.simibubi.create.AllBlocks;
import com.tom.stockbridge.ae.AERegistration;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;

// PORT(26.1): recipe providers are built per run from a registry lookup and wrapped in a Runner.
public class SBRecipes extends RecipeProvider {
	protected SBRecipes(HolderLookup.Provider registries, RecipeOutput output) {
		super(registries, output);
	}

	@Override
	protected void buildRecipes() {
		shaped(RecipeCategory.MISC, AERegistration.BRIDGE_BLOCK.get())
		.pattern("bsb")
		.pattern("eIe")
		.pattern("iti")
		.define('s', AllBlocks.STOCK_LINK)
		.define('I', AEBlocks.INTERFACE)
		.define('t', AllBlocks.STOCK_TICKER)
		.define('b', TagKey.create(Registries.ITEM, Identifier.parse("c:plates/brass")))
		.define('i', TagKey.create(Registries.ITEM, Identifier.parse("c:plates/iron")))
		.define('e', AEItems.ENGINEERING_PROCESSOR)
		.group("create")
		.unlockedBy("stuff", inventoryTrigger(ItemPredicate.Builder.item().of(this.items, AllBlocks.STOCK_LINK, AEBlocks.INTERFACE)))
		.save(this.output);
	}

	public static class Runner extends RecipeProvider.Runner {
		public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
			super(output, registries);
		}

		@Override
		protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
			return new SBRecipes(registries, output);
		}

		@Override
		public String getName() {
			return "Create Stock Bridge recipes";
		}
	}
}
