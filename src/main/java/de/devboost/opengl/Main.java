package de.devboost.opengl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

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
import static org.lwjgl.stb.STBImage.*;

public class Main {

	private Window window = new Window("First Cube", 800, 600);
	private Camera camera = new Camera(new Vector3f(0.0f, 0.0f, 10.0f), new Vector3f(0, 0, -1));
	private Shader shader;
	private int texture;

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
			float rotationSpeed = 5;
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
		Matrix4f m = new Matrix4f();
		m.setLookAt(
				camera.getPosition(),
				new Vector3f(camera.getPosition()).add(camera.getDirection()),
				new Vector3f(0.0f, 1.0f, 0.0f)
		);
		glMatrixMode(GL_MODELVIEW);
		glLoadMatrixf(m.get(fb));
	}

	private int loadTexture(String textureName) {
		ByteBuffer image;
		int width, height;
		try (MemoryStack stack = MemoryStack.stackPush()) {
			// Prepare image buffers
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer comp = stack.mallocInt(1);

			// Load image
			InputStream imageStream = Main.class.getResourceAsStream(textureName);
			if (imageStream == null) {
				throw new IOException("Could not find texture '" + textureName + "'");
			}
			byte[] imageData = IOUtils.toByteArray(imageStream);
			ByteBuffer buffer = BufferUtils.createByteBuffer(imageData.length);
			buffer.put(imageData);
			buffer.flip();

			// Flips the image vertically to make setting up UV coordinates easier
			stbi_set_flip_vertically_on_load(true);
			image = stbi_load_from_memory(buffer, w, h, comp, 4);
			if (image == null) {
				throw new RuntimeException("Failed to load a texture file!"
						+ System.lineSeparator() + stbi_failure_reason());
			}

			// Get width and height of image
			width = w.get();
			height = h.get();

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

			// Upload texture to the graphics card
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);

			return textureHandle;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return -1;
	}

	private int[] generateWireframeIndices(int[] indices) {
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < indices.length; i += 3) {
			result.add(indices[i]);
			result.add(indices[i + 1]);

			result.add(indices[i + 1]);
			result.add(indices[i + 2]);

			result.add(indices[i + 2]);
			result.add(indices[i]);
		}
		return result.stream().mapToInt(x -> x).toArray();
	}

	private void cube() {
		float[] vertices = {
				1, 1, 1, 0.5f, 0.3333333333f,      // 0
				-1, 1, 1, 0.25f, 0.3333333333f,    // 1
				1, -1, 1, 0.75f, 0.3333333333f,    // 2
				1, 1, -1, 0.5f, 0.6666666666f,     // 3
				1, -1, -1, 0.75f, 0.6666666666f,   // 4
				-1, 1, -1, 0.25f, 0.6666666666f,   // 5
				-1, -1, 1, 1.0f, 0.3333333333f,    // 6
				-1, -1, -1, 1.0f, 0.6666666666f,   // 7

				1, -1, 1, 0.5f, 0.0f,              // 8
				-1, -1, 1, 0.25f, 0.0f,            // 9

				-1, -1, -1, 0.25f, 1.0f,           // 10
				1, -1, -1, 0.5f, 1.0f,             // 11

				-1, -1, 1, 0.0f, 0.3333333333f,    // 12
				-1, -1, -1, 0.0f, 0.6666666666f,   // 13
		};
		int[] indices = {
				// front
				0, 1, 9,
				0, 9, 8,

				// right
				0, 2, 4,
				0, 4, 3,

				// back
				3, 10, 5,
				3, 11, 10,

				// left
				1, 5, 13,
				1, 13, 12,

				// top
				1, 3, 5,
				1, 0, 3,

				// bottom
				2, 6, 7,
				2, 7, 4,
		};

		shader.bind();

		// Create a vertex array object and bind it
		int vao = glGenVertexArrays();
		glBindVertexArray(vao);

		// Create a vertex buffer object, bind it and upload our vertices to it
		int vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

		// Create a vertex buffer object, bind it and upload the indices to it
		int vboIndices = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndices);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

		int floatSize = 4;
		// (number of floats) * (size of data type float in bytes) = (bytes per vertex)
		int stride = 5 * floatSize;

		// Get the location of the position vector, enable it and specify where it's data is
		int posAttribute = glGetAttribLocation(shader.getHandle(), "a_Position");
		glEnableVertexAttribArray(posAttribute);
		glVertexAttribPointer(posAttribute, 3, GL_FLOAT, false, stride, 0);

		// Get the location of the uv vector, enable it and specify where it's data is
		int uvAttribute = glGetAttribLocation(shader.getHandle(), "a_UV");
		glEnableVertexAttribArray(uvAttribute);
		glVertexAttribPointer(uvAttribute, 2, GL_FLOAT, false, stride, 3 * floatSize);

		// Bind our texture, to be used by the next draw call
		glBindTexture(GL_TEXTURE_2D, texture);

		// Draw the currently bound state
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

		// Reset state
		glBindTexture(GL_TEXTURE_2D, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		shader.unbind();
	}

	public static void main(String[] args) {
		new Main().run();
	}
}
