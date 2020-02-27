#include <vector>
#include <memory>
#include <string>
#include <sstream>
#include <fstream>
#include <istream>
#include <iostream>
#include <chrono>
#include "Point.h"
#include "Cluster.h"
#include "MeanShift.h"
#include "lodepng.h"

#ifdef _WIN32
#include <io.h>
#else


#endif

typedef unsigned char byte;

static bool loadImage(const std::string &filename);

static std::vector<byte> pixels;
static unsigned width;
static unsigned height;


int main() {


    std::ifstream infile;

    std::cout << "Input image: ";
    std::string filename;
    std::cin >> filename;
    infile.open(filename);
    loadImage(filename);
    std::vector<Point *> v;
    std::vector<byte> outputImage;
    unsigned outputWidth;
    unsigned outputHeight;
    outputImage.clear();
    outputWidth = width;
    outputHeight = height;
    outputImage.reserve(outputWidth * outputHeight);

    for (auto k = 0; k < pixels.size() / 3; k++) {
        Point *p = new Point();
        for (int i = 0; i < 3; i++) {
            p->setCoord(i, double(pixels[(k * 3) + i]));
        }
        v.emplace_back(p);
    }


    double band[] = {1.0, 25.0, 500.0};
    for (int i = 0; i < 3; i++) {
        std::cerr << "Bandwidth = " << band[i] << std::endl;
        MeanShift *m = new MeanShift(v, band[i]);
        MeanShift *s = new MeanShift(v, band[i]);

        const auto t0 = omp_get_wtime();// std::chrono::high_resolution_clock::now();
        auto clusters = m->mean_shift_cluster_seq(5000.0, 100);
        const auto t1 = omp_get_wtime();//std::chrono::high_resolution_clock::now();
        m->~MeanShift();

        std::cerr << "Elapsed time sequential: "
                  << (t1 - t0) << " s" << std::endl;
        for (int th = 2; th < 13; th++) {
            double time = 0;
            //for (int x = 0; x < 5; x++) {
            const auto t2 = omp_get_wtime();//std::chrono::high_resolution_clock::now();
            auto clusters_par = s->mean_shift_cluster(5000.0, 100, th, false);
            const auto t3 = omp_get_wtime();//std::chrono::high_resolution_clock::now();
            time = (t3 - t2);
            //}
            std::cerr << "Elapsed time parallel: " << (time) << " s" << std::endl;
            std::cerr << "Speedup with " << th << " threads: "
                      << (float) ((t1 - t0) / (time)) << "X" << std::endl;

        }
        std::cerr << "Test Finished!" << std::endl;


    }


    pixels.clear();

    return 0;
}


static bool loadImage(const std::string &filename) {
    unsigned error = lodepng::decode(pixels, width, height, filename, LCT_RGB);
    if (error) {
        std::cerr << lodepng_error_text(error) << std::endl;
        return false;
    }
    //  std::cout << "image size is " << width << "x" << height << std::endl;
    return true;
}
//
// Created by giacomo on 25/01/20.
//



