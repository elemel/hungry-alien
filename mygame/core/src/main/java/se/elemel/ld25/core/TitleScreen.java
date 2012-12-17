package se.elemel.ld25.core;

import playn.core.*;

public class TitleScreen implements Screen {
	private MyGame game;

	private ImageLayer imageLayer;

	public TitleScreen(MyGame game) {
		this.game = game;
	}

	@Override
	public void init() {
		Image image = PlayN.assets().getImage("images/title.png");
		imageLayer = PlayN.graphics().createImageLayer(image);
		PlayN.graphics().rootLayer().add(imageLayer);

		PlayN.keyboard().setListener(new Keyboard.Adapter() {
	    	@Override
	    	public void onKeyDown(Keyboard.Event event) {
    			game.setScreen(new PlanetScreen(game));
	    	}
	    });
	}

	@Override
	public void exit() {
		PlayN.keyboard().setListener(null);
		PlayN.graphics().rootLayer().remove(imageLayer);
	}
	
	@Override
	public void paint(float alpha) {
	}

	@Override
	public void update(float delta) {
	}
}
