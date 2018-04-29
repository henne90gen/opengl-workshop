package de.devboost.opengl;

import org.joml.Vector3f;

public class Camera {

	private Vector3f position;
	private Vector3f direction;

	public Camera(Vector3f position, Vector3f direction) {
		this.position = position;
		this.direction = direction;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getDirection() {
		return direction;
	}

	@Override
	public String toString() {
		return "Camera: " + position + ", " + direction;
	}
}
