package me.redcarlos.higtools.modules.main;

import me.redcarlos.higtools.HIGTools;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;

public class AxisViewer extends Module {
    private final SettingGroup sgOverworld = settings.createGroup("Overworld");
    private final SettingGroup sgNether = settings.createGroup("Nether");
    private final SettingGroup sgEnd = settings.createGroup("End");

    /**
     * Overworld
     */
    private final Setting<AxisType> overworldAxisTypes = sgOverworld.add(new EnumSetting.Builder<AxisType>()
        .name("type")
        .description("Which axis to display.")
        .defaultValue(AxisType.Both)
        .build()
    );

    private final Setting<Integer> overworldY = sgOverworld.add(new IntSetting.Builder()
        .name("height")
        .description("Y position of the line.")
        .defaultValue(64)
        .sliderMin(-64)
        .sliderMax(319)
        .visible(() -> overworldAxisTypes.get() != AxisType.None)
        .build()
    );

    private final Setting<SettingColor> overworldColor = sgOverworld.add(new ColorSetting.Builder()
        .name("color")
        .description("The line color.")
        .defaultValue(new SettingColor(25, 25, 225, 255))
        .visible(() -> overworldAxisTypes.get() != AxisType.None)
        .build()
    );

    /**
     * Nether
     */
    private final Setting<AxisType> netherAxisTypes = sgNether.add(new EnumSetting.Builder<AxisType>()
        .name("type")
        .description("Which axis to display.")
        .defaultValue(AxisType.Both)
        .build()
    );

    private final Setting<Integer> netherY = sgNether.add(new IntSetting.Builder()
        .name("height")
        .description("Y position of the line.")
        .defaultValue(120)
        .sliderMin(0)
        .sliderMax(255)
        .visible(() -> netherAxisTypes.get() != AxisType.None)
        .build()
    );

    private final Setting<SettingColor> netherColor = sgNether.add(new ColorSetting.Builder()
        .name("color")
        .description("The line color.")
        .defaultValue(new SettingColor(225, 25, 25, 255))
        .visible(() -> netherAxisTypes.get() != AxisType.None)
        .build()
    );

    /**
     * End
     */
    private final Setting<AxisType> endAxisTypes = sgEnd.add(new EnumSetting.Builder<AxisType>()
        .name("type")
        .description("Which axis to display.")
        .defaultValue(AxisType.Both)
        .build()
    );

    private final Setting<Integer> endY = sgEnd.add(new IntSetting.Builder()
        .name("height")
        .description("Y position of the line.")
        .defaultValue(64)
        .sliderMin(0)
        .sliderMax(255)
        .visible(() -> endAxisTypes.get() != AxisType.None)
        .build()
    );

    private final Setting<SettingColor> endColor = sgEnd.add(new ColorSetting.Builder()
        .name("color")
        .description("The line color.")
        .defaultValue(new SettingColor(225, 25, 25, 255))
        .visible(() -> endAxisTypes.get() != AxisType.None)
        .build()
    );

    public AxisViewer() {
        super(HIGTools.MAIN, "axis-viewer", "Render culling fucks it up :skull:");
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (mc.options.hudHidden) return;
        switch (PlayerUtils.getDimension()) {
            case Overworld -> {
                if (overworldAxisTypes.get() == AxisType.Both) {
                    drawLines(event, true, true, overworldY.get(), overworldColor.get());
                } else if (overworldAxisTypes.get() == AxisType.Axis) {
                    drawLines(event, true, false, overworldY.get(), overworldColor.get());
                } else if (overworldAxisTypes.get() == AxisType.Diagonals) {
                    drawLines(event, false, true, overworldY.get(), overworldColor.get());
                }
            }
            case Nether -> {
                if (netherAxisTypes.get() == AxisType.Both) {
                    drawLines(event, true, true, netherY.get(), netherColor.get());
                } else if (netherAxisTypes.get() == AxisType.Axis) {
                    drawLines(event, true, false, netherY.get(), netherColor.get());
                } else if (netherAxisTypes.get() == AxisType.Diagonals) {
                    drawLines(event, false, true, netherY.get(), netherColor.get());
                }
            }
            case End -> {
                if (endAxisTypes.get() == AxisType.Both) {
                    drawLines(event, true, true, endY.get(), endColor.get());
                } else if (endAxisTypes.get() == AxisType.Axis) {
                    drawLines(event, true, false, endY.get(), endColor.get());
                } else if (endAxisTypes.get() == AxisType.Diagonals) {
                    drawLines(event, false, true, endY.get(), endColor.get());
                }
            }
        }
    }

    // Todo : Render lines block per block to prevent render culling breaking rendering
    private void drawLines(Render3DEvent event, boolean axis, boolean diags, int y, SettingColor color) {
        if (axis) {
            event.renderer.line(0, y, 0, 0, y, 30_000_000, color); // Z+
            event.renderer.line(0, y, 0, 30_000_000, y, 0, color); // X+
            event.renderer.line(0, y, 0, 0, y, -30_000_000, color); // -Z
            event.renderer.line(0, y, 0, -30_000_000, y, 0, color); // -X
        }

        if (diags) {
            event.renderer.line(0, y, 0, 30_000_000, y, 30_000_000, color); // ++
            event.renderer.line(0, y, 0, 30_000_000, y, -30_000_000, color); // +-
            event.renderer.line(0, y, 0, -30_000_000, y, 30_000_000, color); // -+
            event.renderer.line(0, y, 0, -30_000_000, y, -30_000_000, color); // --
        }
    }

    public enum AxisType {
        Both,
        Axis,
        Diagonals,
        None
    }
}
