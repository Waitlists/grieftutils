package me.waitlists.mods;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class CarpetBotBombing {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static KeyBinding spawnCommandKeyBinding;
    private static int tickCounter = 0;
    private static int spawnCounter = 0;
    private static boolean running = false;

    public static void register() {
        spawnCommandKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Carpet Bot Bomb",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "Grieft Utils"
        ));


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if (spawnCommandKeyBinding.wasPressed()) {

                running = !running;

                if (running) {
                    spawnCounter = 1;
                    tickCounter = 0;
                    displayCarpetBotBombingStatus(true); // Show ON status
                } else {
                    displayCarpetBotBombingStatus(false); // Show OFF status
                }
            }


            if (running) {
                tickCounter++;

                if (tickCounter >= 10 && spawnCounter <= 30) {
                    runSpawnCommand(spawnCounter);
                    spawnCounter++;
                    tickCounter = 0;
                }


                if (spawnCounter > 100) {
                    running = false;
                    displayCarpetBotBombingStatus(false); // Show OFF status
                }
            }
        });
    }

    private static void runSpawnCommand(int count) {
        String playerName = "Grieft" + count;
        String command = "/player " + playerName + " spawn";


        client.player.networkHandler.sendCommand(command.substring(1));
    }

    private static void displayCarpetBotBombingStatus(boolean isRunning) {
        String statusMessage = isRunning ? "Carpet Bot Bombing ON" : "Carpet Bot Bombing OFF";
        int color = isRunning ? 0x00FF00 : 0xFF0000; // Green for ON, Red for OFF
        client.player.sendMessage(Text.literal(statusMessage).styled(style -> style.withColor(color)), true); // Display in actionbar
    }
}
