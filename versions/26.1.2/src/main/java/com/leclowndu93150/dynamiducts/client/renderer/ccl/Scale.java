/*
 * Derived from CodeChickenLib 4.6.1.526 by ChickenBones and covers1624.
 * https://github.com/TheCBProject/CodeChickenLib
 * Modified for DynamiDucts on 2026-10-26.
 *
 * This file is licensed under the GNU Lesser General Public License v2.1.
 * https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 */

package com.leclowndu93150.dynamiducts.client.renderer.ccl;

public class Scale extends Transformation {

    public final Vector3 factor;

    public Scale(double x, double y, double z) {
        this.factor = new Vector3(x, y, z);
    }

    @Override
    public void apply(Vector3 vec) {
        vec.multiply(factor);
    }

    @Override
    public void applyN(Vector3 normal) {
    }
}
