cmake_minimum_required(VERSION 3.22.1)
project(monolith-engine C)

set(CMAKE_C_STANDARD 99)
set(CMAKE_C_STANDARD_REQUIRED ON)
set(MONOLITH_VERSION "0.2.0-stage2")

set(ENGINE_SOURCES
    engine/src/monolith.c
    engine/src/log.c
    engine/src/jni_bridge.c
    engine/src/egl_context.c
    engine/src/gles3_renderer.c
    engine/src/input.c
)

include_directories(engine/include)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -Wextra -Wpedantic")
set(CMAKE_C_FLAGS_RELEASE "${CMAKE_C_FLAGS_RELEASE} -O3 -DNDEBUG")
set(CMAKE_C_FLAGS_DEBUG "${CMAKE_C_FLAGS_DEBUG} -O0 -g -DDEBUG")

add_library(monolith SHARED ${ENGINE_SOURCES})

if(ANDROID)
    find_library(log-lib log)
    find_library(egl-lib EGL)
    find_library(glesv3-lib GLESv3)
    target_link_libraries(monolith ${log-lib} ${egl-lib} ${glesv3-lib})
else()
    message(STATUS "Not on Android, skipping liblog/libEGL/libGLESv3 + using JNI stub")
    include_directories(engine/include/stub)
endif()

set_target_properties(monolith PROPERTIES
    OUTPUT_NAME "monolith"
    VERSION ${MONOLITH_VERSION}
    SOVERSION 0
)
