package me.whizvox.gameoflife.render.simple;

import me.whizvox.gameoflife.Resource;
import me.whizvox.gameoflife.render.Camera;
import me.whizvox.gameoflife.render.shader.ShaderUtils;

public class SimpleRenderer implements Resource {

  private final SimpleShaderProgram shaderProgram;
  private final Camera camera;

  public SimpleRenderer() {
    shaderProgram = new SimpleShaderProgram();
    shaderProgram.create();
    camera = new Camera();
  }

  public SimpleShaderProgram getShaderProgram() {
    return shaderProgram;
  }

  public Camera getCamera() {
    return camera;
  }

  public void updateCamera() {
    camera.update();
    shaderProgram.updateTransform(camera.getCombined());
  }

  public void resize(int width, int height) {
    float aspectRatio = (float) width / height;
    camera.getProjection().identity();
    camera.getProjection().ortho2D(-1 * aspectRatio, aspectRatio, -1, 1);
    camera.update();
    shaderProgram.updateTransform(camera.getCombined());
  }

  public void begin() {
    shaderProgram.use();
  }

  public void end() {
    ShaderUtils.unbind();
  }

  @Override
  public void dispose() {
    shaderProgram.dispose();
  }

}
