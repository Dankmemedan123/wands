package com.example.elementalwands;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ElementalWandsMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> ELEMENTAL_TAB =
            TABS.register("elemental_wands", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.elementalwands"))
                    // FIX: Defensive null-check on the icon supplier — if the item somehow
                    // hasn't registered yet, fall back to an empty stack rather than crashing
                    .icon(() -> {
                        var item = ModItems.STORM_WAND.get();
                        return item != null ? new ItemStack(item) : ItemStack.EMPTY;
                    })
                    .displayItems((params, output) -> {
                        output.accept(ModItems.INFERNO_WAND.get());
                        output.accept(ModItems.GLACIAL_WAND.get());
                        output.accept(ModItems.STORM_WAND.get());
                    })
                    .build());
}
