precision mediump float;
varying vec4 v_color;
varying vec2 v_texCoord0;
varying vec3 v_colors[4];
varying float v_r;
varying float v_aratio;
uniform sampler2D u_sampler2D;

const float FADE_SIZE = .8;
const float WAVE_AMP = .15;
const float PEAKS = 7.0;

void main(){
    vec4 color = texture2D(u_sampler2D, v_texCoord0);

    color.rgb = v_colors[int(color.g * 4. - 1.)];

    float dst = sqrt(pow(float(.5 - v_texCoord0.x), 2.) + pow(float((.5 - v_texCoord0.y) / v_aratio), 2.));
    float dev = WAVE_AMP * v_r * sin(atan((.5 - v_texCoord0.y) / v_aratio, .5 - v_texCoord0.x) * PEAKS);
    dev *= v_r / (v_r + FADE_SIZE);

    color.a = max(0.0, 1.0 - smoothstep(v_r * (1.0 + FADE_SIZE) + dev - FADE_SIZE, v_r * (1.0 + FADE_SIZE) + dev, dst));

    gl_FragColor = color;
}