package com.example.elementalwands;

import com.example.elementalwands.item.GlacialWand;
import com.example.elementalwands.item.InfernoWand;
import com.example.elementalwands.item.StormWand;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ElementalWandsMod.MOD_ID);

    public static final RegistryObject<Item> INFERNO_WAND =
            ITEMS.register("inferno_wand", InfernoWand::new);

    public static final RegistryObject<Item> GLACIAL_WAND =
            ITEMS.register("glacial_wand", GlacialWand::new);

    public static final RegistryObject<Item> STORM_WAND =
            ITEMS.register("storm_wand", StormWand::new);
}
