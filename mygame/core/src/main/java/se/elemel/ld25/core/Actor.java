package se.elemel.ld25.core;

public interface Actor {
	public void init();
	public void exit();
	public void paint(float alpha);
	public void update(float delta);
}
