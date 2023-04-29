#version 460

in vec3 color; // уже интерполированно.
in float colorM;
out vec4 frag_color;
void main() {
    frag_color = sqrt(colorM) * vec4(color, 1.0);
}