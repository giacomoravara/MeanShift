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

    for (int N=2; N<NumThreads; N++ ) {
        MeanShift *m = new MeanShift(v, band);
        MeanShift *s = new MeanShift(v, band);
        const auto t2 = omp_get_wtime(); //std::chrono::high_resolution_clock::now();
        auto clusters_par_dynamic = s->mean_shift_cluster(100.0, 100, N, false);
        const auto t3 = omp_get_wtime(); //std::chrono::high_resolution_clock::now();
        const auto t0 = omp_get_wtime(); //std::chrono::high_resolution_clock::now();
        auto clusters_par_static = m->mean_shift_cluster(100.0, 100, N, true);
        const auto t1 = omp_get_wtime(); //std::chrono::high_resolution_clock::now();


        std::cerr << "Numero Threads: " << N << std::endl;
        std::cerr << "Elapsed time static parallel: " << (t1 - t0) << " s" << std::endl;
        std::cerr << "Elapsed time dynamic parallel: " << (t3 - t2) << " s" << std::endl;
        std::cerr << "Speedup: " << (float) ((t1 - t0) / (t3 - t2)) << "X" << std::endl;
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
// Created by giacomo on 07/02/20.
//

