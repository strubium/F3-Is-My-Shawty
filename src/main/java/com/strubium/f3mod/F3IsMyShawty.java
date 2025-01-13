package com.strubium.f3mod;

import com.strubium.f3mod.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.Arrays;
import java.util.List;

@Mod(modid = "f3ismyshawty", name = "F3 Is My Shawty", version = "1.0.0", clientSideOnly = true)
public class F3IsMyShawty {

    @Mod.Instance
    public static F3IsMyShawty instance;

    @EventBusSubscriber(Side.CLIENT)
    public static class EventHandlers {

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onRenderDebug(RenderGameOverlayEvent.Text event) {
            if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
                List<String> leftText = event.getLeft();
                List<String> rightText = event.getRight();

                // Filter out disabled sections
                leftText.removeIf(line -> isDisabled(line));
                rightText.removeIf(line -> isDisabled(line));
            }
        }

        private static boolean isDisabled(String line) {
            return Arrays.stream(ModConfig.disabledSections)
                    .anyMatch(section -> line.toLowerCase().contains(section.toLowerCase()));
        }
    }
}
