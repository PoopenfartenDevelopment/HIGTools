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
import org.slf4j.Logger;

public class HIGTools extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final ModMetadata METADATA = FabricLoader.getInstance().getModContainer("higtools").orElseThrow(() -> new RuntimeException("HIGTools mod container not found!")).getMetadata();
    public static final String VERSION = METADATA.getVersion().toString();
    public static final Category MAIN = new Category("HIG Tools", Items.NETHERITE_PICKAXE.getDefaultStack());
    public static final Category BORERS = new Category(" Borers ", Items.NETHERITE_PICKAXE.getDefaultStack());
    public static final HudGroup HUD = new HudGroup("HIG Tools");

    @Override
    public void onInitialize() {
        LOG.info("Initializing HIGTools {}", HIGTools.VERSION);

        BetterChat.registerCustomHead("[HIGTools]", new Identifier("higtools", "chat/icon.png"));

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
}
