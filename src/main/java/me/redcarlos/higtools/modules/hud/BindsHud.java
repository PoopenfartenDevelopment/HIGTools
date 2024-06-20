package me.redcarlos.higtools.modules.hud;

import me.redcarlos.higtools.HIGTools;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.Alignment;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.hud.elements.TextHud;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

import java.util.ArrayList;
import java.util.List;

public class BindsHud extends HudElement {
    public static final HudElementInfo<BindsHud> INFO = new HudElementInfo<>(HIGTools.HUD, "binds", "Displays modules you've binded keys to.", BindsHud::new);

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Sort> sortMode = sgGeneral.add(new EnumSetting.Builder<Sort>()
        .name("sort-mode")
        .description("How to sort active modules.")
        .defaultValue(Sort.Biggest)
        .build()
    );

    private final Setting<ColorMode> colorMode = sgGeneral.add(new EnumSetting.Builder<ColorMode>()
        .name("color-mode")
        .description("What color to use for active modules.")
        .defaultValue(ColorMode.Rainbow)
        .build()
    );

    private final Setting<Double> rainbowSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("rainbow-speed")
        .description("Rainbow speed of rainbow color mode.")
        .defaultValue(0.05)
        .sliderMin(0.01)
        .sliderMax(0.2)
        .decimalPlaces(4)
        .visible(() -> colorMode.get() == ColorMode.Rainbow)
        .build()
    );

    private final Setting<Double> rainbowSpread = sgGeneral.add(new DoubleSetting.Builder()
        .name("rainbow-spread")
        .description("Rainbow spread of rainbow color mode.")
        .defaultValue(0.01)
        .sliderMin(0.001)
        .sliderMax(0.05)
        .decimalPlaces(4)
        .visible(() -> colorMode.get() == ColorMode.Rainbow)
        .build()
    );

    private final Setting<SettingColor> flatColor = sgGeneral.add(new ColorSetting.Builder()
        .name("flat-color")
        .description("Color for flat color mode.")
        .defaultValue(new SettingColor(225, 25, 25, 255))
        .visible(() -> colorMode.get() == ColorMode.Flat)
        .build()
    );

    private final Setting<Boolean> outlines = sgGeneral.add(new BoolSetting.Builder()
        .name("outlines")
        .description("Whether or not to render outlines")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> outlineWidth = sgGeneral.add(new IntSetting.Builder()
        .name("outline-width")
        .description("Outline width")
        .defaultValue(2)
        .min(1)
        .sliderMin(1)
        .visible(outlines::get)
        .build()
    );

    private final Setting<Boolean> shadow = sgGeneral.add(new BoolSetting.Builder()
        .name("shadow")
        .description("Renders shadow behind text.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Alignment> alignment = sgGeneral.add(new EnumSetting.Builder<Alignment>()
        .name("alignment")
        .description("Horizontal alignment.")
        .defaultValue(Alignment.Auto)
        .build()
    );

    private final List<Module> modules = new ArrayList<>();

    private final Color rainbow = new Color(255, 255, 255, 255);
    private double rainbowHue1, rainbowHue2;

    private double prevX;
    private double prevTextLength;
    private Color prevColor = new Color();

    public BindsHud() {
        super(INFO);
    }

    @Override
    public void tick(HudRenderer renderer) {
        if (Modules.get() == null) {
            box.setSize(renderer.textWidth("Keybind List"), renderer.textHeight());
            return;
        }

        modules.clear();

        modules.addAll(Modules.get().getAll().stream().filter(module -> module.keybind.isSet()).toList());

        modules.sort((o1, o2) -> switch (sortMode.get()) {
            case Biggest -> Double.compare(getModuleWidth(renderer, o2), getModuleWidth(renderer, o1));
            case Smallest -> Double.compare(getModuleWidth(renderer, o1), getModuleWidth(renderer, o2));
        });

        double width = 0;
        double height = 0;

        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);

            width = Math.max(width, getModuleWidth(renderer, module));
            height += renderer.textHeight(shadow.get());
            if (i > 0) height += 2;
        }

        box.setSize(width, height);
    }

    @Override
    public void render(HudRenderer renderer) {
        double x = this.x;
        double y = this.y;

        if (Modules.get() == null) {
            renderer.text("Keybind List", x, y, TextHud.getSectionColor(0), shadow.get());
            return;
        }

        rainbowHue1 += rainbowSpeed.get() * renderer.delta;
        if (rainbowHue1 > 1) rainbowHue1 -= 1;
        else if (rainbowHue1 < -1) rainbowHue1 += 1;

        rainbowHue2 = rainbowHue1;

        prevX = x;

        for (int i = 0; i < modules.size(); i++) {
            double offset = alignX(getModuleWidth(renderer, modules.get(i)), alignment.get());
            renderModule(renderer, modules, i, x + offset, y);

            prevX = x + offset;
            y += 2 + renderer.textHeight(shadow.get());
        }
    }

    private void renderModule(HudRenderer renderer, List<Module> modules, int index, double x, double y) {
        Module module = modules.get(index);
        Color color = flatColor.get();

        switch (colorMode.get()) {
            case Random -> color = module.color;
            case Rainbow -> {
                rainbowHue2 += rainbowSpread.get();
                int c = java.awt.Color.HSBtoRGB((float) rainbowHue2, 1, 1);
                rainbow.r = Color.toRGBAR(c);
                rainbow.g = Color.toRGBAG(c);
                rainbow.b = Color.toRGBAB(c);
                color = rainbow;
            }
            case Flat -> {
                // No effects must be applied in flat mode
            }
        }

        renderer.text(module.title, x, y, color, shadow.get());

        double textHeight = renderer.textHeight(shadow.get());
        double textLength = renderer.textWidth(module.title, shadow.get());

        String info = module.keybind.toString();
        renderer.text(info, x + renderer.textWidth(module.title) + renderer.textWidth(" "), y, TextHud.getSectionColor(1), shadow.get());
        textLength += renderer.textWidth(" ") + renderer.textWidth(info);

        if (outlines.get()) {
            if (index == 0) {
                renderer.quad(x - 2 - outlineWidth.get(), y - 2, outlineWidth.get(), textHeight + 4, prevColor, prevColor, color, color); // Left quad
                renderer.quad(x + textLength + 2, y - 2, outlineWidth.get(), textHeight + 4, prevColor, prevColor, color, color); // Right quad

                renderer.quad(x - 2 - outlineWidth.get(), y - 2 - outlineWidth.get(), textLength + 4 + (outlineWidth.get() * 2), outlineWidth.get(), prevColor, prevColor, color, color); // Top quad

            } else if (index == modules.size() - 1) {
                renderer.quad(x - 2 - outlineWidth.get(), y, outlineWidth.get(), textHeight + 2 + outlineWidth.get(), prevColor, prevColor, color, color); // Left quad
                renderer.quad(x + textLength + 2, y, outlineWidth.get(), textHeight + 2 + outlineWidth.get(), prevColor, prevColor, color, color); // Right quad

                renderer.quad(x - 2 - outlineWidth.get(), y + textHeight + 2, textLength + 4 + (outlineWidth.get() * 2), outlineWidth.get(), prevColor, prevColor, color, color); // Bottom quad
            }

            if (index > 0) {
                if (index < modules.size() - 1) {
                    renderer.quad(x - 2 - outlineWidth.get(), y, outlineWidth.get(), textHeight + 2, prevColor, prevColor, color, color); // Left quad
                    renderer.quad(x + textLength + 2, y, outlineWidth.get(), textHeight + 2, prevColor, prevColor, color, color); // Right quad
                }

                renderer.quad(Math.min(prevX, x) - 2 - outlineWidth.get(), Math.max(prevX, x) == x ? y : y - outlineWidth.get(),
                    (Math.max(prevX, x) - 2) - (Math.min(prevX, x) - 2 - outlineWidth.get()), outlineWidth.get(),
                    prevColor, prevColor, color, color); // Left in-between quad

                renderer.quad(Math.min(prevX + prevTextLength, x + textLength) + 2, Math.min(prevX + prevTextLength, x + textLength) == x + textLength ? y : y - outlineWidth.get(),
                    (Math.max(prevX + prevTextLength, x + textLength) + 2 + outlineWidth.get()) - (Math.min(prevX + prevTextLength, x + textLength) + 2), outlineWidth.get(),
                    prevColor, prevColor, color, color); // Right in-between quad
            }
        }

        prevTextLength = textLength;
        prevColor = color;
    }

    private double getModuleWidth(HudRenderer renderer, Module module) {
        double width = renderer.textWidth(module.title);

        String info = module.keybind.toString();
        width += renderer.textWidth(" ") + renderer.textWidth(info);

        return width;
    }

    public enum Sort {
        Biggest,
        Smallest
    }

    public enum ColorMode {
        Flat,
        Random,
        Rainbow
    }
}
