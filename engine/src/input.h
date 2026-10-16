#ifndef INPUT_H
#define INPUT_H

#include <stdint.h>

#define INPUT_MAX_EVENTS 256

typedef enum {
    INPUT_EVENT_TOUCH_DOWN = 0,
    INPUT_EVENT_TOUCH_UP   = 1,
    INPUT_EVENT_TOUCH_MOVE = 2,
    INPUT_EVENT_KEY_DOWN   = 3,
    INPUT_EVENT_KEY_UP     = 4,
} input_event_type_t;

typedef struct {
    input_event_type_t type;
    int32_t action;      // touch action or key action
    int32_t x;
    int32_t y;
    int32_t keycode;
} input_event_t;

typedef struct {
    input_event_t events[INPUT_MAX_EVENTS];
    uint32_t head;
    uint32_t tail;
    uint32_t count;
} input_queue_t;

// 全局输入队列
extern input_queue_t g_input;

// 初始化
void input_queue_init(input_queue_t *q);

// 入队
int input_queue_push(input_queue_t *q, input_event_t *ev);

// 出队
int input_queue_pop(input_queue_t *q, input_event_t *out);

// 可用事件数
int input_queue_available(input_queue_t *q);

// 清空
void input_queue_clear(input_queue_t *q);

// 注入触摸事件
void input_inject_touch(input_queue_t *q, int action, int x, int y);

// 注入按键事件
void input_inject_key(input_queue_t *q, int action, int keycode);

#endif // INPUT_H
