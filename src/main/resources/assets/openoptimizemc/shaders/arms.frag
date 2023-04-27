#version 460

in vec3 color; // уже интерполированно.
out vec4 frag_color;

void main() {
    frag_color = vec4(color, 1.0);
}