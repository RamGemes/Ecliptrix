package net.ramgames.ecliptrix.screen_handlers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.ramgames.ecliptrix.Ecliptrix;
import net.ramgames.ecliptrix.ModTags;

public class SpyGlassScreenHandler extends ScreenHandler {

    public static final ScreenHandlerType<SpyGlassScreenHandler> SPY_GLASS_SCREEN_HANDLER_SCREEN_HANDLER_TYPE = new ScreenHandlerType<>(SpyGlassScreenHandler::new, FeatureFlags.VANILLA_FEATURES);

    private final Inventory inventory;
    private final Slot slot;

    public SpyGlassScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(1));
    }

    public SpyGlassScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(SPY_GLASS_SCREEN_HANDLER_SCREEN_HANDLER_TYPE, syncId);
        checkSize(inventory, 1);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
        int m;
        int l;
        int index = 0;
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, index++, 8 + m * 18, 125+17));
        }
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, index++, 8 + l * 18, 67 + m * 18+17));
            }
        }
        slot = new Slot(this.inventory, 0, 80, 50){

            @Override
            public boolean canTakeItems(PlayerEntity playerEntity) {
                Ecliptrix.LOGGER.info("is null?");
                if(getCursorStack() == null) return false;
                Ecliptrix.LOGGER.info("is empty?");
                if(getCursorStack().isEmpty()) return false;
                Ecliptrix.LOGGER.info("has tag?");
                if(!getCursorStack().isIn(ModTags.LENSES)) return false;
                Ecliptrix.LOGGER.info("allowing");
                return super.canTakeItems(playerEntity);
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }

            @Override
            public int getMaxItemCount(ItemStack stack) {
                return getMaxItemCount();
            }
        };
        this.addSlot(slot);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return SPY_GLASS_SCREEN_HANDLER_SCREEN_HANDLER_TYPE;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        player.getMainHandStack().getOrCreateNbt().putString("lens", Registries.ITEM.getId(inventory.getStack(0).getItem()).toString());
        super.onClosed(player);
    }
}
