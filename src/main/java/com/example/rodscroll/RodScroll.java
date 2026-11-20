package com.example.rodscroll;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = "rodscroll", version = "1.0")
public class RodScroll {
    public static int rodSlot = 1;    // 0-indexed (slot 2)
    public static int emptySlot = 5;  // 0-indexed (slot 6)
    public static int delay = 2;      // ticks to wait between steps
    private static Configuration config;

    private final KeyBinding key = new KeyBinding("Rod Scroll", Keyboard.KEY_APOSTROPHE, "RodScroll");
    private final KeyBinding menuKey = new KeyBinding("Rod Scroll Menu", Keyboard.KEY_BACKSLASH, "RodScroll");
    private boolean onRod = false;
    private int counter = 0;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        config = new Configuration(e.getSuggestedConfigurationFile());
        rodSlot = clamp(config.get("general", "rodSlot", 2).getInt(2), 1, 9) - 1;
        emptySlot = clamp(config.get("general", "emptySlot", 6).getInt(6), 1, 9) - 1;
        delay = clamp(config.get("general", "delay", 2).getInt(2), 0, 20);
        if (rodSlot == emptySlot) emptySlot = (rodSlot + 1) % 9;
        config.save();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        ClientRegistry.registerKeyBinding(key);
        ClientRegistry.registerKeyBinding(menuKey);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    public static void save() {
        config.get("general", "rodSlot", 2).set(rodSlot + 1);
        config.get("general", "emptySlot", 6).set(emptySlot + 1);
        config.get("general", "delay", 2).set(delay);
        config.save();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        if (mc.currentScreen == null && menuKey.isPressed()) {
            mc.displayGuiScreen(new RodGui());
            return;
        }
        if (mc.currentScreen != null || !key.isKeyDown()) {
            counter = 0;
            return;
        }
        if (counter > 0) {
            counter--;
            return;
        }
        counter = delay;

        InventoryPlayer inv = mc.thePlayer.inventory;
        if (!onRod) {
            ItemStack rod = inv.getStackInSlot(rodSlot);
            if (rod == null || !(rod.getItem() instanceof ItemFishingRod)) return;
            inv.currentItem = rodSlot;
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(rodSlot));
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, rod);
            onRod = true;
        } else {
            inv.currentItem = emptySlot;
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(emptySlot));
            onRod = false;
        }
    }
}
