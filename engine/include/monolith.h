#ifndef MONOLITH_H
#define MONOLITH_H

#ifdef __cplusplus
extern "C" {
#endif

float monolith_get_time(void);
int   monolith_init(void *window, int w, int h);
void  monolith_resize(int w, int h);
void  monolith_update(float dt);
void  monolith_render(void);
void  monolith_shutdown(void);
const char *monolith_get_version(void);

#ifdef __cplusplus
}
#endif

#endif /* MONOLITH_H */
