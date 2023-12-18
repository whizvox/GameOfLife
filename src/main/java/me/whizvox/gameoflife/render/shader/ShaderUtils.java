package me.whizvox.gameoflife.render.shader;

import me.whizvox.gameoflife.IOUtils;
import org.lwjgl.opengl.GL20;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class ShaderUtils {

  public static int createProgram(String vertexShaderPath, String fragmentShaderPath) {
    int vertexShader = glCreateShader(GL_VERTEX_SHADER);
    try {
      GL20.glShaderSource(vertexShader, IOUtils.readResourceAsString(vertexShaderPath));
    } catch (IOException e) {
      throw new RuntimeException("Could not read vertex shader", e);
    }
    glCompileShader(vertexShader);
    try (var stack = stackPush()) {
      var statusPtr = stack.mallocInt(1);
      glGetShaderiv(vertexShader, GL_COMPILE_STATUS, statusPtr);
      if (statusPtr.get(0) != GL_TRUE) {
        var e = new IllegalStateException("Could not compile vertex shader: " + glGetShaderInfoLog(vertexShader));
        glDeleteShader(vertexShader);
        throw e;
      }
    }
    int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
    try {
      glShaderSource(fragmentShader, IOUtils.readResourceAsString(fragmentShaderPath));
    } catch (IOException e) {
      throw new RuntimeException("Could not read fragment shader", e);
    }
    glCompileShader(fragmentShader);
    try (var stack = stackPush()) {
      var statusPtr = stack.mallocInt(1);
      glGetShaderiv(fragmentShader, GL_COMPILE_STATUS, statusPtr);
      if (statusPtr.get(0) != GL_TRUE) {
        glDeleteShader(vertexShader);
        var e = new IllegalStateException("Could not compile fragment shader: " + glGetShaderInfoLog(fragmentShader));
        glDeleteShader(fragmentShader);
        throw e;
      }
    }

    int shaderProgram = glCreateProgram();
    glAttachShader(shaderProgram, vertexShader);
    glAttachShader(shaderProgram, fragmentShader);
    glLinkProgram(shaderProgram);
    glDeleteShader(vertexShader);
    glDeleteShader(fragmentShader);
    try (var stack = stackPush()) {
      var statusPtr = stack.mallocInt(1);
      glGetProgramiv(shaderProgram, GL_LINK_STATUS, statusPtr);
      if (statusPtr.get(0) != GL_TRUE) {
        var e = new IllegalStateException("Could not link program: " + glGetProgramInfoLog(shaderProgram));
        glDeleteProgram(shaderProgram);
        throw e;
      }
    }

    return shaderProgram;
  }

  public static void unbind() {
    glUseProgram(0);
  }

}
