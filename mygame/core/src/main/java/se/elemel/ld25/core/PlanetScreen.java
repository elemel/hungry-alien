package se.elemel.ld25.core;

import java.util.ArrayList;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Gradient;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Key;
import playn.core.Keyboard;
import playn.core.PlayN;
import playn.core.Surface;
import playn.core.SurfaceLayer;

public class PlanetScreen implements Screen {
	private MyGame game;
	private ResourceCache resourceCache;
	private GameState state;

	private SurfaceLayer atmosphereSurfaceLayer;
	private CanvasImage atmosphereImage;
	private SurfaceLayer planetSurfaceLayer;
	private CanvasImage planetImage;
	private Image treeImage;
	private ArrayList<ImageLayer> treeImageLayers = new ArrayList<ImageLayer>();

	public PlanetScreen(MyGame game) {
		this.game = game;
		this.resourceCache = game.getResourceCache();
	}
	
	@Override
	public void init() {
		state = new GameState(resourceCache);
		state.init();

		treeImage = resourceCache.getImage("tree");

		int width = PlayN.graphics().width();
	    int height = PlayN.graphics().height();

	    atmosphereSurfaceLayer = PlayN.graphics().createSurfaceLayer(width, height);
	    state.getAtmosphereLayer().add(atmosphereSurfaceLayer);
	    planetSurfaceLayer = PlayN.graphics().createSurfaceLayer(width, height);
	    state.getPlanetLayer().add(planetSurfaceLayer);

		state.addActor(new AlienShipActor(state, 0.0f));
		
		int treeCount = 10;
		for (int i = 0; i < treeCount; ++i) {
			float polarPosition = 2.0f * (float) Math.PI * (float) i / (float) treeCount +
					2.0f * (float) Math.PI * (PlayN.random() - 0.5f) / (float) treeCount;
			createTree(polarPosition);
		}
		
		int animalCount = 20;
		for (int i = 0; i < animalCount; ++i) {
			float polarPosition = 2.0f * (float) Math.PI * (float) i / (float) animalCount +
					2.0f * (float) Math.PI * (PlayN.random() - 0.5f) / (float) animalCount;
			createAnimal(polarPosition);
		}

		int vehicleCount = 10;
		for (int i = 0; i < vehicleCount; ++i) {
			float polarPosition = (float) Math.PI / 4.0f + 3.0f / 2.0f * (float) Math.PI * (float) i / (float) vehicleCount +
					(float) Math.PI * (PlayN.random() - 0.5f) / (float) vehicleCount;
			state.addActor(new VehicleActor(state, polarPosition));
		}
		
	    float planetRadiusInPixels = state.getPlanetRadius() * state.getPixelsPerMeter() + 0.5f;
	    planetImage = PlayN.graphics().createImage(width, height / 4);
	    Canvas planetCanvas = planetImage.canvas();
	    planetCanvas.setFillColor(0xff33cc00);
	    planetCanvas.fillCircle(0.5f * (float) width, planetRadiusInPixels, planetRadiusInPixels);		

	    PlayN.keyboard().setListener(new Keyboard.Adapter() {
	    	@Override
	    	public void onKeyDown(Keyboard.Event event) {
	    		if (event.key() == Key.BACKSPACE || event.key() == Key.ESCAPE) {
	    			game.setScreen(new TitleScreen(game));
	    			return;
	    		}

	    		state.setKeyPressed(event.key(), true);
	    	}

	    	@Override
	    	public void onKeyUp(Keyboard.Event event) {
	    		state.setKeyPressed(event.key(), false);
	    	}
	    });

	    atmosphereImage = PlayN.graphics().createImage(width, height);
	    Canvas atmosphereCanvas = atmosphereImage.canvas();
	    float atmosphereRadialOffset = 40.0f;
	    float atmosphereRadius = state.getPlanetRadius() + atmosphereRadialOffset;
	    float atmosphereRadiusInPixels = atmosphereRadius * state.getPixelsPerMeter();
	    atmosphereCanvas.setFillColor(0xff000000);
	    atmosphereCanvas.fillRect(0, 0, width, height);

	    int[] colors = { 0xff00ccff, 0x00000000 };
	    float[] colorPositions = { 1.0f - atmosphereRadialOffset / atmosphereRadius, 1.0f };
	    Gradient gradient = PlayN.graphics().createRadialGradient(0.5f * (float) width,
	    		0.75f * (float) height + planetRadiusInPixels, atmosphereRadiusInPixels, colors, colorPositions);
	    atmosphereCanvas.setFillGradient(gradient);
	    atmosphereCanvas.fillRect(0, 0, width, height);
	    atmosphereCanvas.setFillGradient(null);
	}

	@Override
	public void exit() {
		PlayN.keyboard().setListener(null);
		state.exit();
	}
	
	@Override
	public void paint(float alpha) {
		for (ImageLayer treeImageLayer : treeImageLayers) {
			treeImageLayer.setOrigin(0.5f * (float) treeImage.width(), 0.5f * (float) treeImage.height());
		}
		
	    int height = PlayN.graphics().height();

	    Surface atmosphereSurface = atmosphereSurfaceLayer.surface();
	    atmosphereSurface.clear();
	    atmosphereSurface.drawImage(atmosphereImage, 0, 0);

	    Surface planetSurface = planetSurfaceLayer.surface();
	    planetSurface.clear();
	    planetSurface.drawImage(planetImage, 0, height - planetImage.height());

		state.paint(alpha);
	}

	@Override
	public void update(float delta) {
		state.update(delta);
		if (state.getAlienShipActor() == null) {
			game.setScreen(new TitleScreen(game));
		}
	}

	private void createTree(float polarPosition) {
		GroupLayer polarPositionLayer = PlayN.graphics().createGroupLayer();
		polarPositionLayer.setRotation(polarPosition);
		state.getVegetationLayer().add(polarPositionLayer);

		GroupLayer radialPositionLayer = PlayN.graphics().createGroupLayer();
		radialPositionLayer.setTranslation(0.0f, -(state.getPlanetRadius() + 3.0f) * state.getPixelsPerMeter());
		polarPositionLayer.add(radialPositionLayer);

		ImageLayer imageLayer = PlayN.graphics().createImageLayer(treeImage);
		treeImageLayers.add(imageLayer);
		float scale = state.getPixelsPerMeter() * 0.1f;
		float scaleX = (PlayN.random() < 0.5f ? -1.0f : 1.0f) * scale;
		float scaleY = scale;
		imageLayer.setScale(scaleX, scaleY);
		radialPositionLayer.add(imageLayer);
	}

	private void createAnimal(float polarPosition) {
		AnimalActor.Type type = generateAnimalType();
		state.addActor(new AnimalActor(state, polarPosition, type));
	}

	private AnimalActor.Type generateAnimalType() {
		if (PlayN.random() < 0.5f) {
			return PlayN.random() < 0.5f ? AnimalActor.Type.COW : AnimalActor.Type.SHEEP;
		} else {
			return PlayN.random() < 0.5f ? AnimalActor.Type.HORSE : AnimalActor.Type.PIG;
		}
	}
}
