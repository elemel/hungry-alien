package se.elemel.ld25.core;

import playn.core.*;

public class MyGame implements Game {
	private Screen screen;
	private ResourceCache resourceCache = new ResourceCache();

	@Override
	public void init() {
		// graphics().ctx().setTextureFilter(GLContext.Filter.NEAREST, GLContext.Filter.NEAREST);
		
		screen = new TitleScreen(this);
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

	public Screen getScreen() {
		return screen;
	}

	public void setScreen(Screen screen) {
		this.screen.exit();
		this.screen = screen;
		this.screen.init();
	}

	public ResourceCache getResourceCache() {
		return resourceCache;
	}
}
