package se.elemel.ld25.core;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;

import playn.core.PlayN;
import playn.core.Image;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.ResourceCallback;
import playn.core.Sound;

public class LandAnimalActor implements Actor {
	private GameState state;
	private Image image;
	private Sound sound;
	private GroupLayer parentLayer;
	private GroupLayer xLayer;
	private GroupLayer yLayer;
	private ImageLayer imageLayer;
	private float x;
	private float y;
	private float dx;
	private float dy;
	private float time;

	public LandAnimalActor(GameState state) {
		this.state = state;
		this.y = state.getPlanetRadius();
	}
	
	@Override
	public void init() {
		image = assets().getImage("images/horse.png");
		sound = assets().getSound("sounds/cow");
		sound.prepare();

		parentLayer = state.getForegroundLayer();
		xLayer = graphics().createGroupLayer();
		parentLayer.add(xLayer);
		yLayer = graphics().createGroupLayer();
		xLayer.add(yLayer);
		imageLayer = graphics().createImageLayer(image);
		yLayer.add(imageLayer);
	}

	@Override
	public void exit() {
		parentLayer.remove(layer);
	}

	@Override
	public void paint(float alpha) {
		xLayer.setRotation(x / y);
		yLayer.setTranslation(0.0f, -y * state.getPixelsPerMeter());
		imageLayer.setScale(state.getPixelsPerMeter() * 0.1f);
		imageLayer.setOrigin((float) image.width() / 2.0f, (float) image.height() / 2.0f);
	}

	@Override
	public void update(float delta) {
		dy -= state.getGravity() * delta * 0.001f;
		y += dy * delta * 0.001f;
		if (y < state.getPlanetRadius()) {
			y = state.getPlanetRadius();
			dy = 5.0f;
			sound.play();
		}
		x += 2.0f * delta * 0.001f;
	}
}
