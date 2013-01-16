package se.elemel.ld25.core;

import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Key;
import playn.core.PlayN;
import playn.core.Sound;

public class AlienShipActor implements Actor {
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
	private float targetRadialOffset = 20.0f;
	private float maxPerimeterVelocity = 30.0f;
	private float maxTractorBeamPerimeterVelocity = 5.0f;
	private boolean flipped = false;
	private boolean hostile = false;

	private boolean entering = true;
	private boolean crashing = false;
	private float crashTime;
	private boolean leaving = false;
	private float liftTime;
	private float leaveTime;
	private int collectCount = 0;
	private int targetCollectCount = 10;

	private GroupLayer tractorBeamParentLayer;
	private GroupLayer tractorBeamPolarPositionLayer;
	private GroupLayer tractorBeamRadialPositionLayer;
	private Image tractorBeamImage;
	private ImageLayer tractorBeamImageLayer;
	private boolean tractorBeamFlippedX;
	private boolean tractorBeamFlippedY;
	private float tractorBeamFlipTime;
	private float rotationalVelocity;
	private float rotation;

	private Sound collectOneSound;
	private Sound collectAllSound;

	public AlienShipActor(GameState state, float polarPosition) {
		this.state = state;
		this.resourceCache = state.getResourceCache();
		this.polarPosition = polarPosition;
		radialPosition = state.getPlanetRadius() + 50.0f;
	}
	
	@Override
	public void init() {
		image = resourceCache.getImage("alien-ship");
		collectOneSound = resourceCache.getSound("collect-one");
		collectAllSound = resourceCache.getSound("collect-all");

		parentLayer = state.getAlienShipLayer();
		polarPositionLayer = PlayN.graphics().createGroupLayer();
		parentLayer.add(polarPositionLayer);
		radialPositionLayer = PlayN.graphics().createGroupLayer();
		polarPositionLayer.add(radialPositionLayer);
		imageLayer = PlayN.graphics().createImageLayer(image);
		radialPositionLayer.add(imageLayer);
		state.setAlienShipActor(this);

		tractorBeamParentLayer = state.getTractorBeamLayer();
		tractorBeamPolarPositionLayer = PlayN.graphics().createGroupLayer();
		tractorBeamParentLayer.add(tractorBeamPolarPositionLayer);
		tractorBeamRadialPositionLayer = PlayN.graphics().createGroupLayer();
		tractorBeamPolarPositionLayer.add(tractorBeamRadialPositionLayer);
		tractorBeamImage = resourceCache.getImage("tractor-beam");
		tractorBeamImageLayer = PlayN.graphics().createImageLayer(tractorBeamImage);
		tractorBeamRadialPositionLayer.add(tractorBeamImageLayer);
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

		float scale = state.getPixelsPerMeter() * 0.1f;
		float scaleX = (flipped ? -1.0f : 1.0f) * scale;
		float scaleY = scale;
		imageLayer.setScale(scaleX, scaleY);
		imageLayer.setOrigin((float) image.width() / 2.0f, (float) image.height() / 2.0f);
		imageLayer.setRotation(rotation);

		if (state.isTractorBeamEnabled()) {
			tractorBeamPolarPositionLayer.setVisible(true);
			tractorBeamPolarPositionLayer.setRotation(polarPosition);

			float tractorBeamRadialPosition = 0.5f * (radialPosition + state.getPlanetRadius());
			tractorBeamRadialPositionLayer.setTranslation(0.0f, -tractorBeamRadialPosition * state.getPixelsPerMeter());

			tractorBeamImageLayer.setOrigin((float) tractorBeamImage.width() / 2.0f, (float) tractorBeamImage.height() / 2.0f);
			float tractorBeamScaleX = (tractorBeamFlippedX ? -1.0f : 1.0f) * scale;
			float tractorBeamScaleY = (tractorBeamFlippedY ? -1.0f : 1.0f) * scale;
			tractorBeamImageLayer.setScale(tractorBeamScaleX, tractorBeamScaleY);
		} else {
			tractorBeamPolarPositionLayer.setVisible(false);
		}
	}

	@Override
	public void update(float delta) {
		if (crashing) {
			if (state.getTime() > crashTime) {
				state.removeActor(this);
				return;
			}

			polarVelocity += (0.0f - polarVelocity) * delta * 0.001f;
			polarPosition += polarVelocity * delta * 0.001f;
			radialVelocity -= state.getGravity() * delta * 0.001f;
			radialPosition += radialVelocity * delta * 0.001f;
			rotation += rotationalVelocity * delta * 0.001f;
			state.setTractorBeamEnabled(false);
			return;
		}
		
		if (leaving) {
			if (state.getTime() > leaveTime) {
				state.removeActor(this);
				return;
			}
		}
		
		boolean leftInput = state.isKeyPressed(Key.A) || state.isKeyPressed(Key.LEFT);
		boolean tractorBeamInput = !entering && !leaving && state.isKeyPressed(Key.SPACE);
		boolean rightInput = state.isKeyPressed(Key.D) || state.isKeyPressed(Key.RIGHT);

		float polarVelocityInput = (tractorBeamInput ? maxTractorBeamPerimeterVelocity : maxPerimeterVelocity) / radialPosition *
				((rightInput ? 1.0f : 0.0f) - (leftInput ? 1.0f : 0.0f));
		polarVelocity += (polarVelocityInput - polarVelocity) * delta * 0.001f;
		polarPosition += polarVelocity * delta * 0.001f;

		if (leaving && state.getTime() > liftTime) {
			radialPosition += (state.getPlanetRadius() + 50.0f - radialPosition) * delta * 0.001f;
		} else {
			radialPosition += 2.0f * (state.getPlanetRadius() + targetRadialOffset - radialPosition) * delta * 0.001f;
			if (Math.abs(state.getPlanetRadius() + targetRadialOffset - radialPosition) < 1.0f) {
				entering = false;
			}
		}
		
		if (leftInput && !rightInput) {
			flipped = true;
		}
		if (!leftInput && rightInput) {
			flipped = false;
		}

		state.setAlienShipPolarPosition(polarPosition);
		state.setAlienShipRadialPosition(radialPosition);
		state.setAlienShipPolarVelocity(polarVelocity);
		state.setTractorBeamEnabled(tractorBeamInput);

		if (state.getTime() > tractorBeamFlipTime) {
			tractorBeamFlippedX = PlayN.random() < 0.5f;
			tractorBeamFlippedY = PlayN.random() < 0.5f;
			tractorBeamFlipTime = state.getTime() + 0.1f * (2.0f + PlayN.random());
		}
	}

	public void hit() {
		crashing = true;
		crashTime = state.getTime() + 1.0f;
		rotationalVelocity = (PlayN.random() < 0.5f ? -1.0f : 1.0f) * (1.0f + 1.0f * PlayN.random());
	}

	public boolean isCrashing() {
		return crashing;
	}

	public boolean isHostile() {
		return hostile;
	}
	
	public void setHostile(boolean hostile) {
		this.hostile = hostile;
	}

	public void collect() {
		++collectCount;
		if (collectCount == targetCollectCount) {
			collectAllSound.play();

			leaving = true;
			liftTime = state.getTime() + 1.0f;
			leaveTime = state.getTime() + 2.0f;
		} else {
			collectOneSound.play();			
		}
	}
}
