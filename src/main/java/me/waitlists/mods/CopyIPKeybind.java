package me.waitlists.mods;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class CopyIPKeybind {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static KeyBinding copyIPKeyBinding;

    public static void register() {
        copyIPKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Copy Server IP",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "Grieft Utils"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (copyIPKeyBinding.wasPressed() && client.player != null) {
                copyServerIPToClipboard();
            }
        });
    }

    private static void copyServerIPToClipboard() {
        if (client.getCurrentServerEntry() != null) {
            // Get the server IP
            String serverIP = client.getCurrentServerEntry().address;

            try {
                client.keyboard.setClipboard(serverIP);
                // Show the message in the action bar
                client.player.sendMessage(Text.literal("Server IP copied to clipboard: " + serverIP).formatted(Formatting.GREEN), true);
            } catch (Exception e) {
                e.printStackTrace();
                // Show the error message in the action bar
                client.player.sendMessage(Text.literal("Failed to copy IP to clipboard. Please check your system settings.").formatted(Formatting.RED), true);
            }
        } else {
            // Show the not connected message in the action bar
            client.player.sendMessage(Text.literal("Not connected to any server.").formatted(Formatting.RED), true);
        }
    }
}
