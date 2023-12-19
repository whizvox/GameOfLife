package me.whizvox.gameoflife.window;

import me.whizvox.gameoflife.Resource;
import me.whizvox.gameoflife.window.WindowResizeCallback;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Resource {

  private long handle;
  private final Vector2i size;
  private WindowResizeCallback windowResizeCallback;

  public Window() {
    handle = NULL;
    size = new Vector2i();
    windowResizeCallback = WindowResizeCallback.NO_OP;
  }

  public long getHandle() {
    return handle;
  }

  public boolean isCreated() {
    return handle != NULL;
  }

  public boolean shouldClose() {
    return glfwWindowShouldClose(handle);
  }

  public void create(int width, int height) {
    if (isCreated()) {
      throw new IllegalStateException("Window is already created");
    }
    GLFWErrorCallback.createPrint(System.err).set();
    if (!glfwInit()) {
      throw new IllegalStateException("Could initialize GLFW");
    }

    glfwDefaultWindowHints();
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

    handle = glfwCreateWindow(width, height, "Game of Life", NULL, NULL);
    if (handle == NULL) {
      throw new IllegalStateException("Could not create window");
    }
    size.set(width, height);

    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer widthPtr = stack.mallocInt(1);
      IntBuffer heightPtr = stack.mallocInt(1);
      glfwGetWindowSize(handle, widthPtr, heightPtr);
      GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
      glfwSetWindowPos(handle, (vidMode.width() - widthPtr.get(0)) / 2, (vidMode.height() - heightPtr.get(0)) / 2);
    }

    glfwSetWindowSizeCallback(handle, (win, newWidth, newHeight) -> {
      size.set(newWidth, newHeight);
      windowResizeCallback.onResize(newWidth, newHeight);
    });

    glfwMakeContextCurrent(handle);
    glfwSwapInterval(1);
    glfwShowWindow(handle);
  }

  public Vector2i getSize() {
    return new Vector2i(size);
  }

  public void setResizeCallback(WindowResizeCallback callback) {
    windowResizeCallback = callback;
  }

  @Override
  public void dispose() {
    if (isCreated() && !glfwWindowShouldClose(handle)) {
      glfwDestroyWindow(handle);
      handle = NULL;
    }
  }

}
