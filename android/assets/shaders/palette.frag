precision mediump float;
varying vec4 v_color;
varying vec2 v_texCoord0;
varying vec3 v_colors[4];
varying float v_c;
uniform sampler2D u_sampler2D;

void main(){
    vec4 color = texture2D(u_sampler2D, v_texCoord0);
    color.rgb = v_colors[int(color.g * 4. - 1.)];
    gl_FragColor = color;
}