#include "egl_context.h"
#include "log.h"

#ifdef __ANDROID__

int egl_ctx_init(egl_state_t *state, void *window) {
    state->display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (state->display == EGL_NO_DISPLAY) {
        LOGE("EGL: eglGetDisplay failed");
        return -1;
    }

    if (eglInitialize(state->display, NULL, NULL) != EGL_TRUE) {
        LOGE("EGL: eglInitialize failed");
        return -1;
    }

    EGLint attribs[] = {
        EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
        EGL_RENDERABLE_TYPE, EGL_OPENGL_ES3_BIT_KHR,
        EGL_RED_SIZE, 8,
        EGL_GREEN_SIZE, 8,
        EGL_BLUE_SIZE, 8,
        EGL_ALPHA_SIZE, 8,
        EGL_DEPTH_SIZE, 24,
        EGL_STENCIL_SIZE, 8,
        EGL_NONE
    };

    EGLint num_configs;
    if (eglChooseConfig(state->display, attribs, NULL, 0, &num_configs) != EGL_TRUE) {
        LOGE("EGL: eglChooseConfig(count) failed");
        return -1;
    }

    EGLConfig *configs = (EGLConfig *)malloc(num_configs * sizeof(EGLConfig));
    eglChooseConfig(state->display, attribs, configs, num_configs, &num_configs);

    state->config = configs[0];
    free(configs);

    EGLint format;
    eglGetConfigAttrib(state->display, state->config, EGL_NATIVE_VISUAL_ID, &format);

    state->surface = eglCreateWindowSurface(state->display, state->config, (EGLNativeWindowType)window, NULL);
    if (state->surface == EGL_NO_SURFACE) {
        LOGE("EGL: eglCreateWindowSurface failed");
        return -1;
    }

    EGLint ctx_attribs[] = {
        EGL_CONTEXT_CLIENT_VERSION, 3,
        EGL_NONE
    };

    state->context = eglCreateContext(state->display, state->config, EGL_NO_CONTEXT, ctx_attribs);
    if (state->context == EGL_NO_CONTEXT) {
        LOGE("EGL: eglCreateContext failed (GLES3 not supported?)");
        return -1;
    }

    if (eglMakeCurrent(state->display, state->surface, state->surface, state->context) != EGL_TRUE) {
        LOGE("EGL: eglMakeCurrent failed");
        return -1;
    }

    eglQuerySurface(state->display, state->surface, EGL_WIDTH, &state->width);
    eglQuerySurface(state->display, state->surface, EGL_HEIGHT, &state->height);

    state->initialized = 1;
    LOGI("EGL: initialized %dx%d, GLES3 context created", state->width, state->height);
    return 0;
}

void egl_ctx_swap(egl_state_t *state) {
    if (!state->initialized) return;
    eglSwapBuffers(state->display, state->surface);
}

void egl_ctx_destroy(egl_state_t *state) {
    if (!state->initialized) return;

    eglMakeCurrent(state->display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);

    if (state->surface != EGL_NO_SURFACE) {
        eglDestroySurface(state->display, state->surface);
        state->surface = EGL_NO_SURFACE;
    }

    if (state->context != EGL_NO_CONTEXT) {
        eglDestroyContext(state->display, state->context);
        state->context = EGL_NO_CONTEXT;
    }

    eglTerminate(state->display);
    state->display = EGL_NO_DISPLAY;
    state->initialized = 0;
    LOGI("EGL: destroyed");
}

void egl_ctx_resize(egl_state_t *state, int w, int h) {
    state->width = w;
    state->height = h;
}

#else

int egl_ctx_init(egl_state_t *state, void *window) {
    (void)state; (void)window;
    return -1;
}
void egl_ctx_swap(egl_state_t *state) { (void)state; }
void egl_ctx_destroy(egl_state_t *state) { (void)state; }
void egl_ctx_resize(egl_state_t *state, int w, int h) { (void)state; (void)w; (void)h; }

#endif
