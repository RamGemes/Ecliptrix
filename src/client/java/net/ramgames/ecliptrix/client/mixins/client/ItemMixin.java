package net.ramgames.ecliptrix.client.mixins.client;

import net.minecraft.client.item.TooltipData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.ramgames.ecliptrix.client.items.SpyglassTooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "getTooltipData", at = @At("HEAD"), cancellable = true)
    private void getTooltipDataMixin(ItemStack stack, CallbackInfoReturnable<Optional<TooltipData>> cir) {
        if(stack.getItem() == Items.SPYGLASS) getSpyglassTooltip(stack, cir);
    }

    @Unique
    private void getSpyglassTooltip(ItemStack stack, CallbackInfoReturnable<Optional<TooltipData>> cir) {
        if(stack.getNbt() == null) {
            cir.setReturnValue(Optional.of(new SpyglassTooltipData("minecraft:glass")));
            return;
        }
        NbtCompound nbt = stack.getNbt();
        cir.setReturnValue(Optional.of(new SpyglassTooltipData(!nbt.contains("lens") ? "minecraft:glass" : nbt.getString("lens"))));
    }

}
