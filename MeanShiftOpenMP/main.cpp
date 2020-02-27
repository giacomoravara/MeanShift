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
#include <cmath>

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


    int NumThreads;
    double band;
    std::cout << "Insert bandwidth: " << std::endl;
    std::cin >> band;
    std::cout << "Insert Max Number of Threads: " << std::endl;
    std::cin >> NumThreads;

    MeanShift *m = new MeanShift(v, band);

    const auto t0 = omp_get_wtime();//std::chrono::high_resolution_clock::now();
    auto clusters = m->mean_shift_cluster_seq(100.0, 100);
    const auto t1 = omp_get_wtime();//std::chrono::high_resolution_clock::now();

    std::cerr << "Elapsed time of sequential version: " << (t1 - t0) << " s" << std::endl;
    for (int th = 2; th < NumThreads; th++) {
        double time = 0;
        MeanShift *s = new MeanShift(v, band);
        const auto t2 = omp_get_wtime();//std::chrono::high_resolution_clock::now();
        auto clusters_par = s->mean_shift_cluster(100.0, 100, th, false);
        const auto t3 = omp_get_wtime();//std::chrono::high_resolution_clock::now();
        time = (t3 - t2);
        std::cerr << "Elapsed time of parallel version: " << time << " s" << std::endl;
        std::cerr << "Speedup with " << th << " threads: " << (float) ((t1 - t0) / time) << "X" << std::endl;
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

