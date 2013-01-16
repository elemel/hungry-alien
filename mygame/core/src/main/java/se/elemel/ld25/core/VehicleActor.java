package se.elemel.ld25.core;

import playn.core.*;

public class VehicleActor implements Actor {
	private GameState state;
	private ResourceCache resourceCache;
	private Image image;
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
	private float maxPerimeterVelocity;
	private float nextFireTime;
	private boolean flipped = false;

	public VehicleActor(GameState state, float polarPosition) {
		this.state = state;
		this.resourceCache = state.getResourceCache();
		this.polarPosition = polarPosition;
		this.radialPosition = state.getPlanetRadius();
	}
	
	@Override
	public void init() {
		image = resourceCache.getImage("tank");

		parentLayer = state.getVehicleLayer();
		polarPositionLayer = PlayN.graphics().createGroupLayer();
		parentLayer.add(polarPositionLayer);
		radialPositionLayer = PlayN.graphics().createGroupLayer();
		polarPositionLayer.add(radialPositionLayer);
		imageLayer = PlayN.graphics().createImageLayer(image);
		radialPositionLayer.add(imageLayer);
		
		maxPerimeterVelocity = 2.0f + 2.0f * PlayN.random();
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
		boolean beamable = false;
		if (beamable && state.isTractorBeamEnabled() &&
				Math.abs(tractorBeamPolarOffset * radialPosition) < 0.5f * state.getTractorBeamWidth())
		{
			if (radialPosition > state.getAlienShipRadialPosition()) {
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
			radialVelocity = 0.0f;
		}
		
		if (grounded) {
			float maxPolarVelocity = maxPerimeterVelocity / state.getPlanetRadius();
			float targetPolarVelocity = maxPolarVelocity * (flipped ? 1.0f : -1.0f);
			polarVelocity += 5.0f * (targetPolarVelocity - polarVelocity) * delta * 0.001f;

			float targetRotation = alive ? 0.0f : (float) Math.PI;
			rotation += 5.0f * state.normalizePolarOffset(targetRotation - rotation) * delta * 0.001f;
			rotationalVelocity += 5.0f * (0.0f - rotationalVelocity) * delta * 0.001f;

			AlienShipActor alienShipActor = state.getAlienShipActor();
			float alienShipPolarOffset = state.normalizePolarOffset(state.getAlienShipPolarPosition() - polarPosition);
			float alienShipPerimeterOffset = alienShipPolarOffset * state.getPlanetRadius();
			if (alienShipActor != null && alienShipActor.isHostile() && !alienShipActor.isCrashing() &&
					Math.abs(alienShipPolarOffset) < Math.PI / 4.0f && Math.abs(alienShipPerimeterOffset) < 50.0f &&
					state.getTime() > nextFireTime)
			{
				state.addActor(new BulletActor(state, polarPosition));
				nextFireTime = state.getTime() + 0.5f + 2.0f * PlayN.random();
			}
		}

		polarPosition += polarVelocity * delta * 0.001f;
		radialPosition += radialVelocity * delta * 0.001f;
		rotation += rotationalVelocity * delta * 0.001f;
	}
}
