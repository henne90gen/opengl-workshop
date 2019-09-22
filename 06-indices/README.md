# OpenGL Workshop

## Lesson 6: Use indices to avoid repetition

### Overview

- vertex data and how the vertices are drawn should be separated
- two arrays: vertex data and index data
- index data specifies in which order the vertices are drawn
- length of old vertices array: 180
- length of new vertices array: 70
- less data needs to be uploaded to the GPU
- enables alternate drawing methods for existing vertices (wireframe)

### cube()

- upload vertices as always to GL_ARRAY_BUFFER
- upload indices to GL_ELEMENT_ARRAY_BUFFER
- ```glDrawElements```: draw the vertices specified by the index buffer
