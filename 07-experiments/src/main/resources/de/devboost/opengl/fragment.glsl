varying vec2 v_UV;
varying vec3 v_Color;

uniform sampler2D v_Texture;

void main() {
	gl_FragColor = texture2D(v_Texture, v_UV) + vec4(v_Color, 1)	;
}
