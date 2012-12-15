package se.elemel.ld25.core;

import static playn.core.PlayN.*;

import playn.core.Game;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.gl.GLContext;

public class MyGame implements Game {
	Screen screen;

	@Override
	public void init() {
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
