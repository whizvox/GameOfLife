package me.whizvox.gameoflife;

import me.whizvox.gameoflife.render.Camera;
import me.whizvox.gameoflife.render.mesh.Mesh;
import me.whizvox.gameoflife.render.mesh.MeshHelper;
import me.whizvox.gameoflife.render.Window;
import me.whizvox.gameoflife.render.shader.SimpleShaderProgram;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class GameOfLife {

  public static void main(String[] args) {

    Window window = new Window();
    window.create();
    GL.createCapabilities();
    Camera camera = new Camera();
    SimpleShaderProgram shaderProgram = new SimpleShaderProgram();
    shaderProgram.create();
    Mesh mesh = new Mesh();
    mesh.create();
    glfwSetWindowSizeCallback(window.getHandle(), (win, width, height) -> {
      glViewport(0, 0, width, height);
      float aspectRatio = (float) width / height;
      camera.getProjection().identity();
      camera.getProjection().ortho2D(-1 * aspectRatio, aspectRatio, -1, 1);
      camera.update();
      shaderProgram.updateTransform(camera.getCombined());
    });
    float aspectRatio = 600.0f / 480.0f;
    camera.getProjection().identity();
    camera.getProjection().ortho2D(-1 * aspectRatio, aspectRatio, -1, 1);
    camera.update();
    shaderProgram.updateTransform(camera.getCombined());
    window.loop(() -> {
      float moveAmount = 0.05f;
      if (glfwGetKey(window.getHandle(), GLFW_KEY_LEFT) == GLFW_PRESS) {
        camera.getView().translate(-moveAmount, 0, 0);
      }
      if (glfwGetKey(window.getHandle(), GLFW_KEY_RIGHT) == GLFW_PRESS) {
        camera.getView().translate(moveAmount, 0, 0);
      }
      if (glfwGetKey(window.getHandle(), GLFW_KEY_DOWN) == GLFW_PRESS) {
        camera.getView().translate(0, -moveAmount, 0);
      }
      if (glfwGetKey(window.getHandle(), GLFW_KEY_UP) == GLFW_PRESS) {
        camera.getView().translate(0, moveAmount, 0);
      }
      camera.update();
      shaderProgram.updateTransform(camera.getCombined());
      MeshHelper.rect(mesh, -0.5f, -0.5f, 1, 1);
      MeshHelper.rect(mesh, -1, -1, 0.2f, 0.2f);
      shaderProgram.use();
      mesh.flush();
    });

    shaderProgram.dispose();
    mesh.dispose();
    window.dispose();

  }

}