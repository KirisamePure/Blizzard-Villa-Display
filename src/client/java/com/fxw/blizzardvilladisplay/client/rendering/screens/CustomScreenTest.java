package com.fxw.blizzardvilladisplay.client.rendering.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CustomScreenTest extends Screen {
    public CustomScreenTest(Component component) {
        super(component);
    }
    @Override
    public void onClose() {
        this.minecraft.setScreen(null);
    }
    @Override
    protected void init() {
        Button buttonWidget = Button.builder(Component.nullToEmpty("Hello World!"), (btn) -> {
            this.minecraft.getToastManager().addToast(
                    SystemToast.multiline(this.minecraft, SystemToast.SystemToastId.NARRATOR_TOGGLE, Component.nullToEmpty("Hello World!"), Component.nullToEmpty("This is a toast."))
            );
        }).bounds(40, 40, 120, 20).build();
        this.addRenderableWidget(buttonWidget);
    }
    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawString(this.font, "Special Button", 40, 40 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
    }
}
