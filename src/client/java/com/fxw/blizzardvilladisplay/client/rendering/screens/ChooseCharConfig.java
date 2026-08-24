package com.fxw.blizzardvilladisplay.client.rendering.screens;

import net.minecraft.resources.ResourceLocation;

public record ChooseCharConfig (
        String charId,
        ResourceLocation bgTexture,
        ResourceLocation confirmTexture,
        ResourceLocation cancelTexture
) {}

