package me.redcarlos.higtools.modules.itemrefill;

import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.player.ChatUtils.info;

public class Utils {

    // Shulker Refilling

    static int timer = 0;

    public static void placeShulkerBox() {
        if (mc.player == null) return;
        BlockUtils.place((mc.player.getBlockPos().up(2)), findShulkerBox(), true, 0, true);
        BlockPos shulkerPos = mc.player.getBlockPos().up(2);
        info("placed shulker");
        if (timer < 5) {
            timer++;
        } else {
            Vec3d shulkerVec = new Vec3d(shulkerPos.getX(), shulkerPos.getY(), shulkerPos.getZ());
            BlockHitResult table = new BlockHitResult(shulkerVec, Direction.DOWN, shulkerPos, false);
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, table);
            grabAllItems();

            info("opened shulker");
            timer = 0;
        }
    }

    public static FindItemResult findShulkerBox() {
        return InvUtils.find(itemStack -> shulkers.contains(itemStack.getItem()));
    }

    public static void grabAllItems() {
        info("grabbing items");
        int picksMoved = 0;
        int availableSlots = 0;
        for (int i = 27; i < mc.player.currentScreenHandler.slots.size(); i++) {
            Item item = mc.player.currentScreenHandler.getSlot(i).getStack().getItem();
            if (item.equals(Items.AIR)) {
                availableSlots++;
            }

            for (int z = 0; z < mc.player.currentScreenHandler.slots.size() - 36; z++) {
                Item item2 = mc.player.currentScreenHandler.getSlot(z).getStack().getItem();
                if (item2.equals(Items.ENDER_CHEST)) {
                    if (availableSlots - 2 > picksMoved) {
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, z, 1, SlotActionType.QUICK_MOVE, mc.player);
                        picksMoved++;
                    }
                }
            }

            if (mc.currentScreen instanceof ShulkerBoxScreen) {
                if (picksMoved == 0) {
                    mc.currentScreen.close();
                    return;
                }
            }
        }
    }

    private void openShulker(BlockPos shulkerPos) {
        if (mc.interactionManager == null) return;

        Vec3d shulkerVec = new Vec3d(shulkerPos.getX(), shulkerPos.getY(), shulkerPos.getZ());
        BlockHitResult table = new BlockHitResult(shulkerVec, Direction.DOWN, shulkerPos, false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, table);
    }

    /*
    private int getPickAmount() {
        return HIGUtils.haveItem(Items.NETHERITE_PICKAXE);
    }

    private int grabAllPickaxes() {
        int picksMoved = 0;
        int availableSlots = 0;
        // Checks player's inventory for available slots
        for (int i = 27; i < mc.player.currentScreenHandler.slots.size(); i++) {
            Item item = mc.player.currentScreenHandler.getSlot(i).getStack().getItem();
            if (item.equals(Items.AIR)) {
                availableSlots++;
            }
        }
        info("availableSlots: " + availableSlots);

        for (int i = 0; i < mc.player.currentScreenHandler.slots.size() - 36; i++) {
            Item item = mc.player.currentScreenHandler.getSlot(i).getStack().getItem();
            if (item.equals(Items.NETHERITE_PICKAXE)) {
                if (availableSlots - 2 > picksMoved) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 1, SlotActionType.QUICK_MOVE, mc.player);
                    picksMoved++;
                }
            }
        }
        info("picksMoved: " + picksMoved);
        return picksMoved;
    }
     */


    public static ArrayList<Item> shulkers = new ArrayList<>(){{
        add(Items.SHULKER_BOX);
        add(Items.BLACK_SHULKER_BOX);
        add(Items.BLUE_SHULKER_BOX);
        add(Items.BROWN_SHULKER_BOX);
        add(Items.CYAN_SHULKER_BOX);
        add(Items.GRAY_SHULKER_BOX);
        add(Items.GREEN_SHULKER_BOX);
        add(Items.LIGHT_BLUE_SHULKER_BOX);
        add(Items.LIGHT_GRAY_SHULKER_BOX);
        add(Items.LIME_SHULKER_BOX);
        add(Items.MAGENTA_SHULKER_BOX);
        add(Items.ORANGE_SHULKER_BOX);
        add(Items.PINK_SHULKER_BOX);
        add(Items.PURPLE_SHULKER_BOX);
        add(Items.RED_SHULKER_BOX);
        add(Items.WHITE_SHULKER_BOX);
        add(Items.YELLOW_SHULKER_BOX);
    }};
}
