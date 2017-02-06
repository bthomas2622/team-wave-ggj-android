package com.mygdx.ggjteamwave.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.ggjteamwave.TeamWave;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Team Wave";
        config.width = 1920;
        config.height = 1080;
		new LwjglApplication(new TeamWave(), config);
	}
}
