package de.devboost.opengl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
	private final float[] vertices = {
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

	private Window window = new Window("First Cube", 800, 600);
	private Camera camera = new Camera(new Vector3f(0.0f, 0.0f, 10.0f), new Vector3f(0, 0, -1));
	private Vector3f cubeRotation = new Vector3f(0, 0, 0);

	public void run() {
		init();
		loop();
		cleanUp();
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
			Matrix4f projectionMatrix = new Matrix4f();
			projectionMatrix.setPerspective((float) Math.toRadians(45.0f),
					(float) window.getWidth() / (float) window.getHeight(),
					0.01f,
					100.0f);
			glMatrixMode(GL_PROJECTION);
			glLoadMatrixf(projectionMatrix.get(fb));
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

			setupModelView();

			cube();

			window.swapBuffers();
			window.pollEvents();
		}
	}

	private void setupModelView() {
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		Matrix4f modelViewMatrix = new Matrix4f();
		modelViewMatrix.setLookAt(
				camera.getPosition(),
				new Vector3f(camera.getPosition()).add(camera.getDirection()),
				new Vector3f(0.0f, 1.0f, 0.0f)
		);

		cubeRotation.add(new Vector3f(0.01f, 0.01f, 0.01f));
		modelViewMatrix.rotateXYZ(cubeRotation);

		glMatrixMode(GL_MODELVIEW);
		glLoadMatrixf(modelViewMatrix.get(fb));
	}

	private void cube() {
		int programHandle = createShaderProgram();

		// Bind the shader program, so that it will be used by the next draw call
		glUseProgram(programHandle);

		// Create a vertex array object and bind it
		int vertexArrayObject = glGenVertexArrays();
		glBindVertexArray(vertexArrayObject);

		// Create a vertex buffer object, bind it and upload our vertices to it
		int vertexBufferObject = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

		int floatSize = 4;
		int floatsPerVertex = 6;
		// (number of floats per vertex) * (size of data type float in bytes) = bytes per vertex
		int stride = floatsPerVertex * floatSize;

		// Get the location of the position vector, enable it and specify where it's data is
		int posAttribute = glGetAttribLocation(programHandle, "a_Position");
		glEnableVertexAttribArray(posAttribute);
		glVertexAttribPointer(posAttribute, 3, GL_FLOAT, false, stride, 0);

		// Get the location of the color vector, enable it and specify where it's data is
		int colAttribute = glGetAttribLocation(programHandle, "a_Color");
		glEnableVertexAttribArray(colAttribute);
		glVertexAttribPointer(colAttribute, 3, GL_FLOAT, false, stride, 3 * floatSize);

		// Draw the currently bound state
		glDrawArrays(GL_TRIANGLES, 0, vertices.length / floatsPerVertex);

		// Reset state
		glBindVertexArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glUseProgram(0);
	}

	private int createShaderProgram() {
		// Create the shader program
		int programHandle = glCreateProgram();

		// Compile the vertex shader
		int vertexShaderHandle = compileShader("vertex.glsl", GL_VERTEX_SHADER);

		// Attach the vertex shader to the shader program
		glAttachShader(programHandle, vertexShaderHandle);

		// Compile the fragment shader
		int fragmentShaderHandle = compileShader("fragment.glsl", GL_FRAGMENT_SHADER);

		// Attach the fragment shader to the shader program
		glAttachShader(programHandle, fragmentShaderHandle);

		// Link the shader program
		glLinkProgram(programHandle);

		// Check for errors
		int status = glGetProgrami(programHandle, GL_LINK_STATUS);
		if (status != GL_TRUE) {
			throw new RuntimeException(glGetProgramInfoLog(programHandle));
		}
		return programHandle;
	}

	private int compileShader(String shaderName, int shaderType) {
		int shaderHandle = glCreateShader(shaderType);
		BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream(shaderName)));
		String sourceString = reader.lines()
				.reduce((first, second) -> first + "\n" + second)
				.orElse("");

		// Attach the source code to the fragment shader
		glShaderSource(shaderHandle, sourceString);

		// Compile the fragment shader
		glCompileShader(shaderHandle);

		// Check for errors
		int status = glGetShaderi(shaderHandle, GL_COMPILE_STATUS);
		if (status != GL_TRUE) {
			throw new RuntimeException(glGetShaderInfoLog(shaderHandle));
		}
		return shaderHandle;
	}

	private void cleanUp() {
		window.destroy();

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	public static void main(String[] args) {
		new Main().run();
	}
}
