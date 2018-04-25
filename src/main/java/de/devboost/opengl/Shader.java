package de.devboost.opengl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {

	private int program;

	public Shader(InputStream vertexShader, InputStream fragmentShader) {
		program = glCreateProgram();
		compile(vertexShader, GL_VERTEX_SHADER);
		compile(fragmentShader, GL_FRAGMENT_SHADER);
		link();
	}

	public void bind() {
		glUseProgram(program);
	}

	public void unbind() {
		glUseProgram(0);
	}

	private void compile(InputStream source, int type) {
		int handle = glCreateShader(type);
		BufferedReader reader = new BufferedReader(new InputStreamReader(source));
		String sourceString = reader.lines()
				.reduce((first, second) -> first + "\n" + second)
				.orElse("");

		glShaderSource(
				handle,
				sourceString
		);

		glCompileShader(handle);

		int status = glGetShaderi(handle, GL_COMPILE_STATUS);
		if (status != GL_TRUE) {
			throw new RuntimeException(glGetShaderInfoLog(handle));
		}

		glAttachShader(program, handle);
	}

	private void link() {
		glLinkProgram(program);
		int status = glGetProgrami(program, GL_LINK_STATUS);
		if (status != GL_TRUE) {
			throw new RuntimeException(glGetProgramInfoLog(program));
		}
	}

	public int getHandle() {
		return program;
	}
}
