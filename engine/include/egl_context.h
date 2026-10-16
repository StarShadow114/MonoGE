#ifndef EGL_CONTEXT_H
#define EGL_CONTEXT_H

#ifdef __ANDROID__
#include <EGL/egl.h>
#include <EGL/eglext.h>
#endif

typedef struct {
#ifdef __ANDROID__
    EGLDisplay display;
    EGLSurface surface;
    EGLContext context;
    EGLConfig config;
#endif
    int width;
    int height;
    int initialized;
} egl_state_t;

int  egl_ctx_init(egl_state_t *state, void *window);
void egl_ctx_swap(egl_state_t *state);
void egl_ctx_destroy(egl_state_t *state);
void egl_ctx_resize(egl_state_t *state, int w, int h);

#endif /* EGL_CONTEXT_H */
