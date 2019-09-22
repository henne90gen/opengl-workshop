attribute vec3 a_Position;
attribute vec2 a_UV;
attribute vec3 a_Color;

varying vec2 v_UV;
varying vec3 v_Color;

void main() {
	gl_Position = gl_ModelViewProjectionMatrix * vec4(a_Position, 1);
	v_UV = a_UV;
	v_Color = a_Color;
}
