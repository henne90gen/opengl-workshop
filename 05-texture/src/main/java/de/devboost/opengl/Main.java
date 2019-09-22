package de.devboost.opengl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.apache.commons.io.IOUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWWindowRefreshCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

public class Main {
	private final float[] vertices = {
			// front
			1, 1, 1, 0.5f, 0.3333333333f,
			-1, 1, 1, 0.25f, 0.3333333333f,
			-1, -1, 1, 0.25f, 0.0f,
			1, 1, 1, 0.5f, 0.3333333333f,
			-1, -1, 1, 0.25f, 0.0f,
			1, -1, 1, 0.5f, 0.0f,

			// right
			1, 1, 1, 0.5f, 0.3333333333f,
			1, -1, 1, 0.75f, 0.3333333333f,
			1, -1, -1, 0.75f, 0.6666666666f,
			1, 1, 1, 0.5f, 0.3333333333f,
			1, -1, -1, 0.75f, 0.6666666666f,
			1, 1, -1, 0.5f, 0.6666666666f,

			// back
			1, 1, -1, 0.5f, 0.6666666666f,
			-1, -1, -1, 0.25f, 1.0f,
			-1, 1, -1, 0.25f, 0.6666666666f,
			1, 1, -1, 0.5f, 0.6666666666f,
			1, -1, -1, 0.5f, 1.0f,
			-1, -1, -1, 0.25f, 1.0f,

			// left
			-1, 1, 1, 0.25f, 0.3333333333f,
			-1, 1, -1, 0.25f, 0.6666666666f,
			-1, -1, -1, 0.0f, 0.6666666666f,
			-1, 1, 1, 0.25f, 0.3333333333f,
			-1, -1, -1, 0.0f, 0.6666666666f,
			-1, -1, 1, 0.0f, 0.3333333333f,

			// top
			-1, 1, 1, 0.25f, 0.3333333333f,
			1, 1, -1, 0.5f, 0.6666666666f,
			-1, 1, -1, 0.25f, 0.6666666666f,
			-1, 1, 1, 0.25f, 0.3333333333f,
			1, 1, 1, 0.5f, 0.3333333333f,
			1, 1, -1, 0.5f, 0.6666666666f,

			// bottom
			1, -1, 1, 0.75f, 0.3333333333f,
			-1, -1, 1, 1.0f, 0.3333333333f,
			-1, -1, -1, 1.0f, 0.6666666666f,
			1, -1, 1, 0.75f, 0.3333333333f,
			-1, -1, -1, 1.0f, 0.6666666666f,
			1, -1, -1, 0.75f, 0.6666666666f,
	};

	private Window window = new Window("First Cube", 800, 600);
	private Camera camera = new Camera(new Vector3f(0.0f, 0.0f, 10.0f), new Vector3f(0, 0, -1));
	private Vector3f cubeRotation = new Vector3f(0, 0, 0);
	private Shader shader;
	private int texture;

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

		shader = new Shader(
				Main.class.getResourceAsStream("vertex.glsl"),
				Main.class.getResourceAsStream("fragment.glsl")
		);

		texture = loadTexture("CubeTexture.png");
		if (texture == -1) {
			System.err.println("Could not load texture.");
			System.exit(1);
		}

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

	private int loadTexture(String textureName) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			// Prepare image buffers
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer data = stack.mallocInt(1);

			// Load image
			InputStream imageStream = Main.class.getResourceAsStream(textureName);
			if (imageStream == null) {
				throw new IOException("Could not find texture '" + textureName + "'");
			}
			byte[] imageData = IOUtils.toByteArray(imageStream);
			ByteBuffer buffer = BufferUtils.createByteBuffer(imageData.length);
			buffer.put(imageData);
			buffer.flip();

			// Flips the image vertically when loading to make setting up UV coordinates easier
			stbi_set_flip_vertically_on_load(true);
			ByteBuffer image = stbi_load_from_memory(buffer, w, h, data, 4);
			if (image == null) {
				throw new RuntimeException("Failed to load a texture file!"
						+ System.lineSeparator() + stbi_failure_reason());
			}

			// Get width and height of image
			int width = w.get();
			int height = h.get();

			// Generate texture handle and set up texture properties
			int textureHandle = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, textureHandle);

			// Specify the algorithm that should be used when the texture needs to be minified or magnified
			// https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glTexParameter.xhtml
			// GL_NEAREST is more pixelated, since there is no interpolation
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			// GL_LINEAR is smoother, since the color is interpolated between the 4 closest texture pixels
			// glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			// glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

			// Upload texture to the GPU
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);

			return textureHandle;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return -1;
	}

	private void cube() {
		shader.bind();

		// Create a vertex array object and bind it
		int vertexArrayObject = glGenVertexArrays();
		glBindVertexArray(vertexArrayObject);

		// Create a vertex buffer object, bind it and upload our vertices to it
		int vertexBufferObject = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

		int floatSize = 4;
		int floatsPerVertex = 5;
		// (number of floats per vertex) * (size of data type float in bytes) = bytes per vertex
		int stride = floatsPerVertex * floatSize;

		// Get the location of the position vector, enable it and specify where it's data is
		int posAttribute = glGetAttribLocation(shader.getHandle(), "a_Position");
		glEnableVertexAttribArray(posAttribute);
		glVertexAttribPointer(posAttribute, 3, GL_FLOAT, false, stride, 0);

		// Get the location of the color vector, enable it and specify where it's data is
		int colAttribute = glGetAttribLocation(shader.getHandle(), "a_UV");
		glEnableVertexAttribArray(colAttribute);
		glVertexAttribPointer(colAttribute, 2, GL_FLOAT, false, stride, 3 * floatSize);

		// Bind our texture, to be used by the next draw call
		glBindTexture(GL_TEXTURE_2D, texture);

		// Draw the currently bound state
		glDrawArrays(GL_TRIANGLES, 0, vertices.length / floatsPerVertex);

		// Reset state
		glBindTexture(GL_TEXTURE_2D, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		shader.unbind();
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
