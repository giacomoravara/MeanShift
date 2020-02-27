//
// Created by giacomo on 31/12/19.
//

#ifndef MSC_POINT_H
#define MSC_POINT_H

class Point {
    double coord[3];
    double mode[3];
    int size = 3;
public:
    Point() {
        coord[0] = 0.0;
        coord[1] = 0.0;
        coord[2] = 0.0;
    }

    Point(double dx, double dy, double dz) {
        coord[0] = dx;
        coord[1] = dy;
        coord[2] = dz;
    }

    Point(double d[]) {
        coord[0] = d[0];
        coord[1] = d[1];
        coord[2] = d[2];
    }

    Point(int dim) {
        size = dim;
        coord[0] = 0.0;
        coord[1] = 0.0;
        coord[2] = 0.0;
    }

    void setSize(int dim) {
        size = dim;
    }

    int getSize() {
        return size;
    }

    void setCoord(int i, double k) {
        this->coord[i] = k;
    }

    double getCoord(int k) {
        return coord[k];
    }

    void setMode(Point *p) {
        mode[0] = p->getCoord(0);
        mode[1] = p->getCoord(1);
        mode[2] = p->getCoord(2);
    }


};

#endif //MSC_POINT_H
