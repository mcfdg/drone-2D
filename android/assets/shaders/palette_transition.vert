attribute vec4 a_color;
attribute vec3 a_position;
attribute vec2 a_texCoord0;
uniform mat4 u_projTrans;
varying vec4 v_color;
varying vec2 v_texCoord0;
uniform vec3 colors[4];
varying vec3 v_colors[4];
uniform float r;
varying float v_r;
uniform float aratio;
varying float v_aratio;

void main(){
    v_colors = colors;
    v_color = a_color;
    v_aratio = aratio;
    v_r = r;
    v_texCoord0 = a_texCoord0;
    gl_Position = u_projTrans * vec4(a_position, 1.);
}