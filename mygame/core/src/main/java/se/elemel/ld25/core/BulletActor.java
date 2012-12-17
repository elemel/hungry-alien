package se.elemel.ld25.core;

import playn.core.*;

public class BulletActor implements Actor {
	private GameState state;
	private ResourceCache resourceCache;
	private Image image;
	private GroupLayer parentLayer;
	private GroupLayer polarPositionLayer;
	private GroupLayer radialPositionLayer;
	private ImageLayer imageLayer;
	private float polarPosition;
	private float radialPosition;
	private float perimeterVelocity;
	private float polarVelocity;
	private float radialVelocity;
	private float rotation;
	private float rotationalVelocity;
	private float maxRadialPosition;
	private float maxPerimeterVelocity = 10.0f;

	private Sound shotSound;
	private Sound explosionSound;
	
	public BulletActor(GameState state, float polarPosition) {
		this.state = state;
		this.resourceCache = state.getResourceCache();
		this.polarPosition = polarPosition;
		this.radialPosition = state.getPlanetRadius();
		this.maxRadialPosition = state.getPlanetRadius() + 100.0f;
		this.rotationalVelocity = 2.0f * (2.0f * PlayN.random() - 1.0f);
	}
	
	@Override
	public void init() {
		image = resourceCache.getImage("bullet");

		parentLayer = state.getBulletLayer();
		polarPositionLayer = PlayN.graphics().createGroupLayer();
		parentLayer.add(polarPositionLayer);
		radialPositionLayer = PlayN.graphics().createGroupLayer();
		polarPositionLayer.add(radialPositionLayer);
		imageLayer = PlayN.graphics().createImageLayer(image);
		radialPositionLayer.add(imageLayer);

		shotSound = resourceCache.getSound("shot");
		explosionSound = resourceCache.getSound("explosion");
		if (!shotSound.isPlaying()) {
			shotSound.play();
		}

		radialVelocity = 3.0f + 3.0f * PlayN.random();
		float alienShipPolarOffset = state.normalizePolarOffset(state.getAlienShipPolarPosition() - polarPosition);
		float alienShipPerimeterOffset = alienShipPolarOffset * state.getPlanetRadius();
		perimeterVelocity = (10.0f + 10.0f * PlayN.random()) * alienShipPolarOffset;
		if (Math.abs(perimeterVelocity) > maxPerimeterVelocity) {
			float perimeterVelocitySign = (perimeterVelocity < 0.0f) ? -1.0f : 1.0f;
			perimeterVelocity = perimeterVelocitySign * Math.min(Math.abs(perimeterVelocity), maxPerimeterVelocity);
		}
	}

	@Override
	public void exit() {
		parentLayer.remove(polarPositionLayer);
	}

	@Override
	public void paint(float alpha) {
		polarPositionLayer.setRotation(polarPosition);
		radialPositionLayer.setTranslation(0.0f, -radialPosition * state.getPixelsPerMeter());

		float scale = state.getPixelsPerMeter() * 0.1f;
		imageLayer.setScale(scale, scale);
		imageLayer.setOrigin((float) image.width() / 2.0f, (float) image.height() / 2.0f);
		imageLayer.setRotation(rotation);
	}

	@Override
	public void update(float delta) {
		if (radialPosition > maxRadialPosition) {
			state.removeActor(this);
			return;
		}

		AlienShipActor alienShipActor = state.getAlienShipActor();
		float alienShipPolarOffset = state.normalizePolarOffset(state.getAlienShipPolarPosition() - polarPosition);
		float alienShipPerimeterOffset = alienShipPolarOffset * state.getPlanetRadius();
		if (alienShipActor != null && !alienShipActor.isCrashing() &&
				Math.abs(alienShipPerimeterOffset) < 0.5f * state.getTractorBeamWidth() &&
				radialPosition > state.getAlienShipRadialPosition() - 2.0f &&
				radialPosition < state.getAlienShipRadialPosition() + 2.0f)
		{
			if (!explosionSound.isPlaying()) {
				explosionSound.play();
			}
			
			alienShipActor.hit();

			state.removeActor(this);
			return;
		}

		radialPosition += radialVelocity * delta * 0.001f;
		polarVelocity = perimeterVelocity / radialPosition;
		polarPosition += polarVelocity * delta * 0.001f;
		rotation += rotationalVelocity * delta * 0.001f;
	}
}
