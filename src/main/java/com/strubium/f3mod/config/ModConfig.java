package com.strubium.f3mod.config;

import net.minecraftforge.common.config.Config;

@Config(modid = "f3menuconfig", category = "general")
public class ModConfig {

    @Config.Comment("Sections of the F3 menu to disable. For example, adding 'fps' will disable the fps counter")
    public static String[] disabledSections = new String[]{};
}

