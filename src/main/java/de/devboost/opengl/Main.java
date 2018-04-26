package de.devboost.opengl;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWWindowRefreshCallbackI;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Main {

	private Window window = new Window("First Cube", 800, 600);
	private Camera camera = new Camera(new Vector3f(0.0f, 0.0f, 10.0f), new Vector3f(0, 0, -1));
	private Shader shader;

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

		shader = new Shader(
				Main.class.getResourceAsStream("vertex.glsl"),
				Main.class.getResourceAsStream("fragment.glsl")
		);

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

//	int[] indicesWireframe = {
//			0, 1, 1, 6, 6, 0,
//			0, 6, 6, 2, 2, 0,
//
//			0, 2, 2, 4, 4, 0,
//			0, 4, 4, 3, 3, 0,
//
//			3, 7, 7, 5, 5, 3,
//			3, 4, 4, 7, 7, 3,
//
//			1, 7, 7, 6, 6, 1,
//			1, 5, 5, 7, 7, 1,
//
//			1, 3, 3, 5, 5, 1,
//			1, 0, 0, 3, 3, 1,
//
//			6, 7, 7, 4, 4, 6,
//			6, 4, 4, 2, 2, 6
//	};

	private void cube() {
		float[] vertices = {
				1, 1, 1,    // 0
				-1, 1, 1,   // 1
				1, -1, 1,   // 2
				1, 1, -1,   // 3
				1, -1, -1,  // 4
				-1, 1, -1,  // 5
				-1, -1, 1,  // 6
				-1, -1, -1  // 7
		};
		int[] indices = {
				// front
				0, 1, 6,
				0, 6, 2,

				// right
				0, 2, 4,
				0, 4, 3,

				// back
				3, 7, 5,
				3, 4, 7,

				// left
				1, 7, 6,
				1, 5, 7,

				// top
				1, 3, 5,
				1, 0, 3,

				// bottom
				6, 7, 4,
				6, 4, 2,
		};

		shader.bind();

		// Create a vertex array object and bind it
		int vao = glGenVertexArrays();
		glBindVertexArray(vao);

		// Create a vertex buffer object, bind it and upload our vertices to it
		int vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

		int vboIndices = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndices);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

		int floatSize = 4;
		// Number of floats * Size of data type float in bytes = bytes per vertex
		int stride = 3 * floatSize;

		// Get the location of the position vector, enable it and specify where it's data is
		int posAttribute = glGetAttribLocation(shader.getHandle(), "a_Position");
		glEnableVertexAttribArray(posAttribute);
		glVertexAttribPointer(posAttribute, 3, GL_FLOAT, false, stride, 0);

		// Draw the currently bound state
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

		// Reset state
		shader.unbind();
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glUseProgram(0);
	}

	public static void main(String[] args) {
		new Main().run();
	}
}
