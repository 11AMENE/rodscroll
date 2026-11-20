package com.example.rodscroll;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class RodGui extends GuiScreen {

    @Override
    public void initGui() {
        buttonList.clear();
        int startX = width / 2 - 99;
        for (int i = 0; i < 9; i++) {
            buttonList.add(new GuiButton(i, startX + i * 22, height / 2 - 48, 20, 20, String.valueOf(i + 1)));
            buttonList.add(new GuiButton(10 + i, startX + i * 22, height / 2 - 6, 20, 20, String.valueOf(i + 1)));
        }
        buttonList.add(new GuiButton(200, width / 2 - 70, height / 2 + 36, 20, 20, "-"));
        buttonList.add(new GuiButton(201, width / 2 + 50, height / 2 + 36, 20, 20, "+"));
        buttonList.add(new GuiButton(100, width / 2 - 50, height / 2 + 68, 100, 20, "Done"));
        refresh();
    }

    private void refresh() {
        for (GuiButton b : buttonList) {
            if (b.id < 9) {
                int slot = b.id;
                boolean selected = slot == RodScroll.rodSlot;
                boolean blocked = slot == RodScroll.emptySlot;
                b.enabled = !selected && !blocked;
                b.displayString = selected ? "\u00a7a" + (slot + 1) : String.valueOf(slot + 1);
            } else if (b.id >= 10 && b.id < 19) {
                int slot = b.id - 10;
                boolean selected = slot == RodScroll.emptySlot;
                boolean blocked = slot == RodScroll.rodSlot;
                b.enabled = !selected && !blocked;
                b.displayString = selected ? "\u00a7a" + (slot + 1) : String.valueOf(slot + 1);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 100) {
            mc.displayGuiScreen(null);
            return;
        }
        if (button.id < 9) {
            RodScroll.rodSlot = button.id;
        } else if (button.id >= 10 && button.id < 19) {
            RodScroll.emptySlot = button.id - 10;
        } else if (button.id == 200) {
            RodScroll.delay = Math.max(0, RodScroll.delay - 1);
        } else if (button.id == 201) {
            RodScroll.delay = Math.min(20, RodScroll.delay + 1);
        }
        RodScroll.save();
        refresh();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "Rod Scroll", width / 2, height / 2 - 90, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "Rod slot", width / 2, height / 2 - 60, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "Empty slot", width / 2, height / 2 - 18, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "Delay: " + RodScroll.delay + " ticks", width / 2, height / 2 + 42, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
