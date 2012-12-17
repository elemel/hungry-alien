package se.elemel.ld25.core;

import java.util.*;
import playn.core.*;

public class ResourceCache {
	private HashMap<String, Image> images = new HashMap<String, Image>();
	private HashMap<String, Sound> sounds = new HashMap<String, Sound>();

	public Image getImage(String name) {
		Image image = images.get(name);
		if (image == null) {
			image = PlayN.assets().getImage("images/" + name + ".png");
			images.put(name, image);
		}
		return image;
	}

	public Sound getSound(String name) {
		Sound sound = sounds.get(name);
		if (sound == null) {
			sound = PlayN.assets().getSound("sounds/" + name);
			sounds.put(name, sound);
		}
		return sound;
	}
}
