package se.elemel.ld25.core;

import static playn.core.PlayN.*;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Game;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Surface;
import playn.core.SurfaceLayer;

public class PlanetScreen implements Screen {
	private GameState state;

	private SurfaceLayer layer;
	private CanvasImage skyImage;
	private CanvasImage planetImage;

	@Override
	public void init() {
		state = new GameState();
		state.init();

		int width = graphics().width();
	    int height = graphics().height();

	    layer = graphics().createSurfaceLayer(width, height);
	    state.getBackgroundLayer().add(layer);

	    skyImage = graphics().createImage(width, height);
	    Canvas canvas = skyImage.canvas();
	    canvas.setFillColor(0xff0099cc);
	    canvas.fillRect(0, 0, width, height);

		state.addActor(new LandAnimalActor(state));

	    int planetRadiusInPixels = (int) (state.getPlanetRadius() * state.getPixelsPerMeter() + 0.5f);
	    planetImage = graphics().createImage(width, height / 4);
	    canvas = planetImage.canvas();
	    canvas.setFillColor(0xff33cc00);
	    canvas.fillCircle(width / 2, planetRadiusInPixels, planetRadiusInPixels);		
	}

	@Override
	public void exit() {
		state.exit();
	}
	
	@Override
	public void paint(float alpha) {
	    int width = graphics().width();
	    int height = graphics().height();

	    Surface surface = layer.surface();
		surface.clear();
		surface.drawImage(skyImage, 0, 0);
		surface.drawImage(planetImage, 0, height - planetImage.height());

		state.paint(alpha);
	}

	@Override
	public void update(float delta) {
		state.update(delta);
	}
}
