package com.tom.stockbridge.ae.menu;

import org.jetbrains.annotations.Nullable;

import net.createmod.catnip.api.client.gui.ScreenOpener;
import net.createmod.ponder.impl.client.gui.PonderUI;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import com.tom.stockbridge.ae.AERegistration;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import guideme.PageAnchor;

public class AEStockBridgeScreen extends AEBaseScreen<AEStockBridgeMenu> {
	private final EditBox address;

	public AEStockBridgeScreen(AEStockBridgeMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
		super(menu, playerInventory, title, style);
		this.widgets.addOpenPriorityButton();

		this.address = this.widgets.addTextField("address");
		this.address.setMaxLength(32);
		this.address.setResponder(menu::setAddress);
	}

	@Override
	protected void updateBeforeRender() {
		super.updateBeforeRender();
		this.address.setValue(menu.getAddress());
	}

	@Override
	protected void init() {
		super.init();
		this.setInitialFocus(this.address);
	}

	@Override
	public void drawBG(final GuiGraphicsExtractor guiGraphics, final int offsetX, final int offsetY, final int mouseX,
			final int mouseY, final float partialTicks) {
		super.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTicks);
		this.address.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
	}

	@Override
	protected @Nullable PageAnchor getHelpTopic() {
		return new PageAnchor(null, null);//Dummy
	}

	@Override
	protected void openHelp() {
		ScreenOpener.transitionTo(PonderUI.of(AERegistration.BRIDGE_BLOCK.asStack()));
	}
}
