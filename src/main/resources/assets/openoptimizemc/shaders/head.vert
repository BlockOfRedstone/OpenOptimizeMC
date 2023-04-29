#version 460

layout(location = 0) in vec3 vertex_position;
layout(location = 1) in vec3 vertex_color;
layout(location = 2) in vec2 texture_pos;
uniform mat4 view_matrix;
uniform mat4 model_matrix;
uniform mat4 projection_matrix;
uniform vec3 partColorModifier;
uniform float _globalK;

out vec3 color;
out float colorM;

void main() {
    color = partColorModifier * vertex_color;
    colorM = texture_pos.r / cos(vertex_position.x - vertex_position.z);
    gl_Position = projection_matrix * view_matrix * model_matrix * vec4(vertex_position, 1.0); // должно быть нормированным (-1 ; 1)
}