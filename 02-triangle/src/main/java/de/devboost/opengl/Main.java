package de.devboost.opengl;

import java.nio.IntBuffer;

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

	private long window;

	public void run() {
		init();

		loop();

		cleanup();
	}

	private void init() {
		GLFWErrorCallback.createPrint(System.err).set();

		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

		window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
		if (window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
				glfwSetWindowShouldClose(window, true);
			}
		});

		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);
			glfwGetWindowSize(window, pWidth, pHeight);

			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwSetWindowPos(
					window,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
			);
		}

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);

		GL.createCapabilities();
	}

	private void loop() {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		while (!glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			triangle();

			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}

	private void cleanup() {
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	/**
	 * Draw a triangle using the fixed function pipeline
	 */
	private void triangle() {
		// Start the drawing process
		glBegin(GL_TRIANGLES);

		// Add a vertex
		float y = (float) Math.sin(System.currentTimeMillis() / 1000.0d);
		glVertex3f(-1, -1, 0);
		// Add a color to that vertex
		glColor3f(1, 0, 0);

		// Add a vertex
		glVertex3f(1, -1, 0);
		// Add a color to that vertex
		glColor3f(1, 0, 0);

		// Add a vertex
		glVertex3f(0, 1, 0);
		// Add a color to that vertex
		glColor3f(1, 0, 0);

		// End the drawing process
		glEnd();
	}

	public static void main(String[] args) {
		new Main().run();
	}
}
