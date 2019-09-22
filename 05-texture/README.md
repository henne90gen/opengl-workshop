# OpenGL Workshop

## Lesson 5: Render a texture

### loadTexture()

- read image file into byte buffer
- use stbi to load image from byte buffer
- create a texture handle and bind it
- set up texture parameters (minimization and magnification filters)
- upload pixel data to GPU

### cube()

- UV coordinates instead of colors for each vertex
- map part of our texture to a triangle

![alt-text](http://www.opengl-tutorial.org/assets/images/tuto-5-textured-cube/UVintro.png)

### vertex.glsl

- pass along UV coordinates

### fragment.glsl

- ```texture2D```: fill fragment with the corresponding data from our texture
