cmake_minimum_required(VERSION 3.22.1)

project("bitperfect-driver")

add_library(
        bitperfect-driver
        SHARED
        driver.cpp
)

find_library(
        log-lib
        log
)

target_link_libraries(
        bitperfect-driver
        ${log-lib}
)
