# OpenGL Workshop

## Lesson 2: Rendering a triangle

### Fixed function pipeline

[OpenGL Rendering Pipeline](https://www.khronos.org/opengl/wiki/Rendering_Pipeline_Overview)

![alt text](https://www.khronos.org/opengl/wiki_opengl/images/RenderingPipeline.png)

#### Pipeline stages

- Vertex Specification
	- Upload vertex data to the GPU
- Vertex Shader
	- Programmable shader, that is run for each vertex
- Tessellation
	- Increase fidelity by increasing vertex count
- Geometry Shader
	- Modify shapes (geometries)
- Vertex Post-Processing
	- Final fixed function steps
- Primitive Assembly
	- Construct simple primitives (points, lines and triangles)from output of the previous stages
- Rasterization
	- Primitives are being turned into fragments, by rasterizing them
	- at least one fragment per pixel area (can be more depending on OpenGL state)
- Fragment Shader
	- Programmable shader, that is run for each fragment
	- determines the final color of each fragment


#### Fixed function pipeline

- All stages of the rendering pipeline are fixed and not programmable
- They can be configured by setting OpenGL state

### triangle()

Vertices and colors:

Using the fixed function pipeline, we can define vertices in 2D and 3D.
Each vertex can also have a color associated with it.

Exercises:
- change the values in ```glVertex2f```
- use Math.sin() to generate the y coordinate
- change ```glVertex2f``` to ```glVertex3f``` and play around with the coordinates
- change the values in ```glColor3f```
