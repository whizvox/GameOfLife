package me.whizvox.gameoflife;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;

public class MeshHelper {

  public static void rect(Mesh mesh, float x, float y, float width, float height) {
    final float x2 = x + width;
    final float y2 = y + height;
    try (var stack = stackPush()) {
      FloatBuffer vertices = stack.mallocFloat(8);
      IntBuffer indices = stack.mallocInt(6);
      vertices.put(new float[] { x2, y2, x2, y, x, y, x, y2 });
      int off = mesh.getLastIndex();
      indices.put(new int[] { off, off + 1, off + 3, off + 1, off + 2, off + 3 });
      vertices.flip();
      indices.flip();
      mesh.addVerticesAndIndices(vertices, indices);
    }
  }

}
