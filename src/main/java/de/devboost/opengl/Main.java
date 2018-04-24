package de.devboost.opengl;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWWindowRefreshCallbackI;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {

	private Window window = new Window("First Cube", 800, 600);
	private Camera camera = new Camera(new Vector3f(0.0f, 0.0f, 10.0f), new Vector3f(0, 0, -1));

	public void run() {
		init();
		loop();

		window.destroy();

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init() {
		GLFWKeyCallbackI keyCallback = (window, key, scancode, action, mods) -> {
			float rotation = 0;
			float speed = 0.5f;
			float rotationSpeed = 2;
			float forward = 0;
			float sideways = 0;
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
			} else if (key == GLFW_KEY_E) {
				rotation -= rotationSpeed;
			} else if (key == GLFW_KEY_Q) {
				rotation += rotationSpeed;
			} else if (key == GLFW_KEY_W) {
				forward = speed;
			} else if (key == GLFW_KEY_A) {
				sideways = -speed;
			} else if (key == GLFW_KEY_S) {
				forward = -speed;
			} else if (key == GLFW_KEY_D) {
				sideways = speed;
			}
			Vector3f forwardMovement = new Vector3f(camera.getDirection()).normalize().mul(forward);
			Vector3f sidewaysMovement = new Vector3f(-camera.getDirection().z,
					camera.getDirection().y,
					camera.getDirection().x)
					.mul(sideways);
			camera.getPosition().add(forwardMovement)
					.add(sidewaysMovement);
			camera.getDirection().rotateY((float) Math.toRadians(rotation));
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
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Enable depth testing, which lets 3D shapes render correctly
		glEnable(GL_DEPTH_TEST);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!window.shouldClose()) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			setupView();

			cube();

			window.swapBuffers();
			window.pollEvents();
		}
	}

	private void setupView() {
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		Matrix4f m = new Matrix4f();
		m.setLookAt(
				camera.getPosition(),
				new Vector3f(camera.getPosition()).add(camera.getDirection()),
				new Vector3f(0.0f, 1.0f, 0.0f)
		);
		glMatrixMode(GL_MODELVIEW);
		glLoadMatrixf(m.get(fb));
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
		glBegin(GL_TRIANGLES);
		for (int i = 0; i < vertices.length; i += 6) {
			glColor3f(vertices[i + 3], vertices[i + 4], vertices[i + 5]);
			glVertex3f(vertices[i], vertices[i + 1], vertices[i + 2]);
		}
		glEnd();
	}

	public static void main(String[] args) {
		new Main().run();
	}
}
