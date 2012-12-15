package se.elemel.ld25.core;

import static playn.core.PlayN.*;

import playn.core.Game;
import playn.core.Image;
import playn.core.ImageLayer;

public class MyGame implements Game {
	Screen screen;

	@Override
	public void init() {
		// create and add background image layer
		// Image bgImage = assets().getImage("images/bg.png");
		// ImageLayer bgLayer = graphics().createImageLayer(bgImage);
		// graphics().rootLayer().add(bgLayer);
		screen = new PlanetScreen();
		screen.init();
	}

	@Override
	public void paint(float alpha) {
		screen.paint(alpha);
	}

	@Override
	public void update(float delta) {
		screen.update(delta);
	}

	@Override
	public int updateRate() {
		return 25;
	}
}
