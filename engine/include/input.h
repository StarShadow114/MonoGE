#ifndef INPUT_H
#define INPUT_H

#define INPUT_MAX_EVENTS 256

typedef enum {
    INPUT_EVENT_NONE = 0,
    INPUT_EVENT_TOUCH_DOWN,
    INPUT_EVENT_TOUCH_MOVE,
    INPUT_EVENT_TOUCH_UP,
    INPUT_EVENT_KEY_DOWN,
    INPUT_EVENT_KEY_UP
} input_event_type_t;

typedef struct {
    input_event_type_t type;
    int x;
    int y;
    int keycode;
    double timestamp;
} input_event_t;

typedef struct {
    input_event_t events[INPUT_MAX_EVENTS];
    int head;
    int tail;
    int count;
} input_queue_t;

void input_queue_init(input_queue_t *q);
int  input_queue_push(input_queue_t *q, input_event_t *ev);
int  input_queue_pop(input_queue_t *q, input_event_t *out);
int  input_queue_available(input_queue_t *q);
void input_queue_clear(input_queue_t *q);

// JNI helpers
void input_inject_touch(input_queue_t *q, int action, int x, int y);
void input_inject_key(input_queue_t *q, int action, int keycode);

#endif /* INPUT_H */
