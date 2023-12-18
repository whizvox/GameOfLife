package me.whizvox.gameoflife;

import org.lwjgl.opengl.GL;

public class GameOfLife {

  public static void main(String[] args) {

    Window window = new Window();
    window.create();
    GL.createCapabilities();
    Mesh mesh = new Mesh();
    mesh.create();
    window.loop(() -> {
      MeshHelper.rect(mesh, -0.5f, -0.5f, 1, 1);
      MeshHelper.rect(mesh, -1, -1, 0.2f, 0.2f);
      mesh.flush();
    });
    mesh.dispose();
    window.dispose();

  }

}