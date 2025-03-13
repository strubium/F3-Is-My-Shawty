package com.strubium.f3mod;

import com.strubium.f3mod.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mod(modid = "f3ismyshawty", name = "F3 Is My Shawty", version = "1.0.1", clientSideOnly = true)
public class F3IsMyShawty {

    public static final Map<String, TextFormatting> lineColors = new HashMap<>();

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        ModConfig.reloadColors();
    }

    @EventBusSubscriber(Side.CLIENT)
    public static class EventHandlers {

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onRenderDebug(RenderGameOverlayEvent.Text event) {
            Minecraft mc = Minecraft.getMinecraft();

            if (mc.gameSettings.showDebugInfo) {
                mc.profiler.startSection("debugScreen");
                List<String> leftText = event.getLeft();
                List<String> rightText = event.getRight();

                // Filter out disabled sections and apply formatting
                leftText.removeIf(EventHandlers::isDisabled);
                rightText.removeIf(EventHandlers::isDisabled);

                leftText.replaceAll(EventHandlers::applyFormatting);
                rightText.replaceAll(EventHandlers::applyFormatting);

                // Remove debug background if the config is enabled
                if (ModConfig.removeDebugBackground) {
                    event.setCanceled(true); // Cancel the default background rendering
                    // Manually render the text without background
                    renderTextWithoutBackground(leftText, rightText);
                }
                mc.profiler.endSection();
            }
        }

        private static boolean isDisabled(String line) {
            return Arrays.stream(ModConfig.disabledSections)
                    .anyMatch(section -> line.toLowerCase().contains(section.toLowerCase()));
        }

        private static String applyFormatting(String line) {
            // Iterate over the lineColors map to find a matching key
            for (Map.Entry<String, TextFormatting> entry : lineColors.entrySet()) {
                String key = entry.getKey().toLowerCase();
                if (line.toLowerCase().contains(key)) {
                    // Apply the color formatting and reset at the end
                    return entry.getValue() + line + TextFormatting.RESET;
                }
            }
            // Return the original line if no match is found
            return line;
        }

        private static void renderTextWithoutBackground(List<String> leftText, List<String> rightText) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.profiler.startSection("shawtyDebugScreen");

            ScaledResolution scaledResolution = new ScaledResolution(mc);
            int screenWidth = scaledResolution.getScaledWidth(); // Correct scaled width
            int leftMargin = 2;
            int rightMargin = 2;

            // Filter out disabled sections
            List<String> filteredLeftText = leftText.stream()
                    .filter(line -> !isDisabled(line))
                    .collect(Collectors.toList());
            List<String> filteredRightText = rightText.stream()
                    .filter(line -> !isDisabled(line))
                    .collect(Collectors.toList());

            // Render the left-aligned text
            for (int i = 0; i < filteredLeftText.size(); i++) {
                String text = filteredLeftText.get(i);
                int yPosition = 2 + i * 10; // Adjust line spacing as needed
                mc.fontRenderer.drawString(text, leftMargin, yPosition, 0xFFFFFF);
            }

            // Render the right-aligned text
            for (int i = 0; i < filteredRightText.size(); i++) {
                String text = filteredRightText.get(i);
                int textWidth = mc.fontRenderer.getStringWidth(text);
                int xPosition = screenWidth - rightMargin - textWidth; // Use scaled width
                int yPosition = 2 + i * 10; // Adjust line spacing as needed
                mc.fontRenderer.drawString(text, xPosition, yPosition, 0xFFFFFF);
            }
            mc.profiler.endSection();
        }
    }
}
