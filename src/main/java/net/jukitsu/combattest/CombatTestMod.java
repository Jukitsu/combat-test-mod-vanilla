package net.jukitsu.combattest;

import net.fabricmc.api.ModInitializer;
import net.jukitsu.combattest.enchantment.Cleaving;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.Enchantment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CombatTestMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger("combattest");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Initialized Combat Test Mod!");
	}
	private static final Enchantment CLEAVING = Registry.register(BuiltInRegistries.ENCHANTMENT, "cleaving", new Cleaving());
}
