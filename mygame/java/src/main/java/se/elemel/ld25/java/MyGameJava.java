package se.elemel.ld25.java;

import playn.core.PlayN;
import playn.java.JavaPlatform;

import se.elemel.ld25.core.MyGame;

public class MyGameJava {
	public static void main(String[] args) {
		JavaPlatform.Config config = new JavaPlatform.Config();
		config.width = 1024;
		config.height = 576;
		JavaPlatform platform = JavaPlatform.register(config);
		platform.setTitle("Hungry Alien");
		platform.assets().setPathPrefix("se/elemel/ld25/resources");
		PlayN.run(new MyGame());
	}
}
