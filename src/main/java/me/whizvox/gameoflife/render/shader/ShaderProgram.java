package me.whizvox.gameoflife.render.shader;

import me.whizvox.gameoflife.Resource;

import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20C.glUseProgram;

public class ShaderProgram implements Resource {

  private final String baseFileName;
  protected int program;

  public ShaderProgram(String baseFileName) {
    this.baseFileName = baseFileName;
    program = 0;
  }

  public int getProgram() {
    return program;
  }

  public void create() {
    program = ShaderUtils.createProgram("shaders/" + baseFileName + ".vert", "shaders/" + baseFileName + ".frag");
  }

  public void use() {
    glUseProgram(program);
  }

  @Override
  public void dispose() {
    if (program != 0) {
      glDeleteProgram(program);
      program = 0;
    }
  }

}
