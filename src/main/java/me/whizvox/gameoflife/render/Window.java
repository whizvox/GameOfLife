package me.whizvox.gameoflife.render;

import me.whizvox.gameoflife.Resource;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Resource {

  private long handle;

  public Window() {
    handle = NULL;
  }

  public long getHandle() {
    return handle;
  }

  public boolean isCreated() {
    return handle != NULL;
  }

  public void create() {
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

    handle = glfwCreateWindow(600, 480, "Game of Life", NULL, NULL);
    if (handle == NULL) {
      throw new IllegalStateException("Could not create window");
    }

    glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> {
      if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
        glfwSetWindowShouldClose(handle, true);
      }
    });

    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer widthPtr = stack.mallocInt(1);
      IntBuffer heightPtr = stack.mallocInt(1);
      glfwGetWindowSize(handle, widthPtr, heightPtr);
      GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
      glfwSetWindowPos(handle, (vidMode.width() - widthPtr.get(0)) / 2, (vidMode.height() - heightPtr.get(0)) / 2);
    }

    glfwMakeContextCurrent(handle);
    glfwSwapInterval(1);
    glfwShowWindow(handle);
  }

  public void loop(Runnable onRender) {
    while (!glfwWindowShouldClose(handle)) {
      glClearColor(1, 0, 0, 1);
      glClear(GL_COLOR_BUFFER_BIT);

      onRender.run();

      glfwSwapBuffers(handle);
      glfwPollEvents();
    }
  }

  @Override
  public void dispose() {
    if (isCreated() && !glfwWindowShouldClose(handle)) {
      glfwDestroyWindow(handle);
      handle = NULL;
    }
  }

}
