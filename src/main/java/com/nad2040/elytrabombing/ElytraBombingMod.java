package com.nad2040.elytrabombing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;

import java.io.*;
import java.util.Objects;

public class ElytraBombingMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final String MODID = "elytrabombing";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
	public static final Boolean SHOULD_LOG = Objects.requireNonNull(load_config("should_log")).getAsBoolean();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

        LOGGER.info("Elytra Bombing Mod initialized!");
	}

	public static BlockPos VEC_3D_TO_POS(Vec3d vec3d) {
		return new BlockPos((int)Math.round(vec3d.x), (int)Math.round(vec3d.y), (int)Math.round(vec3d.z));
	}

	public static void log(Hand hand, Hand other_hand, ItemStack usedItemStack, ItemStack otherItemStack, Vec3d position, Vec3d velocity) {
		LOGGER.info("right click action detected");
		LOGGER.info("hand is " + ((hand == Hand.MAIN_HAND) ? "main hand" : "off hand"));
		LOGGER.info("other hand is " + ((other_hand == Hand.MAIN_HAND) ? "main hand" : "off hand"));
		LOGGER.info("used item: " + usedItemStack);
		LOGGER.info("other item: " + otherItemStack);
		LOGGER.info("player pos: " + position);
		LOGGER.info("player vel: " + velocity);
	}

	private static JsonElement load_config(String key) {
		Gson gson = new Gson();
		File dir = new File("config");
		if (dir.mkdir()) LOGGER.info("created config directory");
		File f = new File(dir, MODID+".json");
		if (!f.exists()) {
			try (FileWriter fw = new FileWriter(f)) {
				JsonObject new_conf = new JsonObject();
				new_conf.addProperty("should_log",true);
				gson.toJson(new_conf, new JsonWriter(fw));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		try (FileReader fr = new FileReader(f)) {
			JsonObject json = gson.fromJson(new JsonReader(fr), JsonObject.class);
			return json.has(key) ? json.get(key) : null;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public interface FBEInterface {
		void setBlock(BlockState state);
	}
}
