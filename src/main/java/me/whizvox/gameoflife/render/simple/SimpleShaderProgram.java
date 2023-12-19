package me.whizvox.gameoflife.render.simple;

import me.whizvox.gameoflife.render.shader.ShaderProgram;
import me.whizvox.gameoflife.render.shader.ShaderUtils;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class SimpleShaderProgram extends ShaderProgram {

  private int transformUniform;

  private final FloatBuffer transformBuffer;

  public SimpleShaderProgram() {
    super("simple");
    transformUniform = 0;
    transformBuffer = MemoryUtil.memAllocFloat(16);
  }

  public void create() {
    super.create();
    use();
    transformUniform = glGetUniformLocation(program, "transform");
    ShaderUtils.unbind();
  }

  public void updateTransform(Matrix4f transform) {
    use();
    transformBuffer.clear();
    transform.get(transformBuffer);
    transformBuffer.position(16);
    transformBuffer.flip();
    glUniformMatrix4fv(transformUniform, false, transformBuffer);
    ShaderUtils.unbind();
  }

  @Override
  public void dispose() {
    super.dispose();
    MemoryUtil.memFree(transformBuffer);
  }

}
