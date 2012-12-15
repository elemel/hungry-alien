package se.elemel.ld25.android;

import playn.android.GameActivity;
import playn.core.PlayN;

import se.elemel.ld25.core.Game;

public class GameActivity extends GameActivity {

  @Override
  public void main(){
    platform().assets().setPathPrefix("se/elemel/ld25/resources");
    PlayN.run(new Game());
  }
}
