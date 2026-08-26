package com.fxw.blizzardvilladisplay.client.rendering.screens;

import net.minecraft.resources.ResourceLocation;

public record DetectiveConfig(
        String status,
        ResourceLocation bgTexture,
        ResourceLocation confirmTexture,
        ResourceLocation cancelTexture
) {}
