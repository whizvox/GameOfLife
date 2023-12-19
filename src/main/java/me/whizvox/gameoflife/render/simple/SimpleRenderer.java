package me.whizvox.gameoflife.render.simple;

import me.whizvox.gameoflife.Resource;
import me.whizvox.gameoflife.render.Camera;
import me.whizvox.gameoflife.render.mesh.Mesh;

public class SimpleRenderer implements Resource {

  private final SimpleShaderProgram shaderProgram;
  private final Mesh mesh;
  private final Camera camera;

  public SimpleRenderer() {
    shaderProgram = new SimpleShaderProgram();
    shaderProgram.create();
    mesh = new Mesh(10000, 10000);
    mesh.create();
    camera = new Camera();
  }

  public SimpleShaderProgram getShaderProgram() {
    return shaderProgram;
  }

  public Mesh getMesh() {
    return mesh;
  }

  public Camera getCamera() {
    return camera;
  }

  public void updateCamera() {
    camera.update();
    shaderProgram.updateTransform(camera.getCombined());
  }

  public void data(float[] vertices, int[] indices) {
    mesh.addVerticesAndIndices(vertices, indices, true);
  }

  public void rect(float x, float y, float width, float height) {
    quad(x, y, x + width, y + height);
  }

  public void quad(float x1, float y1, float x2, float y2) {
    float[] vertices = new float[] {
        x2, y2,
        x2, y1,
        x1, y1,
        x1, y2
    };
    int[] indices = new int[] { 0, 1, 3, 1, 2, 3 };
    data(vertices, indices);
  }

  public void line(float x1, float y1, float x2, float y2) {
    float[] vertices = new float[] { x1, y1, x2, y2 };
    int[] indices = new int[] { 0, 1 };
    data(vertices, indices);
  }

  public void drawGrid(int left, int right, int bottom, int top) {
    for (int x = left; x <= right; x++) {
      line(x, bottom, x, top);
    }
    for (int y = bottom; y <= top; y++) {
      line(left, y, right, y);
    }
  }

  public void resize(int width, int height) {
    float aspectRatio = (float) width / height;
    camera.getProjection().identity();
    camera.getProjection().ortho2D(-1 * aspectRatio, aspectRatio, -1, 1);
    camera.update();
    shaderProgram.updateTransform(camera.getCombined());
  }

  public void render() {
    shaderProgram.use();
    mesh.render();
  }

  @Override
  public void dispose() {
    shaderProgram.dispose();
    mesh.dispose();
  }

}
