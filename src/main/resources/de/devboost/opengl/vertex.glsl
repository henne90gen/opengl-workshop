attribute vec3 a_Position;

varying vec3 v_Color;

void main() {
	gl_Position = gl_ModelViewProjectionMatrix * vec4(a_Position, 1);
	v_Color = vec3(0);
}
