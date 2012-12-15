package se.elemel.ld25.html;

import playn.core.PlayN;
import playn.html.HtmlGame;
import playn.html.HtmlPlatform;

import se.elemel.ld25.core.Game;

public class GameHtml extends HtmlGame {

  @Override
  public void start() {
    HtmlPlatform platform = HtmlPlatform.register();
    platform.assets().setPathPrefix("game/");
    PlayN.run(new Game());
  }
}
