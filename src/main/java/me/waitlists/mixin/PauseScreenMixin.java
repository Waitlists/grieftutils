package me.waitlists.mixin;

import me.waitlists.mixin.GameMenuScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Mixin(GameMenuScreen.class)
public class PauseScreenMixin extends Screen {

    private static final Identifier TNT_ICON = Identifier.of("minecraft", "textures/block/tnt_side.png");
    private ButtonWidget logButton;

    protected PauseScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "initWidgets", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        // Use the accessor mixin to get the exitButton
        ButtonWidget exitButton = ((GameMenuScreenAccessor) this).getExitButton();
        if (exitButton != null) {
            addGriefLogButton(exitButton);
        }
    }

    private void addGriefLogButton(ButtonWidget exitButton) {

        int x = exitButton.getX() + exitButton.getWidth() + 2;
        int y = exitButton.getY();

        logButton = ButtonWidget.builder(Text.empty(), (button) -> {
            logGriefInfo();
        }).dimensions(x, y, 20, 20).build(); // Keeping the button size the same

        this.addDrawableChild(logButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);


        if (this.client != null) {
            context.getMatrices().push();

            int iconSize = 16; // The original size of the icon
            int scaledSize = (int) (iconSize * 0.75);
            int offsetX = 2;
            int offsetY = 2;


            context.drawTexture(TNT_ICON, logButton.getX() + offsetX, logButton.getY() + offsetY, 0.0F, 0.0F, iconSize, iconSize, iconSize, iconSize);

            context.getMatrices().pop();
        }
    }

    private void logGriefInfo() {
        MinecraftClient client = MinecraftClient.getInstance();
        ServerInfo serverInfo = client.getCurrentServerEntry();

        if (serverInfo != null && client.world != null) {
            String serverIP = serverInfo.address;
            int serverDay = (int) (client.world.getTimeOfDay() / 24000L) + 1;

            String logMessage = "Griefed: " + serverIP + ", Day: " + serverDay;

            // Write to 'grief_logs.txt' in the .minecraft directory
            File griefLogFile = new File(client.runDirectory, "grief_logs.txt");


            try {
                if (griefLogFile.createNewFile()) {
                    System.out.println("Created new log file: " + griefLogFile.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            try (FileWriter writer = new FileWriter(griefLogFile, true)) {
                writer.write(logMessage + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
