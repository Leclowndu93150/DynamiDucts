/*
 * Derived from CodeChickenLib 4.6.1.526 by ChickenBones and covers1624.
 * https://github.com/TheCBProject/CodeChickenLib
 * Modified for DynamiDucts on 2026-10-26.
 *
 * This file is licensed under the GNU Lesser General Public License v2.1.
 * https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 */

package com.leclowndu93150.dynamiducts.client.renderer.ccl;

public class Cuboid6 {

    public final Vector3 min;
    public final Vector3 max;

    public Cuboid6(Vector3 min, Vector3 max) {
        this.min = min;
        this.max = max;
    }

    public Cuboid6(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this(new Vector3(minX, minY, minZ), new Vector3(maxX, maxY, maxZ));
    }

    public Cuboid6(Cuboid6 cuboid) {
        this(cuboid.min.copy(), cuboid.max.copy());
    }

    public Cuboid6 add(Vector3 vec) {
        min.add(vec);
        max.add(vec);
        return this;
    }

    public Cuboid6 expand(double d) {
        min.subtract(d, d, d);
        max.add(d, d, d);
        return this;
    }

    public Cuboid6 apply(Transformation transformation) {
        transformation.apply(min);
        transformation.apply(max);
        double temp;
        if (min.x > max.x) {
            temp = min.x;
            min.x = max.x;
            max.x = temp;
        }
        if (min.y > max.y) {
            temp = min.y;
            min.y = max.y;
            max.y = temp;
        }
        if (min.z > max.z) {
            temp = min.z;
            min.z = max.z;
            max.z = temp;
        }
        return this;
    }

    public Cuboid6 copy() {
        return new Cuboid6(this);
    }
}
