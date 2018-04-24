package de.devboost.opengl;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowRefreshCallbackI;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

	private final String title;
	private int width;
	private int height;

	private long windowHandle;

	public Window(String title, int width, int height) {
		this.title = title;
		this.width = width;
		this.height = height;
	}

	public void init(GLFWKeyCallbackI keyCallback, GLFWWindowRefreshCallbackI refreshCallback) {
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
		windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
		if (windowHandle == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(windowHandle, keyCallback);

		glfwSetWindowRefreshCallback(windowHandle, (window) -> {
			try (MemoryStack stack = stackPush()) {
				IntBuffer pWidth = stack.mallocInt(1); // int*
				IntBuffer pHeight = stack.mallocInt(1); // int*

				glfwGetWindowSize(windowHandle, pWidth, pHeight);

				width = pWidth.get(0);
				height = pWidth.get(0);
				glViewport(0, 0, width, height);
				refreshCallback.callback(window);
			}
		});

		centerOnScreen();

		// Make the OpenGL context current
		glfwMakeContextCurrent(windowHandle);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(windowHandle);

	}

	public void centerOnScreen() {
		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		// Center the window
		glfwSetWindowPos(
				windowHandle,
				(vidmode.width() - width) / 2,
				(vidmode.height() - height) / 2
		);
	}

	public void destroy() {
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(windowHandle);
		glfwDestroyWindow(windowHandle);
	}

	public void swapBuffers() {
		glfwSwapBuffers(windowHandle);
	}

	public void pollEvents() {
		// Poll for window events. The key callback above will only be
		// invoked during this call.
		glfwPollEvents();
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(windowHandle);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
