attribute vec4 a_color;
attribute vec3 a_position;
attribute vec2 a_texCoord0;
uniform mat4 u_projTrans;
varying vec4 v_color;
varying vec2 v_texCoord0;
uniform vec3 colors[4];
varying vec3 v_colors[4];
uniform float c;
varying float v_c;

void main(){
    v_c = c;
    v_colors = colors;
    v_color = a_color;
    v_texCoord0 = a_texCoord0;
    gl_Position = u_projTrans * vec4(a_position, 1.);
}