package me.whizvox.gameoflife.input;

import me.whizvox.gameoflife.window.Window;
import org.joml.Vector2d;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class InputManager {

  private final long window;

  private final Map<Integer, Long> heldKeys;
  private final Map<Integer, Long> heldMouseButtons;
  private final Set<Integer> justPressedKeys;
  private final Set<Integer> justPressedMouseButtons;

  private final Vector2d mousePos;
  private final Vector2d lastMousePos;
  private final Vector2d mouseDelta;

  private final Vector2d scrollDelta;

  public InputManager(Window window) {
    this.window = window.getHandle();
    heldKeys = new HashMap<>();
    heldMouseButtons = new HashMap<>();
    justPressedKeys = new HashSet<>();
    justPressedMouseButtons = new HashSet<>();

    mousePos = new Vector2d();
    lastMousePos = new Vector2d();
    mouseDelta = new Vector2d();
    scrollDelta = new Vector2d();

    glfwSetKeyCallback(this.window, (win, key, scancode, action, mods) -> {
      if (action == GLFW_PRESS) {
        heldKeys.put(key, System.currentTimeMillis());
        justPressedKeys.add(key);
      } else if (action == GLFW_RELEASE) {
        heldKeys.remove(key);
        justPressedKeys.remove(key);
      }
    });
    glfwSetMouseButtonCallback(this.window, (win, button, action, mods) -> {
      if (action == GLFW_PRESS) {
        heldMouseButtons.put(button, System.currentTimeMillis());
        justPressedMouseButtons.add(button);
      } else if (action == GLFW_RELEASE) {
        heldMouseButtons.remove(button);
        justPressedMouseButtons.remove(button);
      }
    });
    glfwSetCursorPosCallback(this.window, (win, xpos, ypos) -> {
      lastMousePos.set(mousePos);
      mousePos.set(xpos, ypos);
      mousePos.sub(lastMousePos, mouseDelta);
    });
    glfwSetScrollCallback(this.window, (win, xoffset, yoffset) -> {
      scrollDelta.set(xoffset, yoffset);
    });
  }

  public boolean isKeyHeld(int key) {
    return glfwGetKey(window, key) == GLFW_PRESS;
  }

  public boolean isKeyJustPressed(int key) {
    return justPressedKeys.contains(key);
  }

  public boolean isKeyJustReleased(int key) {
    return glfwGetKey(window, key) == GLFW_RELEASE;
  }

  public long getKeyHoldTime(int key) {
    Long start = heldKeys.get(key);
    if (start == null) {
      return -1L;
    }
    return System.currentTimeMillis() - start;
  }

  public boolean isMouseButtonHeld(int button) {
    return glfwGetMouseButton(window, button) == GLFW_PRESS;
  }

  public boolean isMouseButtonJustPressed(int button) {
    return justPressedMouseButtons.contains(button);
  }

  public boolean isMouseButtonJustReleased(int button) {
    return glfwGetMouseButton(window, button) == GLFW_RELEASE;
  }

  public long getMouseButtonHoldTime(int button) {
    Long start = heldMouseButtons.get(button);
    if (start == null) {
      return -1L;
    }
    return System.currentTimeMillis() - start;
  }

  public boolean hasMouseMoved() {
    return mouseDelta.x != 0 || mouseDelta.y != 0;
  }

  public Vector2d getMousePosition() {
    return new Vector2d(mousePos);
  }

  public Vector2d getLastMousePosition() {
    return new Vector2d(lastMousePos);
  }

  public Vector2d getMouseDelta() {
    return new Vector2d(mouseDelta);
  }

  public boolean hasScrolled() {
    return scrollDelta.x != 0 || scrollDelta.y != 0;
  }

  public Vector2d getScrollDelta() {
    return new Vector2d(scrollDelta);
  }

  public void update() {
    mouseDelta.zero();
    lastMousePos.set(mousePos);
    scrollDelta.zero();
    justPressedKeys.clear();
    justPressedMouseButtons.clear();
  }

}
