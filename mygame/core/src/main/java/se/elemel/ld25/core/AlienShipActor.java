package se.elemel.ld25.core;

import playn.core.*;

public class AlienShipActor implements Actor {
	private GameState state;
	private Image image;
	private Sound sound;
	private GroupLayer parentLayer;
	private GroupLayer polarPositionLayer;
	private GroupLayer radialPositionLayer;
	private ImageLayer imageLayer;
	private float polarPosition;
	private float radialPosition;
	private float polarVelocity;
	private float radialVelocity;
	private float radialOffset = 20.0f;
	private float maxPolarVelocity = 20.0f;

	public AlienShipActor(GameState state, float polarPosition) {
		this.state = state;
		this.polarPosition = polarPosition;
		radialPosition = state.getPlanetRadius() + radialOffset;
	}
	
	@Override
	public void init() {
		image = PlayN.assets().getImage("images/alien-ship.png");

		parentLayer = state.getAlienShipLayer();
		polarPositionLayer = PlayN.graphics().createGroupLayer();
		parentLayer.add(polarPositionLayer);
		radialPositionLayer = PlayN.graphics().createGroupLayer();
		polarPositionLayer.add(radialPositionLayer);
		imageLayer = PlayN.graphics().createImageLayer(image);
		radialPositionLayer.add(imageLayer);
		state.setAlienShipActor(this);
	}

	@Override
	public void exit() {
		state.setAlienShipActor(null);
		parentLayer.remove(polarPositionLayer);
	}

	@Override
	public void paint(float alpha) {
		polarPositionLayer.setRotation(polarPosition);
		radialPositionLayer.setTranslation(0.0f, -radialPosition * state.getPixelsPerMeter());
		imageLayer.setScale(state.getPixelsPerMeter() * 0.1f);
		imageLayer.setOrigin((float) image.width() / 2.0f, (float) image.height() / 2.0f);
	}

	@Override
	public void update(float delta) {
		boolean upInput = state.isKeyPressed(Key.W) || state.isKeyPressed(Key.UP);
		boolean leftInput = state.isKeyPressed(Key.A) || state.isKeyPressed(Key.LEFT);
		boolean downInput = state.isKeyPressed(Key.S) || state.isKeyPressed(Key.DOWN);
		boolean rightInput = state.isKeyPressed(Key.D) || state.isKeyPressed(Key.RIGHT);

		float polarVelocityInput = maxPolarVelocity / radialPosition * ((rightInput ? 1.0f : 0.0f) - (leftInput ? 1.0f : 0.0f));
		polarVelocity += (polarVelocityInput - polarVelocity) * delta * 0.001f;
		polarPosition += polarVelocity * delta * 0.001f;

		state.setAlienShipPolarPosition(polarPosition);
		state.setAlienShipRadialPosition(radialPosition);
		state.setAlienShipPolarVelocity(polarVelocity);
		state.setTractorBeamEnabled(downInput);
	}
}
