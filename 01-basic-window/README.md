# OpenGL Workshop

## Lesson 1: Creating a window

LWJGL - Light Weight Java Game Library

GLFW - C-library to create and manage windows (lwjgl provides java bindings)

OpenGL - API for rendering 2D and 3D vector graphics, by leveraging the power of the GPU

### init()

- create window with OpenGL context
- OpenGL context is needed for any of the OpenGL methods to work

(see ```glfwCreateWindow```, ```glfwMakeContextCurrent``` and ```glfwSetKeyCallback```)

### loop()

- ```glClearColor```: set the color that should be used by ```glClear```, values can be between 0.0f and 1.0f
- ```glClear```: clears the specified buffers with the current OpenGL state
- ```glfwSwapBuffers```: swaps the front and back buffers, this causes the scene that was rendered in the background to be displayed
- ```glfwPollEvents```: polls for window events

Exercises:
- change the values in ```glClearColor``` and see what happens

### cleanup()

Destroy the window and remove all callbacks
