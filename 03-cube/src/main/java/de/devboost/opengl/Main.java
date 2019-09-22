package de.devboost.opengl;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWWindowRefreshCallbackI;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {

	private Window window = new Window("First Cube", 800, 600);
	private Camera camera = new Camera(new Vector3f(0.0f, 0.0f, 10.0f), new Vector3f(0, 0, -1));
	private Vector3f cubeRotation = new Vector3f(0, 0, 0);

	private void run() {
		init();
		loop();
		cleanup();
	}

	private void init() {
		GLFWKeyCallbackI keyCallback = (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
				glfwSetWindowShouldClose(window, true);
			}
			float yDirection = 0;
			if (key == GLFW_KEY_UP) {
				yDirection = 1;
			} else if (key == GLFW_KEY_DOWN) {
				yDirection = -1;
			}
			float xDirection = 0;
			if (key == GLFW_KEY_LEFT) {
				xDirection = -1;
			} else if (key == GLFW_KEY_RIGHT) {
				xDirection = 1;
			}
			camera.getDirection().add(new Vector3f(xDirection * 0.01f, yDirection * 0.01f, 0));
		};

		GLFWWindowRefreshCallbackI refreshCallback = (windowHandle) -> {
			FloatBuffer fb = BufferUtils.createFloatBuffer(16);
			Matrix4f m = new Matrix4f();
			m.setPerspective((float) Math.toRadians(45.0f),
					(float) window.getWidth() / (float) window.getHeight(),
					0.01f,
					100.0f);
			glMatrixMode(GL_PROJECTION);
			glLoadMatrixf(m.get(fb));
		};

		window.init(keyCallback, refreshCallback);
	}

	private void loop() {
		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Enable depth testing, which lets 3D shapes render correctly
		glEnable(GL_DEPTH_TEST);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!window.shouldClose()) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			setupModelView();

			cube();

			window.swapBuffers();
			window.pollEvents();
		}
	}

	private void setupModelView() {
		// Create a float buffer for our 4x4 view matrix
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		Matrix4f modelViewMatrix = new Matrix4f();

		// With this convenience function, we can construct a view matrix from our camera parameters
		modelViewMatrix.setLookAt(
				camera.getPosition(), // eye: position of camera
				new Vector3f(camera.getPosition()).add(camera.getDirection()), // center: position in space to look at
				new Vector3f(0.0f, 1.0f, 0.0f) // up: direction of 'up'
		);

		// Changing the cubes rotation vector. This happens every frame, which causes the cube to spin
		cubeRotation.add(new Vector3f(0.01f, 0.01f, 0.01f));

		// Applying the rotation vector to the view matrix
		modelViewMatrix.rotateXYZ(cubeRotation);

		// Set the current matrix mode
		glMatrixMode(GL_MODELVIEW);

		// Load the view matrix into the currently selected matrix
		glLoadMatrixf(modelViewMatrix.get(fb));
	}

	private void cube() {
		int[] vertices = {
				// front
				1, 1, 1, 1, 0, 0,
				-1, 1, 1, 1, 0, 0,
				-1, -1, 1, 1, 0, 0,
				1, 1, 1, 1, 0, 0,
				-1, -1, 1, 1, 0, 0,
				1, -1, 1, 1, 0, 0,

				// right
				1, 1, 1, 0, 1, 0,
				1, -1, 1, 0, 1, 0,
				1, -1, -1, 0, 1, 0,
				1, 1, 1, 0, 1, 0,
				1, -1, -1, 0, 1, 0,
				1, 1, -1, 0, 1, 0,

				// back
				1, 1, -1, 0, 0, 1,
				-1, -1, -1, 0, 0, 1,
				-1, 1, -1, 0, 0, 1,
				1, 1, -1, 0, 0, 1,
				1, -1, -1, 0, 0, 1,
				-1, -1, -1, 0, 0, 1,

				// left
				-1, 1, 1, 0, 1, 1,
				-1, -1, -1, 0, 1, 1,
				-1, -1, 1, 0, 1, 1,
				-1, 1, 1, 0, 1, 1,
				-1, 1, -1, 0, 1, 1,
				-1, -1, -1, 0, 1, 1,

				// top
				-1, 1, 1, 1, 0, 1,
				1, 1, -1, 1, 0, 1,
				-1, 1, -1, 1, 0, 1,
				-1, 1, 1, 1, 0, 1,
				1, 1, 1, 1, 0, 1,
				1, 1, -1, 1, 0, 1,

				// bottom
				-1, -1, 1, 1, 1, 0,
				-1, -1, -1, 1, 1, 0,
				1, -1, -1, 1, 1, 0,
				-1, -1, 1, 1, 1, 0,
				1, -1, -1, 1, 1, 0,
				1, -1, 1, 1, 1, 0,
		};

		// Begin drawing triangles
		glBegin(GL_TRIANGLES);

		// Loop through vertex data and upload all vertices with their colors
		for (int i = 0; i < vertices.length; i += 6) {
			glColor3f(vertices[i + 3], vertices[i + 4], vertices[i + 5]);
			glVertex3f(vertices[i], vertices[i + 1], vertices[i + 2]);
		}

		// End drawing triangles
		glEnd();
	}

	private void cleanup() {
		window.destroy();
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	public static void main(String[] args) {
		new Main().run();
	}
}
