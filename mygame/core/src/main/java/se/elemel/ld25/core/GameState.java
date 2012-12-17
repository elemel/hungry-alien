package se.elemel.ld25.core;

import java.util.*;

import playn.core.*;

public class GameState {
	private ResourceCache resourceCache;

	private GroupLayer rootLayer;
	private GroupLayer atmosphereLayer;
	private GroupLayer backgroundLayer;
	private GroupLayer planetLayer;
	private GroupLayer foregroundLayer;
	private GroupLayer vegetationLayer;
	private GroupLayer tractorBeamLayer;
	private GroupLayer animalLayer;
	private GroupLayer vehicleLayer;
	private GroupLayer alienShipLayer;
	private GroupLayer bulletLayer;

	private float time;

	private float planetRadius = 50.0f;
	private float gravity = 10.0f;
	private float pixelsPerMeter;
	private ArrayList<Actor> actors = new ArrayList<Actor>();
	private AlienShipActor alienShipActor;

	private float alienShipPolarPosition;
	private float alienShipRadialPosition;
	private float alienShipPolarVelocity;
	private boolean tractorBeamEnabled;
	private float tractorBeamRadialVelocity = 5.0f;
	private float tractorBeamWidth = 10.0f;

	private float cameraPolarPosition;
	private float frustumHeight = 40.0f;

	private HashSet<Key> pressedKeys = new HashSet<Key>();

	private boolean adultMode = false;
	
	public GameState(ResourceCache resourceCache) {
		this.resourceCache = resourceCache;
	}

	public void init() {
		int width = PlayN.graphics().width();
	    int height = PlayN.graphics().height();

	    pixelsPerMeter = (float) height / frustumHeight;
	    
	    rootLayer = PlayN.graphics().createGroupLayer();
	    PlayN.graphics().rootLayer().add(rootLayer);
	    atmosphereLayer = PlayN.graphics().createGroupLayer();
	    rootLayer.add(atmosphereLayer);
	    backgroundLayer = PlayN.graphics().createGroupLayer();
	    rootLayer.add(backgroundLayer);
	    planetLayer = PlayN.graphics().createGroupLayer();
	    rootLayer.add(planetLayer);
	    foregroundLayer = PlayN.graphics().createGroupLayer();
	    rootLayer.add(foregroundLayer);

	    vegetationLayer = PlayN.graphics().createGroupLayer();
	    backgroundLayer.add(vegetationLayer);
	    tractorBeamLayer = PlayN.graphics().createGroupLayer();
	    backgroundLayer.add(tractorBeamLayer);
	    animalLayer = PlayN.graphics().createGroupLayer();
	    foregroundLayer.add(animalLayer);
	    alienShipLayer = PlayN.graphics().createGroupLayer();
	    foregroundLayer.add(alienShipLayer);
	    bulletLayer = PlayN.graphics().createGroupLayer();
	    foregroundLayer.add(bulletLayer);
	    vehicleLayer = PlayN.graphics().createGroupLayer();
	    foregroundLayer.add(vehicleLayer);
	}

	public void exit() {
		for (Actor actor : actors) {
			actor.exit();
		}

		PlayN.graphics().rootLayer().remove(rootLayer);
	}
	
	public ResourceCache getResourceCache() {
		return resourceCache;
	}
	
	public boolean isAdultMode() {
		return adultMode;
	}

	public void addActor(Actor actor) {
		actors.add(actor);
		actor.init();
	}

	public void removeActor(Actor actor) {
		actor.exit();
		int i = actors.indexOf(actor);
		actors.set(i,  null);
	}

	public AlienShipActor getAlienShipActor() {
		return alienShipActor;
	}

	public void setAlienShipActor(AlienShipActor actor) {
		alienShipActor = actor;
	}

	public float getAlienShipPolarPosition() {
		return alienShipPolarPosition;
	}

	public void setAlienShipPolarPosition(float polarPosition) {
		alienShipPolarPosition = polarPosition;
	}

	public float getAlienShipRadialPosition() {
		return alienShipRadialPosition;
	}

	public void setAlienShipRadialPosition(float radialPosition) {
		alienShipRadialPosition = radialPosition;
	}

	public float getAlienShipPolarVelocity() {
		return alienShipPolarVelocity;
	}

	public void setAlienShipPolarVelocity(float polarVelocity) {
		alienShipPolarVelocity = polarVelocity;
	}

	public boolean isTractorBeamEnabled() {
		return tractorBeamEnabled;
	}

	public void setTractorBeamEnabled(boolean enabled) {
		tractorBeamEnabled = enabled;
	}
	
	public float getTractorBeamRadialVelocity() {
		return tractorBeamRadialVelocity;
	}

	public float getTractorBeamWidth() {
		return tractorBeamWidth;
	}

	public void paint(float alpha) {
		int width = PlayN.graphics().width();
	    int height = PlayN.graphics().height();

	    float planetRadiusInPixels = planetRadius * pixelsPerMeter;
	    float translationX = 0.5f * (float) width;
	    float translationY = 0.75f * (float) height + planetRadiusInPixels;
	    backgroundLayer.setTranslation(translationX, translationY);
	    foregroundLayer.setTranslation(translationX, translationY);
		for (Actor actor : actors) {
			if (actor != null) {
				actor.paint(alpha);
			}
		}
	}

	public void update(float delta) {
		time += delta * 0.001f;

		int actorCount = actors.size();
		for (int i = 0; i < actorCount; ++i) {
			Actor actor = actors.get(i);
			if (actor != null) {
				actor.update(delta);
			}
		}
		actors.remove(null);

		cameraPolarPosition += (alienShipPolarPosition - cameraPolarPosition) * 5.0f * delta * 0.001f;
		backgroundLayer.setRotation(-cameraPolarPosition);
		foregroundLayer.setRotation(-cameraPolarPosition);
	}

	public float getTime() {
		return time;
	}
	
	public GroupLayer getAtmosphereLayer() {
		return atmosphereLayer;
	}

	public GroupLayer getPlanetLayer() {
		return planetLayer;
	}

	public GroupLayer getTractorBeamLayer() {
		return tractorBeamLayer;
	}

	public GroupLayer getVegetationLayer() {
		return vegetationLayer;
	}

	public GroupLayer getAnimalLayer() {
		return animalLayer;
	}

	public GroupLayer getVehicleLayer() {
		return vehicleLayer;
	}

	public GroupLayer getAlienShipLayer() {
		return alienShipLayer;
	}

	public GroupLayer getBulletLayer() {
		return bulletLayer;
	}

	public float getPixelsPerMeter() {
		return pixelsPerMeter;
	}

	public float getPlanetRadius() {
		return planetRadius;
	}

	public float getGravity() {
		return gravity;
	}

	public float normalizePolarPosition(float position) {
		while (position < 0.0f) {
			position += 2.0f * Math.PI;
		}
		while (position > 2.0f * Math.PI) {
			position -= 2.0f * Math.PI;
		}
		return position;
	}

	public float normalizePolarOffset(float offset) {
		while (offset < -Math.PI) {
			offset += 2.0f * Math.PI;
		}
		while (offset > Math.PI) {
			offset -= 2.0f * Math.PI;
		}
		return offset;
	}

	public boolean isKeyPressed(Key key) {
		return pressedKeys.contains(key);
	}
	
	public void setKeyPressed(Key key, boolean pressed) {
		if (pressed) {
			pressedKeys.add(key);
		} else {
			pressedKeys.remove(key);
		}
	}
}
