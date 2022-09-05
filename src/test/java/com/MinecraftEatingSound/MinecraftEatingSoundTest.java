package com.MinecraftEatingSound;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class MinecraftEatingSoundTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(MinecraftEatingSoundPlugin.class);
		RuneLite.main(args);
	}
}