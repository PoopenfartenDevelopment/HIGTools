package me.redcarlos.higtools.modules.main;

import me.redcarlos.higtools.HIGTools;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.TickRate;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;

public class HotbarManager extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("move-delay")
        .description("Delay in ticks between moving items.")
        .defaultValue(1)
        .range(1, 20)
        .sliderRange(1, 20)
        .build()
    );

    private final Setting<Boolean> replace = sgGeneral.add(new BoolSetting.Builder()
        .name("replace")
        .description("Replace items already in your hotbar.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> toggle = sgGeneral.add(new BoolSetting.Builder()
        .name("toggle")
        .description("Toggle off automatically after one pass through the hotbar.")
        .defaultValue(true)
        .build()
    );

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WButton reset = theme.button("Reset");
        reset.action = () -> itemSettings.forEach(Setting::reset);

        return reset;
    }

    private final List<Setting<Item>> itemSettings = new ArrayList<>();
    private double ticksLeft;

    public HotbarManager() {
        super(HIGTools.MAIN, "hotbar-manager", "Automatically move items to your hotbar.");

        final SettingGroup sgHotbar = settings.createGroup("Hotbar");

        for (int i = 0; i < 9; i++) {
            ItemSetting setting = new ItemSetting.Builder()
                .name("slot-" + (i + 1))
                .description("The item to store in slot " + (i + 1) + ".")
                .defaultValue(Items.AIR)
                .build();

            itemSettings.add(sgHotbar.add(setting));
        }
    }

    @Override
    public void onActivate() {
        ticksLeft = 0.0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if ((ticksLeft -= TickRate.INSTANCE.getTickRate() / 20.0) > 0.0) return;
        int highestSlot = -1;

        for (int i = 0; i < 9; i++) {
            if (ticksLeft > 0.0) return;
            highestSlot = Math.max(highestSlot, i);

            ItemSetting setting = (ItemSetting) itemSettings.get(i);
            if (setting.get() == Items.AIR) continue;

            Item slotItem = mc.player.getInventory().getStack(i).getItem();
            if (slotItem != Items.AIR && !replace.get()) continue;
            if (mc.player.getInventory().getStack(i).getItem() == setting.get()) continue;

            FindItemResult result = InvUtils.find(stack -> stack.getItem() == setting.get(), i, 35);
            if (!result.found()) continue;

            InvUtils.move().from(result.slot()).to(i);
            ticksLeft = delay.get();
        }

        if (highestSlot == 8 && toggle.get()) toggle();
    }
}
