#include "gles3_renderer.h"
#include "log.h"

#ifdef __ANDROID__

static const GLchar *vert_src =
    "#version 300 es\n"
    "layout(location = 0) in vec3 aPos;\n"
    "layout(location = 1) in vec2 aTexCoord;\n"
    "out vec2 vTexCoord;\n"
    "void main() {\n"
    "    vTexCoord = aTexCoord;\n"
    "    gl_Position = vec4(aPos, 1.0);\n"
    "}\n";

static const GLchar *frag_src =
    "#version 300 es\n"
    "precision mediump float;\n"
    "in vec2 vTexCoord;\n"
    "out vec4 FragColor;\n"
    "uniform float uTime;\n"
    "void main() {\n"
    "    float c = 0.5 + 0.5 * sin(uTime + vTexCoord.x * 6.28);\n"
    "    FragColor = vec4(c, vTexCoord.x, vTexCoord.y, 1.0);\n"
    "}\n";

static GLuint compile_shader(GLenum type, const GLchar *src) {
    GLuint shader = glCreateShader(type);
    glShaderSource(shader, 1, &src, NULL);
    glCompileShader(shader);

    GLint ok;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &ok);
    if (!ok) {
        GLchar log[512];
        glGetShaderInfoLog(shader, 512, NULL, log);
        LOGE("GLES3: shader compile failed: %s", log);
        glDeleteShader(shader);
        return 0;
    }
    return shader;
}

int gles3_renderer_init(gles3_renderer_t *r, int w, int h) {
    r->width = w;
    r->height = h;

    GLuint vs = compile_shader(GL_VERTEX_SHADER, vert_src);
    GLuint fs = compile_shader(GL_FRAGMENT_SHADER, frag_src);
    if (!vs || !fs) return -1;

    r->program = glCreateProgram();
    glAttachShader(r->program, vs);
    glAttachShader(r->program, fs);
    glLinkProgram(r->program);

    GLint linked;
    glGetProgramiv(r->program, GL_LINK_STATUS, &linked);
    if (!linked) {
        GLchar log[512];
        glGetProgramInfoLog(r->program, 512, NULL, log);
        LOGE("GLES3: program link failed: %s", log);
        return -1;
    }

    glDeleteShader(vs);
    glDeleteShader(fs);

    // Fullscreen quad
    float vertices[] = {
        -1.0f, -1.0f, 0.0f,  0.0f, 0.0f,
         1.0f, -1.0f, 0.0f,  1.0f, 0.0f,
         1.0f,  1.0f, 0.0f,  1.0f, 1.0f,
        -1.0f,  1.0f, 0.0f,  0.0f, 1.0f
    };

    unsigned int indices[] = { 0, 1, 2, 2, 3, 0 };

    glGenVertexArrays(1, &r->vao);
    glGenBuffers(1, &r->vbo);
    glGenBuffers(1, &r->ebo);

    glBindVertexArray(r->vao);

    glBindBuffer(GL_ARRAY_BUFFER, r->vbo);
    glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, r->ebo);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices, GL_STATIC_DRAW);

    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void *)0);
    glEnableVertexAttribArray(0);

    glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void *)(3 * sizeof(float)));
    glEnableVertexAttribArray(1);

    glBindVertexArray(0);

    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glEnable(GL_DEPTH_TEST);

    r->initialized = 1;
    LOGI("GLES3: renderer initialized (%dx%d)", w, h);
    return 0;
}

void gles3_renderer_resize(gles3_renderer_t *r, int w, int h) {
    r->width = w;
    r->height = h;
    glViewport(0, 0, w, h);
}

void gles3_renderer_render(gles3_renderer_t *r) {
    if (!r->initialized) return;

    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glViewport(0, 0, r->width, r->height);

    glUseProgram(r->program);

    GLint loc = glGetUniformLocation(r->program, "uTime");
    if (loc >= 0) {
        extern float monolith_get_time(void);
        glUniform1f(loc, monolith_get_time());
    }

    glBindVertexArray(r->vao);
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    glBindVertexArray(0);
}

void gles3_renderer_destroy(gles3_renderer_t *r) {
    if (!r->initialized) return;

    glDeleteVertexArrays(1, &r->vao);
    glDeleteBuffers(1, &r->vbo);
    glDeleteBuffers(1, &r->ebo);
    glDeleteProgram(r->program);

    r->initialized = 0;
    LOGI("GLES3: renderer destroyed");
}

#else

int gles3_renderer_init(gles3_renderer_t *r, int w, int h) {
    (void)r; (void)w; (void)h; return -1;
}
void gles3_renderer_resize(gles3_renderer_t *r, int w, int h) {
    (void)r; (void)w; (void)h;
}
void gles3_renderer_render(gles3_renderer_t *r) { (void)r; }
void gles3_renderer_destroy(gles3_renderer_t *r) { (void)r; }

#endif
