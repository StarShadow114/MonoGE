#include "monolith.h"
#include "egl_context.h"
#include "gles3_renderer.h"
#include "input.h"
#include "log.h"

#include <time.h>

static egl_state_t      g_egl = {0};
static gles3_renderer_t g_renderer = {0};
static int              g_running = 0;
static struct timespec  g_start_time;

float monolith_get_time(void) {
    struct timespec now;
    clock_gettime(CLOCK_MONOTONIC, &now);
    return (float)(now.tv_sec - g_start_time.tv_sec)
         + (float)(now.tv_nsec - g_start_time.tv_nsec) / 1e9f;
}

int monolith_init(void *window, int w, int h) {
    clock_gettime(CLOCK_MONOTONIC, &g_start_time);

    input_queue_init(&g_input);

    if (egl_ctx_init(&g_egl, window) != 0) {
        LOGE("MONOLITH: EGL init failed");
        return -1;
    }

    if (gles3_renderer_init(&g_renderer, w, h) != 0) {
        LOGE("MONOLITH: GLES3 renderer init failed");
        egl_ctx_destroy(&g_egl);
        return -1;
    }

    g_running = 1;
    LOGI("MONOLITH: engine initialized v%s", monolith_get_version());
    return 0;
}

void monolith_resize(int w, int h) {
    egl_ctx_resize(&g_egl, w, h);
    gles3_renderer_resize(&g_renderer, w, h);
}

void monolith_update(float dt) {
    (void)dt;

    input_event_t ev;
    int processed = 0;
    while (input_queue_pop(&g_input, &ev) == 0) {
        processed++;
        // TODO: dispatch to game logic / Lua VM (stage 3+)
    }
    if (processed > 0) {
        LOGD("MONOLITH: processed %d input events", processed);
    }
}

void monolith_render(void) {
    if (!g_running) return;
    gles3_renderer_render(&g_renderer);
    egl_ctx_swap(&g_egl);
}

void monolith_shutdown(void) {
    g_running = 0;
    gles3_renderer_destroy(&g_renderer);
    egl_ctx_destroy(&g_egl);
    input_queue_clear(&g_input);
    LOGI("MONOLITH: engine shutdown");
}

const char *monolith_get_version(void) {
    return "0.2.0-stage2";
}
