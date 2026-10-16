#ifndef GLES3_RENDERER_H
#define GLES3_RENDERER_H

#ifdef __ANDROID__
#include <GLES3/gl3.h>
#endif

typedef struct {
#ifdef __ANDROID__
    GLuint program;
    GLuint vao;
    GLuint vbo;
    GLuint ebo;
#endif
    int width;
    int height;
    int initialized;
} gles3_renderer_t;

int  gles3_renderer_init(gles3_renderer_t *r, int w, int h);
void gles3_renderer_resize(gles3_renderer_t *r, int w, int h);
void gles3_renderer_render(gles3_renderer_t *r);
void gles3_renderer_destroy(gles3_renderer_t *r);

#endif /* GLES3_RENDERER_H */
