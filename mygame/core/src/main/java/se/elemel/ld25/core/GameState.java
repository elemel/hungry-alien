package se.elemel.ld25.core;

import java.util.*;

import static playn.core.PlayN.*;

import playn.core.Game;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;

public class GameState {
	private GroupLayer backgroundLayer;
	private GroupLayer foregroundLayer;

	private float planetRadius = 50.0f;
	private float gravity = 10.0f;
	private float pixelsPerMeter = 20.0f;
	private ArrayList<Actor> actors = new ArrayList<Actor>();

	public void init() {
	    backgroundLayer = graphics().createGroupLayer();
	    graphics().rootLayer().add(backgroundLayer);
	    foregroundLayer = graphics().createGroupLayer();
	    graphics().rootLayer().add(foregroundLayer);
	}

	public void exit() {
		for (Actor actor : actors) {
			actor.exit();
		}
	    graphics().rootLayer().remove(backgroundLayer);
	    graphics().rootLayer().remove(foregroundLayer);
	}

	public void addActor(Actor actor) {
		actors.add(actor);
		actor.init();
	}

	public void removeActor(Actor actor) {
		actor.exit();
		actors.remove(actor);
	}

	public void paint(float alpha) {
		int width = graphics().width();
	    int height = graphics().height();

	    int planetRadiusInPixels = (int) (planetRadius * pixelsPerMeter + 0.5f);		
		foregroundLayer.setTranslation((float) (width / 2), (float) (height * 3 / 4 + planetRadiusInPixels));
		for (Actor actor : actors) {
			actor.paint(alpha);
		}
	}

	public void update(float delta) {
		for (Actor actor : actors) {
			actor.update(delta);
		}
	}

	public GroupLayer getBackgroundLayer() {
		return backgroundLayer;
	}

	public GroupLayer getForegroundLayer() {
		return foregroundLayer;
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
}
