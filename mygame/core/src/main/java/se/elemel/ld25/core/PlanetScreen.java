package se.elemel.ld25.core;

import playn.core.*;

public class PlanetScreen implements Screen {
	private GameState state;

	private SurfaceLayer layer;
	private CanvasImage skyImage;
	private CanvasImage planetImage;

	@Override
	public void init() {
		state = new GameState();
		state.init();

		int width = PlayN.graphics().width();
	    int height = PlayN.graphics().height();

	    layer = PlayN.graphics().createSurfaceLayer(width, height);
	    state.getBackgroundLayer().add(layer);

		state.addActor(new AlienShipActor(state, 0.0f));
		state.addActor(new LandAnimalActor(state, 0.0f, LandAnimalActor.Type.COW));
		state.addActor(new LandAnimalActor(state, 1.0f, LandAnimalActor.Type.SHEEP));
		state.addActor(new LandAnimalActor(state, -1.0f, LandAnimalActor.Type.HORSE));
		state.addActor(new LandAnimalActor(state, -2.0f, LandAnimalActor.Type.PIG));

		int treeCount = 20;
		for (int i = 0; i < treeCount; ++i) {
			float polarPosition = 2.0f * (float) Math.PI * (float) i / (float) treeCount +
					2.0f * (float) Math.PI * PlayN.random() / (float) treeCount;
			createTree(polarPosition);
		}
		
	    float planetRadiusInPixels = state.getPlanetRadius() * state.getPixelsPerMeter() + 0.5f;
	    planetImage = PlayN.graphics().createImage(width, height / 4);
	    Canvas planetCanvas = planetImage.canvas();
	    planetCanvas.setFillColor(0xff33cc00);
	    planetCanvas.fillCircle(0.5f * (float) width, planetRadiusInPixels, planetRadiusInPixels);		

	    PlayN.keyboard().setListener(new Keyboard.Adapter() {
	    	@Override
	    	public void onKeyDown(Keyboard.Event event) {
	    		state.setKeyPressed(event.key(), true);
	    	}

	    	@Override
	    	public void onKeyUp(Keyboard.Event event) {
	    		state.setKeyPressed(event.key(), false);
	    	}
	    });

	    skyImage = PlayN.graphics().createImage(width, height);
	    Canvas canvas = skyImage.canvas();
	    float atmosphereRadialOffset = 40.0f;
	    float atmosphereRadius = state.getPlanetRadius() + atmosphereRadialOffset;
	    float atmosphereRadiusInPixels = atmosphereRadius * state.getPixelsPerMeter();
	    canvas.setFillColor(0xff000000);
	    canvas.fillRect(0, 0, width, height);

	    int[] colors = { 0xff00ccff, 0x00000000 };
	    float[] colorPositions = { 1.0f - atmosphereRadialOffset / atmosphereRadius, 1.0f };
	    Gradient gradient = PlayN.graphics().createRadialGradient(0.5f * (float) width,
	    		0.75f * (float) height + planetRadiusInPixels, atmosphereRadiusInPixels, colors, colorPositions);
	    canvas.setFillGradient(gradient);
	    canvas.fillRect(0, 0, width, height);
	    canvas.setFillGradient(null);
	}

	@Override
	public void exit() {
		state.exit();
	}
	
	@Override
	public void paint(float alpha) {
		int width = PlayN.graphics().width();
	    int height = PlayN.graphics().height();

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

	private void createTree(float polarPosition) {
		GroupLayer polarPositionLayer = PlayN.graphics().createGroupLayer();
		polarPositionLayer.setRotation(polarPosition);
		state.getVegetationLayer().add(polarPositionLayer);

		GroupLayer radialPositionLayer = PlayN.graphics().createGroupLayer();
		radialPositionLayer.setTranslation(0.0f, -(state.getPlanetRadius() + 2.0f + PlayN.random()) * state.getPixelsPerMeter());
		polarPositionLayer.add(radialPositionLayer);

		Image image = PlayN.assets().getImageSync("images/tree.png");
		ImageLayer imageLayer = PlayN.graphics().createImageLayer(image);
		imageLayer.setScale(state.getPixelsPerMeter() * 0.1f);
		imageLayer.setOrigin((float) image.width() / 2.0f, (float) image.height() / 2.0f);
		radialPositionLayer.add(imageLayer);
	}
}
