package com.MinecraftEatingSound;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.sound.sampled.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.SoundEffectPlayed;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.io.IOException;
import java.net.URL;

@Slf4j
@PluginDescriptor(
		name = "Minecraft Eating Sound",
		enabledByDefault = false,
		description = "Changes the default eating sound to the one from Minecraft"
)
public class MinecraftEatingSoundPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private MinecraftEatingSoundConfig config;

	private Clip clip;

	String wavPath = "Minecraft Eating - Sound Effect.wav";

	@Provides
	MinecraftEatingSoundConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MinecraftEatingSoundConfig.class);
	}

	@Subscribe
	public void onCommandExecuted(CommandExecuted event)
	{
		if (event.getCommand().equals("eat"))
		{
			playEat();
		}
	}

	@Subscribe
	public void onSoundEffectPlayed(SoundEffectPlayed event)
	{
		if (event.getSoundId() == 2393)
		{
			event.consume();
			playEat();
		}
	}

	public void playEat()
	{
		try {
			if (clip != null)
			{
				clip.close();
			}

			Class pluginClass = null;
			AudioInputStream stream = null;
			try {
				pluginClass = Class.forName("com.MinecraftEatingSound.MinecraftEatingSoundPlugin");
				URL url = pluginClass.getClassLoader().getResource(wavPath);
				stream = AudioSystem.getAudioInputStream(url);
			} catch (ClassNotFoundException | UnsupportedAudioFileException | IOException e) {
				e.printStackTrace();
			}

			if (stream == null)
			{
				return;
			}

			AudioFormat format = stream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(info);

			clip.open(stream);

			FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			float volumeValue = volume.getMinimum() + ((50 + (config.volumeLevel()*5)) * ((volume.getMaximum() - volume.getMinimum()) / 100));

			volume.setValue(volumeValue);

			clip.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}

