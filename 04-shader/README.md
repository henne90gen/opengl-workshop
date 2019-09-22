# OpenGL Workshop

## Lesson 4: Using shaders to display our cube

### Shaders vs. Fixed Function Pipeline (FFP)

- FFP depends on global state
- shaders give more control over calculations
- shaders are more efficient, because calculations are offloaded to the GPU

### createShaderProgram()

- create program
- compile and attach vertex shader
- compile and attach fragment shader
- link program
- check for errors

### compileShader()

- load the source code of the shader from a file
- attach the source code to our shader handle
- compile
- check for errors

### cube()

- Vertex Array Object: stores all state necessary to supply vertex data (layout, vertex buffer objects)
- Vertex Buffer Objects: stores actual vertex data
- 2 vertex attributes: position, color
- each vertex has 6 floats associated with it: 3 floats for position, 3 floats for color
- possible ways to upload the vertex data (V - Vertex Position, C - Color):
	- (VVVV)(CCCC): separate buffer object for each vertex attribute
	- (VVVVCCCC): one buffer object, with large offset between attributes
	- (VCVCVCVC): interleave vertex attributes, use offset and stride to differentiate between them
- ```glDrawArrays```: draws vertex array that is currently bound in this context

### vertex.glsl

- defining attributes and variables that need to be passed on
- ```gl_Position```: output vertex position
- ```gl_ModelViewProjectionMatrix```: accessing MVP matrix from fixed function pipeline
- passing color through to fragment shader

### fragment.glsl

- ```gl_FragColor```: output color
- adding transparency component to color
