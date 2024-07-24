package me.redcarlos.higtools.modules.main;

import me.redcarlos.higtools.HIGTools;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;

public class AxisViewer extends Module {
    private final SettingGroup sgOverworld = settings.createGroup("Overworld");
    private final SettingGroup sgNether = settings.createGroup("Nether");
    private final SettingGroup sgEnd = settings.createGroup("End");

    // Overworld

    private final Setting<AxisType> overworldAxisTypes = sgOverworld.add(new EnumSetting.Builder<AxisType>()
        .name("render")
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

    // Nether

    private final Setting<AxisType> netherAxisTypes = sgNether.add(new EnumSetting.Builder<AxisType>()
        .name("render")
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

    // End

    private final Setting<AxisType> endAxisTypes = sgEnd.add(new EnumSetting.Builder<AxisType>()
        .name("render")
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
        super(HIGTools.MAIN, "axis-viewer-(WIP)", "Render culling fucks it up :skull:");
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (mc.options.hudHidden) return;

        AxisType axisType;
        int y;
        Color lineColor;

        switch (PlayerUtils.getDimension()) {
            case Overworld -> {
                axisType = overworldAxisTypes.get();
                y = overworldY.get();
                lineColor = overworldColor.get();
            }
            case Nether -> {
                axisType = netherAxisTypes.get();
                y = netherY.get();
                lineColor = netherColor.get();
            }
            case End -> {
                axisType = endAxisTypes.get();
                y = endY.get();
                lineColor = endColor.get();
            }
            default -> throw new IllegalStateException("Unexpected value: " + PlayerUtils.getDimension());
        }

        // TODO: Fix to be unaffected by render culling

        if (axisType.cardinals()) {
            event.renderer.line(0, y, 0, 0, y, 30_000_000, lineColor); // Z+
            event.renderer.line(0, y, 0, 30_000_000, y, 0, lineColor); // X+
            event.renderer.line(0, y, 0, 0, y, -30_000_000, lineColor); // -Z
            event.renderer.line(0, y, 0, -30_000_000, y, 0, lineColor); // -X
        }

        if (axisType.diagonals()) {
            event.renderer.line(0, y, 0, 30_000_000, y, 30_000_000, lineColor); // ++
            event.renderer.line(0, y, 0, 30_000_000, y, -30_000_000, lineColor); // +-
            event.renderer.line(0, y, 0, -30_000_000, y, 30_000_000, lineColor); // -+
            event.renderer.line(0, y, 0, -30_000_000, y, -30_000_000, lineColor); // --
        }

    }

    public enum AxisType {
        Both,
        Cardinals,
        Diagonals,
        None;

        boolean cardinals() {
            return this == Both || this == Cardinals;
        }

        boolean diagonals() {
            return this == Both || this == Diagonals;
        }
    }
}
