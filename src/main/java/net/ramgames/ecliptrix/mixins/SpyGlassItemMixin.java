package net.ramgames.ecliptrix.mixins;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpyglassItem;
import net.minecraft.registry.Registries;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.ramgames.ecliptrix.screen_handlers.SpyGlassScreenHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpyglassItem.class)
public class SpyGlassItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void openSpyGlassScreen(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if(user.isSneaking()) {
            user.openHandledScreen(new NamedScreenHandlerFactory() {
                @Override
                public Text getDisplayName() {
                    return Text.empty();
                }

                @Override
                public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                    String itemString = user.getMainHandStack().getOrCreateNbt().getString("lens");
                    ItemStack stack = new ItemStack(Registries.ITEM.containsId(new Identifier(itemString)) ? Registries.ITEM.get(new Identifier(itemString)) : Items.GLASS);
                    return new SpyGlassScreenHandler(syncId, playerInventory, new SimpleInventory(stack));
                }
            });
            cir.setReturnValue(TypedActionResult.pass(user.getMainHandStack()));
        }
    }
}
