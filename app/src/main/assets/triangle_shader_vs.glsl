#version 300 es
layout (location = 0) in vec3 av_Position;
layout (location = 1) in vec3 av_color;

out vec3 shader_color;

void main() {
    gl_Position = vec4(av_Position, 1.0);
    shader_color = av_color;
}