package computer.gingershaped.afkmarker.mixin;

import computer.gingershaped.afkmarker.client.AfkMarkerClient;
import computer.gingershaped.afkmarker.client.NameplateRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerTabOverlay.class)
public class TabListMixin {
    @Inject(method = "renderPingIcon", at = @At("TAIL"))
    private void renderPingIconInject(
            GuiGraphics guiGraphics,
            int width,
            int x,
            int y,
            PlayerInfo playerInfo,
            CallbackInfo ci
    ) {
        if (AfkMarkerClient.INSTANCE.getAfkPlayers().contains(playerInfo.getProfile())) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
            guiGraphics.blit(
                    RenderType::guiTextured,
                    AfkMarkerClient.getAFK_ICON(),
                    x + width + 2, y,
                    0F, 0F,
                    8, 8,
                    8, 8
            );
            guiGraphics.pose().popPose();
        }
    }
}
