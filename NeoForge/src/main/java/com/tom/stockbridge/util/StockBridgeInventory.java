package com.tom.stockbridge.util;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

import com.tom.stockbridge.block.entity.AbstractStockBridgeBlockEntity;
import com.tom.stockbridge.block.entity.AbstractStockBridgeBlockEntity.BridgeInventory;

public class StockBridgeInventory implements IItemHandler, BridgeInventory {
	private SimpleContainer insert = new SimpleContainer(9);
	private SimpleContainer extract = new SimpleContainer(9);
	public InvWrapper insertW = new InvWrapper(insert);
	public InvWrapper extractW = new InvWrapper(extract);

	private InvWrapper insertOnly = new InvWrapper(insert) {
		@Override
		public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
			return ItemStack.EMPTY;
		}
	};
	private InvWrapper extractOnly = new InvWrapper(extract) {
		@Override
		public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
			return stack;
		}

		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			return false;
		}
	};
	private IItemHandler inv = new CombinedInvWrapper(insertOnly, extractOnly);
	private final AbstractStockBridgeBlockEntity be;

	public StockBridgeInventory(AbstractStockBridgeBlockEntity be) {
		this.be = be;
	}

	@Override
	public int getSlots() {
		return inv.getSlots();
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot) {
		return inv.getStackInSlot(slot);
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		return inv.insertItem(slot, stack, simulate);
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		return inv.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return inv.getSlotLimit(slot);
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return inv.isItemValid(slot, stack);
	}

	public boolean isInsertEmpty() {
		return insert.isEmpty();
	}

	@Override
	public AbstractStockBridgeBlockEntity getBlockEntity() {
		return be;
	}

	// PORT(26.1): containers serialize as typed item lists through ValueInput/ValueOutput.
	public void read(ValueInput input) {
		insert.fromItemList(input.listOrEmpty("in", ItemStack.CODEC));
		extract.fromItemList(input.listOrEmpty("out", ItemStack.CODEC));
	}

	public void write(ValueOutput output) {
		insert.storeAsItemList(output.list("in", ItemStack.CODEC));
		extract.storeAsItemList(output.list("out", ItemStack.CODEC));
	}
}
