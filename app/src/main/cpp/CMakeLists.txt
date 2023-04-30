cmake_minimum_required(VERSION 3.4.1)

add_library(
        PortfolioPulsar

        SHARED

        native-lib.cpp
        portfolio_jni.cpp
        stock.cpp
        portfolio.cpp)

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

target_link_libraries( # Specifies the target library.
        PortfolioPulsar

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib} )
