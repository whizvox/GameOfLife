package me.whizvox.gameoflife;

import me.whizvox.gameoflife.input.InputManager;
import me.whizvox.gameoflife.window.Window;
import me.whizvox.gameoflife.render.simple.SimpleRenderer;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class GameOfLife {

  public static void main(String[] args) {

    Window window = new Window();
    window.create(600, 480);
    GL.createCapabilities();
    InputManager inputManager = new InputManager(window);
    SimpleRenderer renderer = new SimpleRenderer();
    window.setResizeCallback((width, height) -> {
      glViewport(0, 0, width, height);
      renderer.resize(width, height);
    });
    boolean showGrid = false;
    renderer.resize(600, 480);

    while (!window.shouldClose()) {
      glClearColor(1, 0, 0, 1);
      glClear(GL_COLOR_BUFFER_BIT);

      float moveAmount = 0.05f;
      if (inputManager.isKeyHeld(GLFW_KEY_LEFT)) {
        renderer.getCamera().getView().translate(moveAmount, 0, 0);
      }
      if (inputManager.isKeyHeld(GLFW_KEY_RIGHT)) {
        renderer.getCamera().getView().translate(-moveAmount, 0, 0);
      }
      if (inputManager.isKeyHeld(GLFW_KEY_DOWN)) {
        renderer.getCamera().getView().translate(0, moveAmount, 0);
      }
      if (inputManager.isKeyHeld(GLFW_KEY_UP)) {
        renderer.getCamera().getView().translate(0, -moveAmount, 0);
      }
      if (inputManager.isKeyJustPressed(GLFW_KEY_G)) {
        showGrid = !showGrid;
      }
      if (inputManager.hasScrolled()) {
        float amount = (float) inputManager.getScrollDelta().y * -0.1f;
        if (inputManager.isKeyHeld(GLFW_KEY_LEFT_SHIFT) || inputManager.isKeyHeld(GLFW_KEY_RIGHT_SHIFT)) {
          amount *= 10;
        }
        renderer.getCamera().changeZoom(amount);
      }
      renderer.updateCamera();
      if (inputManager.hasMouseMoved()) {
        Vector3d mousePos = new Vector3d(inputManager.getMousePosition(), 0);
        Vector2i windowSize = window.getSize();
        renderer.getCamera().unproject(mousePos, windowSize.x, windowSize.y);
        renderer.rect((float) mousePos.x - 0.1f, (float) mousePos.y - 0.1f, 0.2f, 0.2f);
      }
      renderer.rect(-0.5f, -0.5f, 1, 1);
      renderer.rect(-1, -1, 0.2f, 0.2f);
      renderer.getMesh().setDrawingMode(GL_TRIANGLES);
      renderer.render();
      renderer.getMesh().reset();

      if (showGrid) {
        renderer.drawGrid(-100, 100, -100, 100);
        renderer.getMesh().setDrawingMode(GL_LINES);
        renderer.render();
        renderer.getMesh().reset();
      }

      glfwSwapBuffers(window.getHandle());
      inputManager.update();
      glfwPollEvents();
    }

    renderer.dispose();
    window.dispose();

  }

}