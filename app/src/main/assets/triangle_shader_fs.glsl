#version 300 es
out vec4 FragColor;

in vec3 shader_color;

void main()
{
    FragColor = vec4(shader_color, 1.0f);
}