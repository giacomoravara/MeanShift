cmake_minimum_required(VERSION 3.15)
project(MeanShift)

set(CMAKE_CXX_STANDARD 11)
find_package(OpenMP)
add_executable(MeanShift main.cpp lodepng.h lodepng.cpp)
add_executable(TestBand test_bandwidth.cpp  lodepng.h lodepng.cpp)
add_executable(TestDynamic testdynamicstatic.cpp lodepng.h lodepng.cpp)

if (OPENMP_FOUND)
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} ${OpenMP_C_FLAGS}")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${OpenMP_CXX_FLAGS} -std=c++11 -fopenmp")
endif()