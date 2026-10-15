cmake_minimum_required(VERSION 3.22.1)
project(monolith-engine C)

set(CMAKE_C_STANDARD 99)
set(CMAKE_C_STANDARD_REQUIRED ON)
set(MONOLITH_VERSION "0.1.0")

set(ENGINE_SOURCES
    engine/src/monolith.c
    engine/src/log.c
    engine/src/jni_bridge.c
)

include_directories(engine/include)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -Wextra -Wpedantic")
set(CMAKE_C_FLAGS_RELEASE "${CMAKE_C_FLAGS_RELEASE} -O3 -DNDEBUG")
set(CMAKE_C_FLAGS_DEBUG "${CMAKE_C_FLAGS_DEBUG} -O0 -g -DDEBUG")

add_library(monolith SHARED ${ENGINE_SOURCES})

# 只在 Android 上链 liblog
if(ANDROID)
    find_library(log-lib log)
    target_link_libraries(monolith ${log-lib})
else()
    message(STATUS "Not on Android, skipping liblog")
endif()

set_target_properties(monolith PROPERTIES
    OUTPUT_NAME "monolith"
    VERSION ${MONOLITH_VERSION}
    SOVERSION 0
)
