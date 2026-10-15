#ifndef MONOLITH_H
#define MONOLITH_H

#ifdef __ANDROID__
#include <android/log.h>
#endif

#define MONOLITH_VERSION_MAJOR 0
#define MONOLITH_VERSION_MINOR 1
#define MONOLITH_VERSION_PATCH 0
#define MONOLITH_VERSION_STRING "0.1.0"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum {
    MONOLITH_STATE_UNINITIALIZED = 0,
    MONOLITH_STATE_RUNNING,
    MONOLITH_STATE_PAUSED,
    MONOLITH_STATE_SHUTDOWN
} monolith_state_t;

int  monolith_init(void);
void monolith_update(float dt);
void monolith_render(void);
void monolith_shutdown(void);
monolith_state_t monolith_get_state(void);

#ifdef __cplusplus
}
#endif

#endif // MONOLITH_H
