package com.example.elementalwands;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ElementalWandsMod.MOD_ID)
public class ElementalWandsMod {

    public static final String MOD_ID = "elementalwands";

    public ElementalWandsMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
    }
}
