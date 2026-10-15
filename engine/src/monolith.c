#include "monolith.h"
#include "log.h"

#include <time.h>

static monolith_state_t g_state = MONOLITH_STATE_UNINITIALIZED;
static double g_start_time = 0.0;
static unsigned int g_frame_count = 0;

static double
get_time_sec(void)
{
#ifdef __ANDROID__
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return (double)ts.tv_sec + (double)ts.tv_nsec / 1e9;
#else
    return 0.0;
#endif
}

int
monolith_init(void)
{
    if (g_state != MONOLITH_STATE_UNINITIALIZED) {
        LOGW("monolith_init called but already initialized (state=%d)", g_state);
        return -1;
    }

    g_start_time = get_time_sec();
    g_frame_count = 0;

    LOGI("═══════════════════════════════════════");
    LOGI("  Monolith Engine v%s", MONOLITH_VERSION_STRING);
    LOGI("  Build: %s %s", __DATE__, __TIME__);
    LOGI("═══════════════════════════════════════");

    /* TODO: stage 2 — EGL/GLES init */
    /* TODO: stage 3 — Lua 5.4 init */
    /* TODO: stage 4 — mod loader */

    g_state = MONOLITH_STATE_RUNNING;
    LOGI("Engine initialized successfully.");
    return 0;
}

void
monolith_update(float dt)
{
    if (g_state != MONOLITH_STATE_RUNNING) {
        return;
    }

    /* TODO: stage 8 — player movement/collision */
    /* TODO: stage 5 — chunk load/unload */
    /* TODO: stage 4 — Lua update callback */

    (void)dt;
}

void
monolith_render(void)
{
    if (g_state != MONOLITH_STATE_RUNNING) {
        return;
    }

    g_frame_count++;

    if (g_frame_count % 60 == 0) {
        double elapsed = get_time_sec() - g_start_time;
        LOGD("Frame %u | Elapsed: %.1fs", g_frame_count, elapsed);
    }

    /* TODO: stage 2 — glClearColor + gradient */
}

void
monolith_shutdown(void)
{
    if (g_state == MONOLITH_STATE_SHUTDOWN) {
        return;
    }

    LOGI("Shutting down Monolith Engine...");
    LOGI("Total frames: %u", g_frame_count);

    /* TODO: reverse cleanup — Lua → mods → render → world */

    g_state = MONOLITH_STATE_SHUTDOWN;
    LOGI("Monolith Engine shutdown complete.");
}

monolith_state_t
monolith_get_state(void)
{
    return g_state;
}
