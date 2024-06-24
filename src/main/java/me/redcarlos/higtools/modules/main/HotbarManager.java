package me.redcarlos.higtools.modules.main;

import me.redcarlos.higtools.HIGTools;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.MeteorToast;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

import java.util.stream.IntStream;

import static me.redcarlos.higtools.utils.HIGUtils.csToPs;

public class HotbarManager extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup slotsGroup = settings.createGroup("Slots", false);

    private final Identifier[] itemIds = IntStream.range(0, 9).mapToObj(i -> new Identifier("")).toArray(Identifier[]::new);

    private final Setting<String> slot0 = slotsGroup.add(new StringSetting.Builder()
        .name("slot0")
        .defaultValue("")
        .visible(() -> false)
        .onChanged(cur -> itemIds[0] = new Identifier(cur))
        .build()
    );
    private final Setting<String> slot1 = slotsGroup.add(new StringSetting.Builder()
        .name("slot1")
        .defaultValue("")
        .visible(() -> false)
        .onChanged(cur -> itemIds[1] = new Identifier(cur))
        .build()
    );
    private final Setting<String> slot2 = slotsGroup.add(new StringSetting.Builder()
        .name("slot2")
        .defaultValue("")
        .visible(() -> false)
        .onChanged(cur -> itemIds[2] = new Identifier(cur))
        .build()
    );
    private final Setting<String> slot3 = slotsGroup.add(new StringSetting.Builder()
        .name("slot3")
        .defaultValue("")
        .visible(() -> false)
        .onChanged(cur -> itemIds[3] = new Identifier(cur))
        .build()
    );
    private final Setting<String> slot4 = slotsGroup.add(new StringSetting.Builder()
        .name("slot4")
        .defaultValue("")
        .visible(() -> false)
        .onChanged(cur -> itemIds[4] = new Identifier(cur))
        .build()
    );
    private final Setting<String> slot5 = slotsGroup.add(new StringSetting.Builder()
        .name("slot5")
        .defaultValue("")
        .visible(() -> false)
        .onChanged(cur -> itemIds[5] = new Identifier(cur))
        .build()
    );
    private final Setting<String> slot6 = slotsGroup.add(new StringSetting.Builder()
        .name("slot6")
        .defaultValue("")
        .visible(() -> false)
        .onChanged(cur -> itemIds[6] = new Identifier(cur))
        .build()
    );
    private final Setting<String> slot7 = slotsGroup.add(new StringSetting.Builder()
        .name("slot7")
        .defaultValue("")
        .visible(() -> false)
        .onChanged(cur -> itemIds[7] = new Identifier(cur))
        .build()
    );
    private final Setting<String> slot8 = slotsGroup.add(new StringSetting.Builder()
        .name("slot8")
        .defaultValue("")
        .visible(() -> false)
        .onChanged(cur -> itemIds[8] = new Identifier(cur))
        .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("Delay between moving actions.")
        .defaultValue(1)
        .range(1, 35)
        .sliderRange(1, 35)
        .build()
    );

    public HotbarManager() {
        super(HIGTools.MAIN, "hotbar-manager", "Automatically sort and replenish your hotbar.");
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WTable table = theme.table();
        fillTable(theme, table);

        return table;
    }

    private void fillTable(GuiTheme theme, WTable table) {
        WButton save = table.add(theme.button("Save")).expandCellX().right().widget();
        save.action = this::save;

        WButton reset = table.add(theme.button("Reset")).right().widget();
        reset.action = this::reset;
    }

    @EventHandler
    public void onRender(Render3DEvent event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (mc.player.age % delay.get() != 0) return;

        for (int i = 0; i <= 8; i++) {
            if (itemIds[i].toString().replace("minecraft:", "").isEmpty()) continue;
            if (!Registries.ITEM.getId(mc.player.getInventory().getStack(i).getItem()).equals(itemIds[i])) {
                for (int j = 35; j >= 9; j--) {
                    if (Registries.ITEM.getId(mc.player.getInventory().getStack(j).getItem()).equals(itemIds[i])) {
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, csToPs(j), 0, SlotActionType.PICKUP, mc.player);
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, csToPs(i), 0, SlotActionType.PICKUP, mc.player);
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, csToPs(j), 0, SlotActionType.PICKUP, mc.player);
                        mc.interactionManager.tick();
                        break;
                    }
                }
            }
        }
    }

    private void reset() {
        slot0.set("");
        slot1.set("");
        slot2.set("");
        slot3.set("");
        slot4.set("");
        slot5.set("");
        slot6.set("");
        slot7.set("");
        slot8.set("");

        mc.getToastManager().add(new MeteorToast(Items.ENDER_CHEST, "Hotbar Manager", "Cleared Saved Hotbar", 3000));
    }

    private void save() {
        if (mc.player == null) return;

        slot0.set(Registries.ITEM.getId(mc.player.getInventory().getStack(0).getItem()).toString());
        slot1.set(Registries.ITEM.getId(mc.player.getInventory().getStack(1).getItem()).toString());
        slot2.set(Registries.ITEM.getId(mc.player.getInventory().getStack(2).getItem()).toString());
        slot3.set(Registries.ITEM.getId(mc.player.getInventory().getStack(3).getItem()).toString());
        slot4.set(Registries.ITEM.getId(mc.player.getInventory().getStack(4).getItem()).toString());
        slot5.set(Registries.ITEM.getId(mc.player.getInventory().getStack(5).getItem()).toString());
        slot6.set(Registries.ITEM.getId(mc.player.getInventory().getStack(6).getItem()).toString());
        slot7.set(Registries.ITEM.getId(mc.player.getInventory().getStack(7).getItem()).toString());
        slot8.set(Registries.ITEM.getId(mc.player.getInventory().getStack(8).getItem()).toString());

        mc.getToastManager().add(new MeteorToast(Items.ENDER_CHEST, "Hotbar Manager", "Saved Hotbar", 3000));
    }
}
