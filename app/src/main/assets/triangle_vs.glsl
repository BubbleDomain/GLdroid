#version 300 es
layout (location = 0) in vec3 av_Position;
void main() {
    gl_Position = vec4(av_Position, 1.0);
}