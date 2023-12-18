package me.whizvox.gameoflife;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Mesh implements Resource {

  public static final int VERTEX_SIZE = 2;

  private int shaderProgram;
  private int vao;
  private int vertexBuffer;
  private int indexBuffer;

  private int vertexBufferCapacity;
  private int vertexBufferSize;
  private int indexBufferCapacity;
  private int indexBufferSize;

  public Mesh() {
    shaderProgram = 0;
    vao = 0;
    vertexBuffer = 0;
    indexBuffer = 0;

    vertexBufferCapacity = 1000;
    vertexBufferSize = 0;
    indexBufferCapacity = 1000;
    indexBufferSize = 0;
  }

  private void checkVertexCount(int size) {
    if (size > vertexBufferCapacity) {
      throw new IllegalArgumentException("Vertex buffer not large enough");
    }
    if (vertexBufferSize + size > vertexBufferCapacity) {
      flush();
    }
  }

  private void checkIndexCount(int size) {
    if (size > indexBufferCapacity) {
      throw new IllegalArgumentException("Index buffer not large enough");
    }
    if (indexBufferSize + size > indexBufferCapacity) {
      flush();
    }
  }

  public int getVertexBufferSize() {
    return vertexBufferSize;
  }

  public int getIndexBufferSize() {
    return indexBufferSize;
  }

  public int getLastIndex() {
    return vertexBufferSize / VERTEX_SIZE;
  }

  public void addVertices(FloatBuffer vertices) {
    checkVertexCount(vertices.remaining());
    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glBufferSubData(GL_ARRAY_BUFFER, vertexBufferSize, vertices);
    vertexBufferSize += vertices.remaining();
  }

  public void addIndices(IntBuffer indices) {
    checkIndexCount(indices.remaining());
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
    glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, indexBufferSize, indices);
    indexBufferSize += indices.remaining();
  }

  public void addVerticesAndIndices(FloatBuffer vertices, IntBuffer indices) {
    checkVertexCount(vertices.remaining());
    checkIndexCount(indices.remaining());
    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glBufferSubData(GL_ARRAY_BUFFER, vertexBufferSize * Float.BYTES, vertices);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
    glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, indexBufferSize * Integer.BYTES, indices);
    vertexBufferSize += vertices.remaining();
    indexBufferSize += indices.remaining();
  }

  public void create() {
    int vertexShader = glCreateShader(GL_VERTEX_SHADER);
    try {
      glShaderSource(vertexShader, IOUtils.readResourceAsString("shaders/simple.vert"));
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
      glShaderSource(fragmentShader, IOUtils.readResourceAsString("shaders/simple.frag"));
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
    shaderProgram = glCreateProgram();
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
        shaderProgram = 0;
        throw e;
      }
    }

    vao = glGenVertexArrays();
    vertexBuffer = glGenBuffers();
    indexBuffer = glGenBuffers();

    glBindVertexArray(vao);
    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glBufferData(GL_ARRAY_BUFFER, vertexBufferCapacity, GL_DYNAMIC_DRAW);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBufferCapacity, GL_DYNAMIC_DRAW);
    glVertexAttribPointer(0, VERTEX_SIZE, GL_FLOAT, false, Float.BYTES * VERTEX_SIZE, NULL);
    glEnableVertexAttribArray(0);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindVertexArray(0);
  }

  public void flush() {
    render();
    reset();
  }

  public void reset() {
    vertexBufferSize = 0;
    indexBufferSize = 0;
  }

  public void render() {
    if (indexBufferSize > 0) {
      glUseProgram(shaderProgram);
      glBindVertexArray(vao);
      glDrawElements(GL_TRIANGLES, indexBufferSize, GL_UNSIGNED_INT, 0);
      glBindVertexArray(0);
    }
  }

  @Override
  public void dispose() {
    if (vao != 0) {
      glDeleteVertexArrays(vao);
      vao = 0;
    }
    if (vertexBuffer != 0) {
      glDeleteBuffers(vertexBuffer);
      vertexBuffer = 0;
    }
    if (shaderProgram != 0) {
      glDeleteProgram(shaderProgram);
      shaderProgram = 0;
    }
  }

}
