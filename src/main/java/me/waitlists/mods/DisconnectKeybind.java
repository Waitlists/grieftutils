package me.waitlists.mods;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class DisconnectKeybind {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static KeyBinding disconnectKeyBinding;

    public static void register() {
        disconnectKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Disconnect from Server",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "Grieft Utils"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (disconnectKeyBinding.wasPressed() && client.player != null) {
                client.player.networkHandler.sendChatMessage(".disconnect");
            }
        });
    }
}
