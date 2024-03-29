#version 300 es

layout (location = 0) in vec4 a_Position;
layout (location = 1) in vec2 a_texCoord;

uniform mat4 matrix;
out vec2 v_texCoord;

void main()
{
    gl_Position = matrix * a_Position;
    v_texCoord = a_texCoord;
}