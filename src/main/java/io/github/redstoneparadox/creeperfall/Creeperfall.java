package io.github.redstoneparadox.creeperfall;

import io.github.redstoneparadox.creeperfall.game.config.CreeperfallConfig;
import io.github.redstoneparadox.creeperfall.game.CreeperfallWaiting;
import io.github.redstoneparadox.creeperfall.item.CreeperfallItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.plasmid.api.game.GameType;

public class Creeperfall implements ModInitializer {

    public static final String ID = "creeperfall";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    public static final GameType<CreeperfallConfig> TYPE = GameType.register(
            Identifier.of(ID, "creeperfall"),
            CreeperfallConfig.CODEC,
            CreeperfallWaiting::open
    );

    @Override
    public void onInitialize() {
        CreeperfallItems.init();
    }
}
