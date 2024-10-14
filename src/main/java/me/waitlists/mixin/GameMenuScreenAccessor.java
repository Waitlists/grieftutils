package me.waitlists.mixin;

import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.gui.screen.GameMenuScreen;

@Mixin(GameMenuScreen.class)
public interface GameMenuScreenAccessor {
    @Accessor("exitButton")
    ButtonWidget getExitButton();
}
