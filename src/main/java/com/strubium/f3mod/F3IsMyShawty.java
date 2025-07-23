package com.strubium.f3mod;

import com.strubium.f3mod.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraft.util.text.TextFormatting;

import java.util.*;

@Mod(modid = "f3ismyshawty", name = "F3 Is My Shawty", version = "1.0.5", clientSideOnly = true)
public class F3IsMyShawty {

    // Lowercase cache for faster comparison
    public static String[] disabledSectionsLower = new String[]{};

    public static Minecraft mc;

    public static final Map<String, TextFormatting> lineColors = new HashMap<>();
    private static List<String> cachedLeftText;
    private static List<String> cachedRightText;
    private static int lastHash = 0;

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event){
        ModConfig.reloadColors();
        mc = Minecraft.getMinecraft();
    }

    @EventBusSubscriber(Side.CLIENT)
    public static class EventHandlers {

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onRenderDebug(RenderGameOverlayEvent.Text event) {

            if (mc.gameSettings.showDebugInfo) {
                mc.profiler.startSection("debugScreen");

                List<String> leftText = new ArrayList<>(event.getLeft());
                List<String> rightText = new ArrayList<>(event.getRight());

                // Remove any disabled sections from the debug text
                leftText.removeIf(EventHandlers::isDisabled);
                rightText.removeIf(EventHandlers::isDisabled);

                // Apply formatting
                leftText.replaceAll(EventHandlers::applyFormatting);
                rightText.replaceAll(EventHandlers::applyFormatting);

                if (ModConfig.cacheEnabled) {
                    mc.profiler.startSection("cacheComparison");

                    // Compute new hash for the text lists (only visible sections)
                    int newHash = Arrays.hashCode(new int[] {
                            leftText.hashCode(),
                            rightText.hashCode(),
                            leftText.size(),
                            rightText.size()
                    });

                    // Check if the hash has changed and update cache accordingly
                    if (newHash != lastHash) {
                        mc.profiler.startSection("cacheMiss");
                        // Cache miss: update the cache and log
                        cachedLeftText = new ArrayList<>(leftText);
                        cachedRightText = new ArrayList<>(rightText);
                        lastHash = newHash;

                        mc.profiler.endSection(); // End cacheMiss section
                    }

                    mc.profiler.endSection(); // End cacheComparison section
                } else {
                    // If cache is disabled, just set the left and right text to the current state
                    cachedLeftText = new ArrayList<>(leftText);
                    cachedRightText = new ArrayList<>(rightText);
                }

                if (ModConfig.removeDebugBackground) {
                    event.setCanceled(true);
                    renderTextWithoutBackground(cachedLeftText, cachedRightText);
                }
                mc.profiler.endSection();
            }
        }

        // Helper method to check if a section should be disabled
        private static boolean isDisabled(String line) {
            String lower = line.toLowerCase(Locale.ROOT);
            for (String section : disabledSectionsLower) {
                if (lower.contains(section)) return true;
            }
            return false;
        }

        private static String applyFormatting(String line) {
            for (Map.Entry<String, TextFormatting> entry : lineColors.entrySet()) {
                if (line.toLowerCase(Locale.ROOT).contains(entry.getKey().toLowerCase(Locale.ROOT))) {
                    return entry.getValue() + line + TextFormatting.RESET;
                }
            }
            return line;
        }

        private static void renderTextWithoutBackground(List<String> leftText, List<String> rightText) {
            mc.profiler.startSection("shawtyDebugScreen");

            ScaledResolution scaledResolution = new ScaledResolution(mc);
            int screenWidth = scaledResolution.getScaledWidth();
            int leftMargin = 2;
            int rightMargin = 2;

            for (int i = 0; i < leftText.size(); i++) {
                mc.fontRenderer.drawString(leftText.get(i), leftMargin, 2 + i * mc.fontRenderer.FONT_HEIGHT, 0xFFFFFF);
            }

            for (int i = 0; i < rightText.size(); i++) {
                int textWidth = mc.fontRenderer.getStringWidth(rightText.get(i));
                mc.fontRenderer.drawString(rightText.get(i), screenWidth - rightMargin - textWidth, 2 + i * mc.fontRenderer.FONT_HEIGHT, 0xFFFFFF);
            }
            mc.profiler.endSection();
        }
    }
}
