//
// Created by giacomo on 31/12/19.
//
#include <iostream>
#include "Point.h"

#ifndef MSC_CLUSTER_H
#define MSC_CLUSTER_H

class Cluster {
private:
    Point *mode;

    std::vector<float> coordMode;
public:
    Cluster(Point *mode) : mode(mode), members() {}

    std::vector<int> members;

    void getCoordMode() {
        std::cout << " - Num. elems.: " << this->members.size();
        std::cout << " Mode:" << "";
        for (int i = 0; i < mode->getSize(); i++) {
            std::cout << " " << mode->getCoord(i);

        }
        std::cout << std::endl;
    }

    void setMode(std::vector<Point *> p) {
        for (int k = 0; k < members.size(); k++) {
            p[members[k]]->setMode(mode);
        }
    }

    Point *getMode() {
        return mode;
    }
};

#endif //MSC_CLUSTER_H
