package com.tom.stockbridge.block.entity;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.mutable.MutableBoolean;

import net.createmod.catnip.api.data.Iterate;
import net.createmod.catnip.api.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.logistics.packager.IdentifiedInventory;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagingRequest;
import com.simibubi.create.content.logistics.packagerLink.WiFiParticle;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.content.redstone.displayLink.LinkWithBulbBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import com.tom.stockbridge.ae.AEStockBridgeBlock;

public abstract class AbstractStockBridgeBlockEntity extends LinkWithBulbBlockEntity implements IHaveHoveringInformation {
	public BridgeBehaviour behaviour;
	public UUID placedBy;

	public AbstractStockBridgeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		setLazyTickRate(10);
		placedBy = null;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(behaviour = new BridgeBehaviour(this, true));
	}

	public abstract InventorySummary fetchSummaryFromPackager();

	public abstract Pair<PackagerBlockEntity, PackagingRequest> processRequest(ItemStack stack, int amount, String address,
			int linkIndex, MutableBoolean finalLink, int orderId, PackageOrderWithCrafts context);

	public abstract IdentifiedInventory getInvId();

	protected PackagerBlockEntity getPackager() {
		if (behaviour.redstonePower == 15)
			return null;

		return getPackager0();
	}

	private PackagerBlockEntity getPackager0() {
		for (Direction d : Iterate.directions) {
			if (!level.isLoaded(worldPosition.relative(d)))
				continue;

			var adjacentState = level.getBlockEntity(worldPosition.relative(d));
			if (adjacentState instanceof PackagerBlockEntity p)
				return p;
		}
		return null;
	}

	@Override
	public void initialize() {
		super.initialize();
		PackagerBlockEntity packager = getPackager();
		behaviour.redstonePowerChanged(AEStockBridgeBlock.getPower(level, worldPosition, packager != null ? packager.getBlockPos() : null));
		if (packager != null)
			packager.recheckIfLinksPresent();
	}

	public static interface BridgeInventory {
		AbstractStockBridgeBlockEntity getBlockEntity();
	}

	public abstract void pull(PackagingRequest packagingRequest);

	@Override
	public Direction getBulbFacing(BlockState state) {
		return Direction.UP;
	}

	@Override
	public Vec3 getBulbOffset(BlockState state) {
		return new Vec3(11 / 16f, 19 / 16f, 5 / 16f);
	}

	@Override
	public void pulse() {
		Vec3 vec3 = Vec3.atCenterOf(worldPosition);

		vec3 = vec3.add(new Vec3(0, 12 / 16f, -3 / 16f));

		super.pulse();
		level.addParticle(new WiFiParticle.Data(), vec3.x, vec3.y, vec3.z, 1, 1, 1);
	}

	public void preparePackages(List<PackagingRequest> queuedRequests) {
		PackagingRequest firstRequest = queuedRequests.get(0);
		String firstAddress = firstRequest.address();
		int firstOrderId = firstRequest.orderId();

		// Get all requests that can be combined (same address/orderId)
		for (PackagingRequest request : queuedRequests) {
			if (!request.address().equals(firstAddress) || request.orderId() != firstOrderId) {
				break;
			}

			if (!hasInventorySpace()) {
				break;
			}

			pull(request);
		}
	}

	protected abstract boolean hasInventorySpace();

	public void neighborChanged() {
		PackagerBlockEntity packager = getPackager0();
		int power = AEStockBridgeBlock.getPower(level, worldPosition, packager != null ? packager.getBlockPos() : null);
		boolean powered = power > 0;
		BlockState state = getBlockState();
		boolean previouslyPowered = state.getValue(AEStockBridgeBlock.POWERED);
		if (previouslyPowered != powered)
			level.setBlock(worldPosition, state.cycle(AEStockBridgeBlock.POWERED), 2);
		behaviour.redstonePowerChanged(power);
	}
}
