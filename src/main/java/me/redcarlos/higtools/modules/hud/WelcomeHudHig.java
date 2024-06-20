package me.redcarlos.higtools.modules.hud;

import me.redcarlos.higtools.HIGTools;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.hud.elements.TextHud;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.NameProtect;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class WelcomeHudHig extends HudElement {
    public static final HudElementInfo<WelcomeHudHig> INFO = new HudElementInfo<>(HIGTools.HUD, "HIG-welcome", "Displays a welcome message.", WelcomeHudHig::new);

    private String leftText;
    private String rightText;
    private double leftWidth;

    public WelcomeHudHig() {
        super(INFO);
    }

    @Override
    public void tick(HudRenderer renderer) {
        leftText = "Welcome to HIG Tools, ";
        rightText = Modules.get().get(NameProtect.class).getName(mc.getSession().getUsername());

        leftWidth = renderer.textWidth(leftText);
        double rightWidth = renderer.textWidth(rightText);

        box.setSize((leftWidth + rightWidth), renderer.textHeight());
    }

    @Override
    public void render(HudRenderer renderer) {
        double x = this.x;
        double y = this.y;

        if (isInEditor()) {
            renderer.text("Welcome Hud", x, y, TextHud.getSectionColor(0), true);
            return;
        }

        renderer.text(leftText, x, y, TextHud.getSectionColor(0), true);
        renderer.text(rightText, x + leftWidth, y, TextHud.getSectionColor(1), true);
    }
}
