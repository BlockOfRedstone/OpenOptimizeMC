#version 460

layout(location = 0) in vec3 vertex_position;
layout(location = 1) in vec3 vertex_color;
layout(location = 2) in vec2 texture_pos;
uniform mat4 view_matrix;
uniform mat4 model_matrix;
uniform mat4 projection_matrix;
uniform float entityColorR;
uniform float entityColorG;
uniform float entityColorB;

out vec3 color;

void main() {
    color = vertex_color;
    color = vertex_color;
    color.r *= entityColorR / 1.5;
    color.g *= entityColorG / 1.5;
    color.b *= entityColorB / 1.5;
    gl_Position = projection_matrix * view_matrix * model_matrix * vec4(vertex_position, 1.0); // должно быть нормированным (-1 ; 1)
}