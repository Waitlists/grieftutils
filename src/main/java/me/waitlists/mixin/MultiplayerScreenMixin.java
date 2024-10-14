package me.waitlists.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen {
    @Shadow
    protected MultiplayerServerListWidget serverListWidget;

    @Unique
    private ButtonWidget clearServersButton;

    protected MultiplayerScreenMixin() {
        super(null);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;updateButtonActivationStates()V"))
    private void onInit(CallbackInfo info) {
        int buttonWidth = 80;
        int buttonHeight = 20;


        this.clearServersButton = this.addDrawableChild(
                new ButtonWidget.Builder(
                        Text.literal("Clear Servers"),
                        onPress -> {
                            if (this.client == null) return;


                            MinecraftClient client = this.client;
                            ServerList serverList = new ServerList(client);
                            serverList.loadFile();


                            for (int i = serverList.size() - 1; i >= 0; i--) {
                                ServerInfo serverInfo = serverList.get(i);
                                serverList.remove(serverInfo);
                            }


                            serverList.saveFile();


                            this.serverListWidget.children().clear();
                            this.serverListWidget.setSelected(null);
                        }
                )
                        .position(1, this.height - buttonHeight - 1)
                        .width(buttonWidth)
                        .build()
        );
    }
}
