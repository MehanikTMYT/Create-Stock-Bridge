package com.tom.stockbridge.block.entity;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.joml.Math;

import net.createmod.catnip.api.data.Pair;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.IdentifiedInventory;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagingRequest;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.content.logistics.packagerLink.LogisticsManager;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

public class BridgeBehaviour extends LogisticallyLinkedBehaviour {
	public String recipeAddress = "";

	public BridgeBehaviour(SmartBlockEntity be, boolean global) {
		super(be, global);
	}

	@Override
	public InventorySummary getSummary(@Nullable IdentifiedInventory ignoredHandler) {
		if (blockEntity instanceof AbstractStockBridgeBlockEntity plbe) {
			if (ignoredHandler == null || !plbe.getInvId().equals(ignoredHandler))
				return plbe.fetchSummaryFromPackager();
		}
		return InventorySummary.EMPTY;
	}

	@Override
	public Pair<PackagerBlockEntity, PackagingRequest> processRequest(ItemStack stack, int amount, String address,
			int linkIndex, MutableBoolean finalLink, int orderId, @Nullable PackageOrderWithCrafts context,
			@Nullable IdentifiedInventory ignoredHandler) {

		if (blockEntity instanceof AbstractStockBridgeBlockEntity plbe) {
			if (ignoredHandler == null || !plbe.getInvId().equals(ignoredHandler))
				return plbe.processRequest(stack, amount, address, linkIndex, finalLink, orderId, context);
		}

		return null;
	}

	@Override
	public void initialize() {
		super.initialize();
		if (getWorld().isClientSide())
			return;

		if (blockEntity instanceof AbstractStockBridgeBlockEntity plbe)
			Create.LOGISTICS.linkAdded(freqId, getGlobalPos(), plbe.placedBy);
	}

	private GlobalPos getGlobalPos() {
		return GlobalPos.of(getWorld().dimension(), getPos());
	}

	public int requestItem(ItemStack stack, int demand) {
		if (blockEntity instanceof AbstractStockBridgeBlockEntity plbe) {
			var id = plbe.getInvId();
			int availableOnNetwork = LogisticsManager.getStockOf(freqId, stack, id);
			if (availableOnNetwork == 0) {
				return 0;
			}

			int maxStackSize = stack.getMaxStackSize();
			int amountToOrder = Math.clamp(demand, 0, maxStackSize * 9);

			BigItemStack orderedItem = new BigItemStack(stack, Math.min(amountToOrder, availableOnNetwork));
			PackageOrderWithCrafts order = PackageOrderWithCrafts.simple(List.of(orderedItem));

			if (!LogisticsManager.broadcastPackageRequest(freqId, RequestType.RESTOCK, order,
					id, recipeAddress))
				return 0;

			return amountToOrder;
		}
		return 0;
	}

	@Override
	public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
		super.writeSafe(tag, registries);
		tag.putString("address", recipeAddress);
	}

	@Override
	public void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		tag.putString("address", recipeAddress);
	}

	@Override
	public void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		recipeAddress = tag.getStringOr("address", "");
	}

	public InventorySummary getItems() {
		if (blockEntity instanceof AbstractStockBridgeBlockEntity plbe) {
			var id = plbe.getInvId();
			InventorySummary summaryOfLinks = new InventorySummary();
			LogisticallyLinkedBehaviour.getAllPresent(freqId, false)
			.forEach(link -> {
				InventorySummary summary = link.getSummary(id);
				if (summary != InventorySummary.EMPTY)
					summaryOfLinks.contributingLinks++;
				summaryOfLinks.add(summary);
			});
			return summaryOfLinks;
		}
		return InventorySummary.EMPTY;
	}
}
