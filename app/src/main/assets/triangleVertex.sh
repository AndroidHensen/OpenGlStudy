#version 300 es
layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec4 aColor;

uniform mat4 matrix;
out vec4 vColor;

void main(){
    gl_Position = matrix * vPosition;
    vColor = aColor;
}