package se.elemel.ld25.core;

import static playn.core.PlayN.*;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Game;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Surface;
import playn.core.SurfaceLayer;

class PlanetScreen implements Screen {
	private SurfaceLayer layer;
	private CanvasImage skyImage;
	private CanvasImage planetImage;
	  
	@Override
	public void init() {
	    int width = graphics().width();
	    int height = graphics().height();
	    layer = graphics().createSurfaceLayer(width, height);
	    graphics().rootLayer().add(layer);

	    skyImage = graphics().createImage(width, height);
	    Canvas canvas = skyImage.canvas();
	    canvas.setFillColor(0xff0099cc);
	    canvas.fillRect(0, 0, width, height);

	    float pixelsPerMeter = 10.0f;
	    float planetRadius = 100.0f;
	    
	    int planetRadiusInPixels = (int) (planetRadius * pixelsPerMeter);
	    int circleX = 0;
	    int circleY = 0;
	    planetImage = graphics().createImage(width, height / 4);
	    canvas = planetImage.canvas();
	    canvas.setFillColor(0xff33cc00);
	    canvas.fillCircle(width / 2, planetRadiusInPixels, planetRadiusInPixels);		
	}

	@Override
	public void exit() { }
	
	@Override
	public void paint(float alpha) {
	    int width = graphics().width();
	    int height = graphics().height();
		Surface surface = layer.surface();
		surface.clear();
		surface.drawImage(skyImage, 0, 0);
		surface.drawImage(planetImage, 0, height - planetImage.height());
	}

	@Override
	public void update(float delta) {
	}
}
