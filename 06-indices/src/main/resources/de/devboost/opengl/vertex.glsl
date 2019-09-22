attribute vec3 a_Position;
attribute vec2 a_UV;

varying vec2 v_UV;

void main() {
	gl_Position = gl_ModelViewProjectionMatrix * vec4(a_Position, 1);
	v_UV = a_UV;
}
