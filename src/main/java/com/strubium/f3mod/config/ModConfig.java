package com.strubium.f3mod.config;

import com.strubium.f3mod.F3IsMyShawty;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.Config;

@Config(modid = "f3ismyshawty")
public class ModConfig {

    @Config.Comment("Sections of the F3 menu to disable. For example, adding 'fps' will disable the fps counter.")
    public static String[] disabledSections = new String[]{};

    @Config.Comment("Remove the background of the debug screen.")
    public static boolean removeDebugBackground = true;

    @Config.Comment({
            "Custom text-to-color mappings.",
            "Format each entry as 'key:color'.",
            "Supported colors: BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE."
    })
    @Config.RequiresMcRestart
    public static String[] textColorMappings = new String[]{
            "fps:GREEN",
            "mem:AQUA"
    };

    static {
        // Parse the textColorMappings into the lineColors map
        reloadColors();
    }

    public static void reloadColors() {
       F3IsMyShawty.lineColors.clear();
        for (String mapping : textColorMappings) {
            String[] parts = mapping.split(":", 2);
            if (parts.length == 2) {
                try {
                    String key = parts[0].trim();
                    // Convert the string to a TextFormatting enum
                    TextFormatting color = TextFormatting.valueOf(parts[1].trim().toUpperCase());
                    F3IsMyShawty.lineColors.put(key, color);
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid color mapping: " + mapping);
                }
            }
        }
    }
}
