package me.whizvox.gameoflife.render;

import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class Camera {

  private Matrix4f view;
  private Matrix4f projection;
  private Matrix4f invCombined;
  private Matrix4f combined;

  private float zoom;

  public Camera() {
    view = new Matrix4f();
    projection = new Matrix4f();
    invCombined = new Matrix4f();
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

  public void setZoom(float newZoom, Vector3f pivot) {
    float oldZoom = zoom;
    zoom = newZoom;
    if (zoom < 0) {
      zoom = 0.0001f;
    }
    // Matrix4f#scaleAround doesn't do this in the order I want
    view.translate(pivot);
    view.scale(oldZoom / zoom);
    view.translate(-pivot.x, -pivot.y, -pivot.z);
  }

  public void changeZoom(float amount, Vector3f pivot) {
    setZoom(zoom + amount, pivot);
  }

  // special thanks to LibGDX
  // https://github.com/libgdx/libgdx/blob/ddc75209f30c3f6aa23d8888604663b09784320a/gdx/src/com/badlogic/gdx/graphics/Camera.java#L236
  public void unproject(Vector3d screenPos, int viewportWidth, int viewportHeight) {
    screenPos.x = (2 * screenPos.x) / viewportWidth - 1;
    // 2(h - y) / h - 1 = 1 - 2y / h
    screenPos.y = 1 - (2 * screenPos.y) / viewportHeight;
    screenPos.z = 2 * screenPos.z - 1;
    screenPos.mulProject(invCombined);
  }

  public void update() {
    view.mul(projection, combined);
    combined.invert(invCombined);
  }

}
