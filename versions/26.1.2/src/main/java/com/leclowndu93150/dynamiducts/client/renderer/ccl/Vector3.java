/*
 * Derived from CodeChickenLib 4.6.1.526 by ChickenBones and covers1624.
 * https://github.com/TheCBProject/CodeChickenLib
 * Modified for DynamiDucts on 2026-10-26.
 *
 * This file is licensed under the GNU Lesser General Public License v2.1.
 * https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 */

package com.leclowndu93150.dynamiducts.client.renderer.ccl;

public class Vector3 {

    public static final Vector3 CENTER = new Vector3(0.5, 0.5, 0.5);

    public double x;
    public double y;
    public double z;

    public Vector3() {
    }

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector3 vec) {
        this(vec.x, vec.y, vec.z);
    }

    public Vector3 set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3 set(Vector3 vec) {
        return set(vec.x, vec.y, vec.z);
    }

    public Vector3 add(double dx, double dy, double dz) {
        x += dx;
        y += dy;
        z += dz;
        return this;
    }

    public Vector3 add(Vector3 vec) {
        return add(vec.x, vec.y, vec.z);
    }

    public Vector3 subtract(double dx, double dy, double dz) {
        x -= dx;
        y -= dy;
        z -= dz;
        return this;
    }

    public Vector3 subtract(Vector3 vec) {
        return subtract(vec.x, vec.y, vec.z);
    }

    public Vector3 multiply(double fx, double fy, double fz) {
        x *= fx;
        y *= fy;
        z *= fz;
        return this;
    }

    public Vector3 multiply(double f) {
        return multiply(f, f, f);
    }

    public Vector3 multiply(Vector3 f) {
        return multiply(f.x, f.y, f.z);
    }

    public Vector3 negate() {
        x = -x;
        y = -y;
        z = -z;
        return this;
    }

    public Vector3 normalize() {
        double length = Math.sqrt(x * x + y * y + z * z);
        if (length != 0) {
            multiply(1 / length);
        }
        return this;
    }

    public Vector3 crossProduct(Vector3 vec) {
        double cx = y * vec.z - z * vec.y;
        double cy = z * vec.x - x * vec.z;
        double cz = x * vec.y - y * vec.x;
        x = cx;
        y = cy;
        z = cz;
        return this;
    }

    public double getSide(int side) {
        return switch (side) {
            case 0, 1 -> y;
            case 2, 3 -> z;
            case 4, 5 -> x;
            default -> throw new IndexOutOfBoundsException("Invalid side " + side);
        };
    }

    public Vector3 setSide(int side, double value) {
        switch (side) {
            case 0, 1 -> y = value;
            case 2, 3 -> z = value;
            case 4, 5 -> x = value;
            default -> throw new IndexOutOfBoundsException("Invalid side " + side);
        }
        return this;
    }

    public boolean equalsT(Vector3 vec) {
        return Math.abs(x - vec.x) <= 1E-5 && Math.abs(y - vec.y) <= 1E-5 && Math.abs(z - vec.z) <= 1E-5;
    }

    public Vector3 copy() {
        return new Vector3(this);
    }
}
