package com.zaixiayesheng.ae2_emi_quick_fill;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AE2: EMI Quick Fill — main mod entry point.
 *
 * <p>This mod is purely <b>client-side</b>. It contains no blocks, items, packets or
 * registry entries. All functionality is implemented by a single Mixin
 * ({@link com.zaixiayesheng.ae2_emi_quick_fill.mixin.Ae2AbstractRecipeHandlerMixin}) that lets
 * EMI's shift-click recipe transfer work inside AE2 terminals.
 *
 * <p>The {@code dist = Dist.CLIENT} annotation ensures this mod is not constructed on
 * a dedicated server, and {@code displayTest = "IGNORE_ALL_VERSION"} in
 * {@code neoforge.mods.toml} prevents the server list from flagging the mod as
 * incompatible when the server does not have it installed.
 *
 * @author zaixiayesheng (在下叶笙)
 */
// FML 11 (NeoForge 26.1): @Mod#dist is now Dist[] — single-element array shorthand is kept
@Mod(value = EmiAe2ShiftCraft.MOD_ID, dist = {Dist.CLIENT})
public final class EmiAe2ShiftCraft {

    /** Mod identifier, must match {@code mod_id} in gradle.properties and neoforge.mods.toml. */
    public static final String MOD_ID = "ae2_emi_quick_fill";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public EmiAe2ShiftCraft(IEventBus modEventBus) {
        LOGGER.info("AE2: EMI Quick Fill loaded — shift-click a recipe to pull it into any AE2 terminal.");
    }
}
