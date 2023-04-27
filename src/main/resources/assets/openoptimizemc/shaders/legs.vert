#version 460

layout(location = 0) in vec3 vertex_position;
layout(location = 1) in vec3 vertex_color;
layout(location = 2) in vec2 texture_pos;
uniform mat4 view_matrix;
uniform mat4 model_matrix;
uniform mat4 projection_matrix;

out vec3 color;

void main() {
    color = vertex_color;
    gl_Position = projection_matrix * view_matrix * model_matrix * vec4(vertex_position, 1.0); // должно быть нормированным (-1 ; 1)
}