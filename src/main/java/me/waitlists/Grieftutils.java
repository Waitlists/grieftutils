package me.waitlists;

import me.waitlists.mods.CarpetBotBombing;
import me.waitlists.mods.CopyIPKeybind;
import me.waitlists.mods.DisconnectKeybind;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.lwjgl.glfw.GLFW;

import java.awt.Color;

public class Grieftutils implements ModInitializer {
    private static int dayCounter = 0;
    private static float hue = 0.0f;
    private boolean displayHud = false;
    private KeyBinding toggleHudKey;

    public void onInitialize() {

        FabricLoader.getInstance().getModContainer("grieftutils").ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of("grieftutils:background"),  modContainer, ResourcePackActivationType.ALWAYS_ENABLED);
        });


        DisconnectKeybind.register();
        CopyIPKeybind.register();
        CarpetBotBombing.register();


        // Register key binding to toggle HUD display
        toggleHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Toggle HUD Rendering",
                GLFW.GLFW_KEY_UNKNOWN,
                "Grieft Utils"
        ));


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null && !client.isPaused()) {
                dayCounter = (int) (client.world.getTimeOfDay() / 24000L) + 1;
                hue += 0.01f;
                if (hue > 1.0f) {
                    hue = 0.0f;
                }


                if (toggleHudKey.wasPressed()) {
                    displayHud = !displayHud;
                    displayHudStatusActionBar();
                }
            }
        });


        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (displayHud) {
                renderHud(drawContext);
            }
        });
    }

    private void displayHudStatusActionBar() {
        String statusMessage = displayHud ? "HUD is ON" : "HUD is OFF";
        int color = displayHud ? 0x00FF00 : 0xFF0000; // Green for ON, Red for OFF
        MinecraftClient.getInstance().player.sendMessage(
                Text.literal(statusMessage).styled(style -> style.withColor(color)), true);
    }

    private void renderHud(net.minecraft.client.gui.DrawContext drawContext) {
        MinecraftClient client = MinecraftClient.getInstance();


        if (client.options.hudHidden) {
            return;
        }

        if (client.player != null && client.world != null) {
            int baseX = client.getWindow().getScaledWidth() - 5;
            int baseY = 5;


            int rainbowColor = Color.HSBtoRGB(hue, 1.0f, 1.0f);


            String dayText = "Day: " + dayCounter;
            int dayWidth = client.textRenderer.getWidth(dayText);
            drawContext.drawText(client.textRenderer, dayText, baseX - dayWidth, baseY, rainbowColor, false);
            baseY += 12;


            String serverType = getServerType(client);
            int typeWidth = client.textRenderer.getWidth(serverType);
            drawContext.drawText(client.textRenderer, serverType, baseX - typeWidth, baseY, rainbowColor, false);
            baseY += 12; // Move down for the next line


            ServerInfo serverInfo = client.getCurrentServerEntry();
            if (serverInfo != null) {
                String serverIpText = "Griefing: " + serverInfo.address;
                int ipWidth = client.textRenderer.getWidth(serverIpText);
                drawContext.drawText(client.textRenderer, serverIpText, baseX - ipWidth, baseY, rainbowColor, false);
            }
        }
    }

    private String getServerType(MinecraftClient client) {
        ServerInfo serverInfo = client.getCurrentServerEntry();
        if (serverInfo == null) {
            return "Type: Vanilla";
        }

        String serverType = String.valueOf(serverInfo.version);

        if (serverType.contains("Paper")) {
            return "Type: Paper";
        } else if (serverType.contains("Bukkit")) {
            return "Type: Bukkit";
        } else if (serverType.contains("Spigot")) {
            return "Type: Spigot";
        } else if (serverType.contains("Forge")) {
            return "Type: Forge";
        } else if (serverType.contains("Fabric")) {
            return "Type: Fabric";
        } else if (serverType.startsWith("1.20") || serverType.startsWith("1.19")) { // Adjust as needed for Vanilla versions
            return "Type: Vanilla";
        } else {
            return "Type: Vanilla"; // Default to Vanilla if unknown
        }
    }
}
