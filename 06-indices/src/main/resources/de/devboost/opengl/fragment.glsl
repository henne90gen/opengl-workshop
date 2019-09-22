varying vec2 v_UV;

uniform sampler2D v_Texture;

void main() {
	gl_FragColor = texture2D(v_Texture, v_UV);
}
