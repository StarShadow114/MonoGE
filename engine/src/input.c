#include "input.h"
#include <string.h>
#include "log.h"

input_queue_t g_input;

void input_queue_init(input_queue_t *q) {
    memset(q, 0, sizeof(input_queue_t));
}

int input_queue_push(input_queue_t *q, input_event_t *ev) {
    if (q->count >= INPUT_MAX_EVENTS) {
        LOGW("INPUT: queue full, dropping event");
        return -1;
    }

    q->events[q->tail] = *ev;
    q->tail = (q->tail + 1) % INPUT_MAX_EVENTS;
    q->count++;
    return 0;
}

int input_queue_pop(input_queue_t *q, input_event_t *out) {
    if (q->count == 0) return -1;

    *out = q->events[q->head];
    q->head = (q->head + 1) % INPUT_MAX_EVENTS;
    q->count--;
    return 0;
}

int input_queue_available(input_queue_t *q) {
    return q->count;
}

void input_queue_clear(input_queue_t *q) {
    q->head = 0;
    q->tail = 0;
    q->count = 0;
}

void input_inject_touch(input_queue_t *q, int action, int x, int y) {
    input_event_t ev;
    memset(&ev, 0, sizeof(ev));

    switch (action) {
        case 0: ev.type = INPUT_EVENT_TOUCH_DOWN; break;
        case 1: ev.type = INPUT_EVENT_TOUCH_MOVE; break;
        case 2: ev.type = INPUT_EVENT_TOUCH_UP; break;
        default: return;
    }

    ev.x = x;
    ev.y = y;
    input_queue_push(q, &ev);
}

void input_inject_key(input_queue_t *q, int action, int keycode) {
    input_event_t ev;
    memset(&ev, 0, sizeof(ev));

    ev.type = (action == 0) ? INPUT_EVENT_KEY_DOWN : INPUT_EVENT_KEY_UP;
    ev.keycode = keycode;
    input_queue_push(q, &ev);
}

#ifdef __GNUC__
__attribute__((constructor))
#endif
static void input_global_init(void) {
    input_queue_init(&g_input);
}
