package me.whizvox.gameoflife.render.mesh;

import me.whizvox.gameoflife.Resource;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Mesh implements Resource {

  public static final int VERTEX_SIZE = 2;

  private int drawingMode;
  private int bufferUsage;

  private int vao;
  private int vertexBuffer;
  private int indexBuffer;

  private final int vertexBufferCapacity;
  private int vertexBufferSize;
  private final int indexBufferCapacity;
  private int indexBufferSize;

  public Mesh(int vertexBufferCapacity, int indexBufferCapacity) {
    drawingMode = GL_TRIANGLES;
    bufferUsage = GL_DYNAMIC_DRAW;

    vao = 0;
    vertexBuffer = 0;
    indexBuffer = 0;

    this.vertexBufferCapacity = vertexBufferCapacity;
    vertexBufferSize = 0;
    this.indexBufferCapacity = indexBufferCapacity;
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

  public void setDrawingMode(int drawingMode) {
    this.drawingMode = drawingMode;
  }

  public void setBufferUsage(int bufferUsage) {
    this.bufferUsage = bufferUsage;
  }

  public void uploadData(FloatBuffer vertices, IntBuffer indices, boolean applyIndexOffset) {
    checkVertexCount(vertices.remaining());
    checkIndexCount(indices.remaining());
    if (applyIndexOffset) {
      final int bufferOff = indices.position();
      final int indexOff = getLastIndex();
      for (int i = 0; i < indices.remaining(); i++) {
        indices.put(i + bufferOff, indices.get(i + bufferOff) + indexOff);
      }
    }
    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glBufferSubData(GL_ARRAY_BUFFER, vertexBufferSize * Float.BYTES, vertices);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
    glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, indexBufferSize * Integer.BYTES, indices);
    vertexBufferSize += vertices.remaining();
    indexBufferSize += indices.remaining();
  }

  public void uploadData(FloatBuffer vertices, IntBuffer indices) {
    uploadData(vertices, indices, true);
  }

  public void uploadData(float[] vertices, int[] indices, boolean applyIndexOffset) {
    checkVertexCount(vertices.length);
    checkIndexCount(indices.length);
    if (applyIndexOffset) {
      final int indexOff = getLastIndex();
      for (int i = 0; i < indices.length; i++) {
        indices[i] += indexOff;
      }
    }
    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glBufferSubData(GL_ARRAY_BUFFER, vertexBufferSize * Float.BYTES, vertices);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
    glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, indexBufferSize * Integer.BYTES, indices);
    vertexBufferSize += vertices.length;
    indexBufferSize += indices.length;
  }

  public void uploadData(float[] vertices, int[] indices) {
    uploadData(vertices, indices, true);
  }

  /**
   * Requires drawing mode of {@link org.lwjgl.opengl.GL11#GL_LINES}
   */
  public void line(float x1, float y1, float x2, float y2) {
    float[] vertices = new float[] { x1, y1, x2, y2 };
    int[] indices = new int[] { 0, 1 };
    uploadData(vertices, indices, true);
  }

  /**
   * Requires drawing mode of {@link org.lwjgl.opengl.GL11#GL_LINES}
   */
  public void grid(int left, int right, int bottom, int top) {
    for (int x = left; x <= right; x++) {
      line(x, bottom, x, top);
    }
    for (int y = bottom; y <= top; y++) {
      line(left, y, right, y);
    }
  }

  /**
   * Requires drawing mode of {@link org.lwjgl.opengl.GL11#GL_TRIANGLES}
   */
  public void quad(float x1, float y1, float x2, float y2) {
    float[] vertices = new float[] {
        x2, y2,
        x2, y1,
        x1, y1,
        x1, y2
    };
    int[] indices = new int[] { 0, 1, 3, 1, 2, 3 };
    uploadData(vertices, indices, true);
  }

  /**
   * Requires drawing mode of {@link org.lwjgl.opengl.GL11#GL_TRIANGLES}
   */
  public void rect(float x, float y, float width, float height) {
    quad(x, y, x + width, y + height);
  }

  public void create() {
    vao = glGenVertexArrays();
    vertexBuffer = glGenBuffers();
    indexBuffer = glGenBuffers();

    glBindVertexArray(vao);
    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glBufferData(GL_ARRAY_BUFFER, vertexBufferCapacity, bufferUsage);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBufferCapacity, bufferUsage);
    glVertexAttribPointer(0, VERTEX_SIZE, GL_FLOAT, false, Float.BYTES * VERTEX_SIZE, NULL);
    glEnableVertexAttribArray(0);
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
      glBindVertexArray(vao);
      glDrawElements(drawingMode, indexBufferSize, GL_UNSIGNED_INT, 0);
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
  }

}
