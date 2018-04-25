attribute vec3 a_Position;
attribute vec3 a_Color;

varying vec3 v_Color;

void main() {
	gl_Position = gl_ModelViewProjectionMatrix * vec4(a_Position, 1);
	v_Color = a_Color;
}
