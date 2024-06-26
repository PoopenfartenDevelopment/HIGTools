package me.redcarlos.higtools;

import com.mojang.logging.LogUtils;
import me.redcarlos.higtools.commands.Center;
import me.redcarlos.higtools.commands.Coordinates;
import me.redcarlos.higtools.modules.highwayborers.*;
import me.redcarlos.higtools.modules.hud.WelcomeHudHig;
import me.redcarlos.higtools.modules.main.*;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.BetterChat;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class HIGTools extends MeteorAddon {
    public static final String MOD_ID = "higtools";
    public static final ModMetadata METADATA;
    public static final String VERSION;
    public static final Category MAIN;
    public static final Category BORERS;
    public static final HudGroup HUD;

    static {
        METADATA = FabricLoader.getInstance().getModContainer("higtools").orElseThrow(() -> new RuntimeException("HIGTools mod container not found!")).getMetadata();
        VERSION = METADATA.getVersion().toString();

        MAIN = new Category("HIG Tools", Items.NETHERITE_PICKAXE.getDefaultStack());
        BORERS = new Category(" Borers ", Items.NETHERITE_PICKAXE.getDefaultStack());
        HUD = new HudGroup("HIG Tools");
    }

    @Override
    public void onInitialize() {
        LogUtils.getLogger().info("Initializing HIGTools {}", HIGTools.VERSION);

        BetterChat.registerCustomHead("[HIGTools]", HIGTools.identifier("chat/icon.png"));

        // Commands
        Commands.add(new Center());
        Commands.add(new Coordinates());

        // Hud
        Hud hud = Systems.get(Hud.class);
        hud.register(WelcomeHudHig.INFO);

        // Modules
        Modules modules = Modules.get();
        modules.add(new AfkLogout());
        modules.add(new AutoCenter());
        modules.add(new AutoWalkHig());
        modules.add(new AxisViewer());
        modules.add(new HIGPrefix());
        modules.add(new DiscordRPC());
        modules.add(new OffhandManager());
        modules.add(new HighwayBuilderPlus());
        modules.add(new HighwayTools());
        modules.add(new HotbarManager());
        modules.add(new LiquidFillerHig());
        modules.add(new RotationLock());
        modules.add(new ScaffoldPlus());
        // Borers
        modules.add(new AxisBorer());
        modules.add(new NegNegBorer());
        modules.add(new NegPosBorer());
        modules.add(new PosNegBorer());
        modules.add(new PosPosBorer());
    }

    @Override
    public String getPackage() {
        return "me.redcarlos.higtools";
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(MAIN);
        Modules.registerCategory(BORERS);
    }

    public static Identifier identifier(String path) {
        return Identifier.of(HIGTools.MOD_ID, path);
    }
}
