package se.elemel.ld25.core;

import playn.core.*;

public class AnimalActor implements Actor {
	public enum Type { COW, HORSE, PIG, SHEEP };

	private GameState state;
	private ResourceCache resourceCache;
	private Type type;
	private Image image;
	private Sound animalSound;
	private GroupLayer parentLayer;
	private GroupLayer polarPositionLayer;
	private GroupLayer radialPositionLayer;
	private ImageLayer imageLayer;
	private float polarPosition;
	private float radialPosition;
	private float polarVelocity;
	private float radialVelocity;
	private float rotation;
	private float rotationalVelocity;
	private boolean alive = true;
	private boolean grounded = false;
	private float normalizedTractorBeamOffset;
	private float tractorBeamRotationalVelocity;
	private boolean flipped = false;

	public AnimalActor(GameState state, float polarPosition, Type type) {
		this.state = state;
		this.resourceCache = state.getResourceCache();
		this.type = type;
		this.polarPosition = polarPosition;
		this.radialPosition = state.getPlanetRadius();
	}
	
	@Override
	public void init() {
		switch (type) {
		case COW:
			image = resourceCache.getImage("cow");
			animalSound = resourceCache.getSound("cow");
			break;

		case HORSE:
			image = resourceCache.getImage("horse");
			animalSound = resourceCache.getSound("horse");
			break;

		case PIG:
			image = resourceCache.getImage("pig");
			animalSound = resourceCache.getSound("pig");
			break;

		case SHEEP:
			image = resourceCache.getImage("sheep");
			animalSound = resourceCache.getSound("sheep");
			break;
		}

		parentLayer = state.getAnimalLayer();
		polarPositionLayer = PlayN.graphics().createGroupLayer();
		parentLayer.add(polarPositionLayer);
		radialPositionLayer = PlayN.graphics().createGroupLayer();
		polarPositionLayer.add(radialPositionLayer);
		imageLayer = PlayN.graphics().createImageLayer(image);
		radialPositionLayer.add(imageLayer);
		
		flipped = (PlayN.random() > 0.5f);
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
		float scaleX = (flipped ? -1.0f : 1.0f) * scale;
		float scaleY = scale;
		imageLayer.setScale(scaleX, scaleY);

		imageLayer.setRotation(rotation);
		imageLayer.setOrigin((float) image.width() / 2.0f, (float) image.height() / 2.0f);
	}

	@Override
	public void update(float delta) {
		float tractorBeamPolarOffset = state.normalizePolarOffset(state.getAlienShipPolarPosition() - polarPosition);
		if (state.isTractorBeamEnabled() &&
				Math.abs(tractorBeamPolarOffset * radialPosition) < 0.5f * state.getTractorBeamWidth())
		{
			AlienShipActor alienShipActor = state.getAlienShipActor();
			if (alienShipActor != null) {
				alienShipActor.setHostile(true);
			}

			if (radialPosition > state.getAlienShipRadialPosition()) {
				if (alienShipActor != null) {
					alienShipActor.collect();
				}
				state.removeActor(this);
				return;
			}

			if (grounded) {
				grounded = false;
				
				normalizedTractorBeamOffset = 2.0f * PlayN.random() - 1.0f;
				tractorBeamRotationalVelocity = 2.0f * (2.0f * PlayN.random() - 1.0f);
			}

			radialVelocity += (state.getTractorBeamRadialVelocity() - radialVelocity) * delta * 0.001f;

			float targetPolarPosition = state.getAlienShipPolarPosition() +
					0.5f * state.getTractorBeamWidth() * 0.5f * normalizedTractorBeamOffset / radialPosition;
			polarPosition += state.normalizePolarOffset(targetPolarPosition - polarPosition) * delta * 0.001f;

			polarVelocity += (state.getAlienShipPolarVelocity() - polarVelocity) * delta * 0.001f;
			rotationalVelocity += (tractorBeamRotationalVelocity - rotationalVelocity) * delta * 0.001f;
			
			if (alive && !animalSound.isPlaying()) {
				animalSound.play();
			}
		} else {
			radialVelocity -= state.getGravity() * delta * 0.001f;
		}

		if (radialPosition < state.getPlanetRadius()) {
			if (!grounded) {
				grounded = true;

				if (radialVelocity < -10.0f && state.isAdultMode()) {
					alive = false;
				}
			}

			radialPosition = state.getPlanetRadius();

			if (alive) {
				radialVelocity = 2.0f + 2.0f * PlayN.random();
			} else {
				radialVelocity = 0.0f;
			}
		}
		
		if (grounded) {
			polarVelocity += 5.0f * (0.0f - polarVelocity) * delta * 0.001f;

			float targetRotation = alive ? 0.0f : (float) Math.PI;
			rotation += 5.0f * state.normalizePolarOffset(targetRotation - rotation) * delta * 0.001f;
			rotationalVelocity += 5.0f * (0.0f - rotationalVelocity) * delta * 0.001f;
		}

		polarPosition += polarVelocity * delta * 0.001f;
		radialPosition += radialVelocity * delta * 0.001f;
		rotation += rotationalVelocity * delta * 0.001f;
	}
}
