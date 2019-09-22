# OpenGL Workshop

## Lesson 3: Displaying a spinning cube

### Matrices

- 4D vector for 3D and 3D vector for 2D 
	- additional coordinate is 1 for positions and 0 for directions
	- prevents directions from being affected by transformations
- translation matrix:

![alt-text](http://www.opengl-tutorial.org/assets/images/tuto-3-matrix/translationMatrix.png)
- scaling matrix:

![alt-text](http://www.opengl-tutorial.org/assets/images/tuto-3-matrix/scalingMatrix.png)
- accumulate transformation, scaling and rotation by matrix multiplication
- order of operation is important
	- scale -> rotate -> translate
- TransformedVector = TranslationMatrix * RotationMatrix * ScaleMatrix * OriginalVector
- Model, View and Projection matrix to separate transformations cleanly

![alt-text](http://www.opengl-tutorial.org/assets/images/tuto-3-matrix/MVP.png)

### loop()

- ```glEnable(GL_DEPTH_TEST)```: makes sure the GPU checks, which shape is closest to the camera, before rendering them

### setupModelView()

- ```setLookAt```: convenience function to position the camera and make it look at a certain point
- ```glMatrixMode```: set the currently selected matrix of the fixed function pipeline
- ```glLoadMatrixf```: upload model-view matrix to GPU

### cube()

- specify all vertices
- draw them with for loop


### Exercises

- move the camera with the arrow keys (keyCallback contains the code for it)
- change the colors of the cube
