package me.whizvox.gameoflife.window;

public interface WindowResizeCallback {

  void onResize(int width, int height);

  WindowResizeCallback NO_OP = (width, height) -> {};

}
