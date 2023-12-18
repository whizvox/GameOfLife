package me.whizvox.gameoflife.render;

import org.joml.Matrix4f;

public class Camera {

  private Matrix4f view;
  private Matrix4f projection;
  private Matrix4f combined;

  private float zoom;

  public Camera() {
    view = new Matrix4f();
    projection = new Matrix4f();
    combined = new Matrix4f();
    zoom = 1.0f;
  }

  public Matrix4f getView() {
    return view;
  }

  public Matrix4f getProjection() {
    return projection;
  }

  public Matrix4f getCombined() {
    return combined;
  }

  public float getZoom() {
    return zoom;
  }

  public void setZoom(float zoom) {
    this.zoom = zoom;
  }

  public void changeZoom(float amount) {
    zoom += amount;
  }

  public void update() {
    view.mul(projection, combined);
    combined.scale(zoom);
  }

}
