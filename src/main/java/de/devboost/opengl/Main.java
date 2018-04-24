package de.devboost.opengl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {

	// The window handle
	private long window;

	private Vector3f cameraPosition = new Vector3f(0.0f, 0.0f, 10.0f);
	private Vector3f cameraDirection = new Vector3f(0, 0, -1);

	public void run() {
		init();
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(800, 600, "Hello World!", NULL, NULL);
		if (window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
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
			cameraPosition.add(new Vector3f(cameraDirection).normalize().mul(forward))
					.add(new Vector3f(-cameraDirection.z, cameraDirection.y, cameraDirection.x).mul(sideways));
			cameraDirection.rotateY((float) Math.toRadians(rotation));
		});

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
					window,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
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
		glEnable(GL_DEPTH_TEST);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			setupProjectionAndView();

			cube();

			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}

	private void setupProjectionAndView() {
		int width = 0;
		int height = 0;
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			glfwGetWindowSize(window, pWidth, pHeight);

			width = pWidth.get(0);
			height = pWidth.get(0);
			glViewport(0, 0, width, height);
		}

		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		Matrix4f m = new Matrix4f();
		m.setPerspective((float) Math.toRadians(45.0f), (float) width / (float) height, 0.01f, 100.0f);
		glMatrixMode(GL_PROJECTION);
		glLoadMatrixf(m.get(fb));

		m.setLookAt(
				cameraPosition,
				new Vector3f(cameraPosition).add(cameraDirection),
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
