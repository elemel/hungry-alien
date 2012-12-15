package se.elemel.ld25.java;

import playn.core.PlayN;
import playn.java.JavaPlatform;

import se.elemel.ld25.core.Game;

public class GameJava {

  public static void main(String[] args) {
    JavaPlatform platform = JavaPlatform.register();
    platform.assets().setPathPrefix("se/elemel/ld25/resources");
    PlayN.run(new Game());
  }
}
