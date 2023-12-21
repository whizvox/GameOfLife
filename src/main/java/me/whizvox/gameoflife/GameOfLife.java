package me.whizvox.gameoflife;

import me.whizvox.gameoflife.input.InputManager;
import me.whizvox.gameoflife.render.mesh.Mesh;
import me.whizvox.gameoflife.render.simple.SimpleRenderer;
import me.whizvox.gameoflife.simulation.World;
import me.whizvox.gameoflife.window.Window;
import org.joml.*;
import org.lwjgl.opengl.GL;

import java.util.Timer;
import java.util.TimerTask;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class GameOfLife {

  private static final int[] TICK_RATES = new int[] { 1000, 881, 762, 643, 525, 406, 287, 168, 50 };

  private static TimerTask createWorldTickTask(World world) {
    return new TimerTask() {
      @Override
      public void run() {
        world.tick();
      }
    };
  }

  private static Vector2f getWorldPosition(Window window, SimpleRenderer renderer, Vector2d mousePos) {
    Vector3d mousePos3 = new Vector3d(mousePos, 0);
    Vector2i windowSize = window.getSize();
    renderer.getCamera().unproject(mousePos3, windowSize.x, windowSize.y);
    return new Vector2f((float) mousePos3.x, (float) mousePos3.y);
  }

  public static void main(String[] args) {

    Window window = new Window();
    window.create(600, 480);
    GL.createCapabilities();
    Mesh mesh = new Mesh(10000, 10000);
    mesh.create();
    InputManager inputManager = new InputManager(window);
    SimpleRenderer renderer = new SimpleRenderer();
    window.setResizeCallback((width, height) -> {
      glViewport(0, 0, width, height);
      renderer.resize(width, height);
    });
    boolean showGrid = false;
    renderer.resize(600, 480);

    World world = new World();
    boolean runWorld = false;
    int worldTickRate = TICK_RATES[4];

    Timer worldTimer = new Timer();
    TimerTask worldTickTask = null;
    boolean rescheduleTickTask = false;

    while (!window.shouldClose()) {
      glClearColor(1, 0, 0, 1);
      glClear(GL_COLOR_BUFFER_BIT);

      if (inputManager.isKeyJustPressed(GLFW_KEY_T)) {
        runWorld = !runWorld;
        rescheduleTickTask = true;
      }
      if (inputManager.isKeyJustPressed(GLFW_KEY_R)) {
        world.reset();
        runWorld = false;
        rescheduleTickTask = true;
      }
      if (inputManager.isKeyJustPressed(GLFW_KEY_O)) {
        renderer.getCamera().getView().setTranslation(new Vector3f());
        renderer.getCamera().setZoom(1, new Vector3f());
      }
      if (inputManager.isKeyJustPressed(GLFW_KEY_1)) {
        worldTickRate = TICK_RATES[0];
        rescheduleTickTask = true;
      }
      if (inputManager.isKeyJustPressed(GLFW_KEY_2)) {
        worldTickRate = TICK_RATES[1];
        rescheduleTickTask = true;
      }
      if (inputManager.isKeyJustPressed(GLFW_KEY_3)) {
        worldTickRate = TICK_RATES[2];
        rescheduleTickTask = true;
      }
      if (inputManager.isKeyJustPressed(GLFW_KEY_4)) {
        worldTickRate = TICK_RATES[3];
        rescheduleTickTask = true;
      }
      if (inputManager.isKeyJustPressed(GLFW_KEY_5)) {
        worldTickRate = TICK_RATES[4];
        rescheduleTickTask = true;
      }
      if (inputManager.isKeyJustPressed(GLFW_KEY_6)) {
        worldTickRate = TICK_RATES[5];
        rescheduleTickTask = true;
      }
      if (inputManager.isKeyJustPressed(GLFW_KEY_7)) {
        worldTickRate = TICK_RATES[6];
        rescheduleTickTask = true;
      }
      if (inputManager.isKeyJustPressed(GLFW_KEY_8)) {
        worldTickRate = TICK_RATES[7];
        rescheduleTickTask = true;
      }
      if (inputManager.isKeyJustPressed(GLFW_KEY_9)) {
        worldTickRate = TICK_RATES[8];
        rescheduleTickTask = true;
      }
      if (inputManager.isKeyJustPressed(GLFW_KEY_0)) {
        worldTickRate = 0;
        rescheduleTickTask = true;
      }
      if (rescheduleTickTask) {
        rescheduleTickTask = false;
        if (runWorld) {
          if (worldTickTask != null) {
            worldTickTask.cancel();
          }
          worldTickTask = createWorldTickTask(world);
          if (worldTickRate > 0) {
            worldTimer.schedule(worldTickTask, 0, worldTickRate);
          } else {
            worldTimer.schedule(worldTickTask, 0, 5);
          }
        } else {
          if (worldTickTask != null) {
            worldTickTask.cancel();
          }
        }
      }
      if (inputManager.isMouseButtonJustPressed(GLFW_MOUSE_BUTTON_1)) {
        Vector2f worldPos = getWorldPosition(window, renderer, inputManager.getMousePosition());
        int x, y;
        if (worldPos.x < 0) {
          x = (int) worldPos.x - 1;
        } else {
          x = (int) worldPos.x;
        }
        if (worldPos.y < 0) {
          y = (int) worldPos.y - 1;
        } else {
          y = (int) worldPos.y;
        }
        world.toggle(x, y);
      }

      float moveAmount = 0.05f * renderer.getCamera().getZoom();
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
        Vector2f mouseWorldPos = getWorldPosition(window, renderer, inputManager.getMousePosition());
        float amount = (float) inputManager.getScrollDelta().y * -0.3f;
        if (inputManager.isKeyHeld(GLFW_KEY_LEFT_SHIFT) || inputManager.isKeyHeld(GLFW_KEY_RIGHT_SHIFT)) {
          amount *= 10;
        }
        renderer.getCamera().changeZoom(amount, new Vector3f(mouseWorldPos, 0));
      }
      renderer.updateCamera();
      renderer.begin();
      world.forEachCell(pos -> {
        mesh.rect(pos.x(), pos.y(), 1, 1);
      });
      mesh.setDrawingMode(GL_TRIANGLES);
      mesh.flush();

      if (showGrid) {
        Vector2f corner1 = getWorldPosition(window, renderer, new Vector2d(0, 0));
        Vector2f corner2 = getWorldPosition(window, renderer, new Vector2d(window.getSize()));
        mesh.grid((int) corner1.x - 1, (int) corner2.x + 1, (int) corner2.y - 1, (int) corner1.y + 1);
        mesh.setDrawingMode(GL_LINES);
        mesh.flush();
      }
      renderer.end();

      glfwSwapBuffers(window.getHandle());
      inputManager.update();
      glfwPollEvents();
    }

    worldTimer.cancel();
    mesh.dispose();
    renderer.dispose();
    window.dispose();

  }

}