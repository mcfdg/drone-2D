package com.distlestudio.drone2d.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.distlestudio.drone2d.Drone2D;

public class DesktopLauncher {
	public static void main (String[] arg) {
		TexturePacker.Settings settings = new TexturePacker.Settings();
		settings.maxWidth = 1024;
		settings.maxHeight = 1024;
		settings.combineSubdirectories=true;
		TexturePacker.process(settings, "../assets/images", "../assets", "images");

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = (int)(Drone2D.WIDTH * 3.5);
		config.height = (int)(Drone2D.HEIGHT * 3.5);
		new LwjglApplication(new Drone2D(), config);
	}
}
