package de.devboost.opengl;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowRefreshCallbackI;
import org.lwjgl.opengl.GL;
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
		GLFWErrorCallback.createPrint(System.err).set();

		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

		windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
		if (windowHandle == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		glfwSetKeyCallback(windowHandle, keyCallback);

		glfwSetWindowRefreshCallback(windowHandle, (window) -> {
			try (MemoryStack stack = stackPush()) {
				IntBuffer pWidth = stack.mallocInt(1);
				IntBuffer pHeight = stack.mallocInt(1);

				glfwGetWindowSize(windowHandle, pWidth, pHeight);

				width = pWidth.get(0);
				height = pWidth.get(0);
				glViewport(0, 0, width, height);
				refreshCallback.callback(window);
			}
		});

		centerOnScreen();

		glfwMakeContextCurrent(windowHandle);
		glfwSwapInterval(1);
		glfwShowWindow(windowHandle);

		GL.createCapabilities();
	}

	public void centerOnScreen() {
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(
				windowHandle,
				(vidmode.width() - width) / 2,
				(vidmode.height() - height) / 2
		);
	}

	public void destroy() {
		glfwFreeCallbacks(windowHandle);
		glfwDestroyWindow(windowHandle);
	}

	public void swapBuffers() {
		glfwSwapBuffers(windowHandle);
	}

	public void pollEvents() {
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
