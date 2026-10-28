/*
 * Derived from CodeChickenLib 4.6.1.526 by ChickenBones and covers1624.
 * https://github.com/TheCBProject/CodeChickenLib
 * Modified for DynamiDucts on 2026-10-26.
 *
 * This file is licensed under the GNU Lesser General Public License v2.1.
 * https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 */

package com.leclowndu93150.dynamiducts.client.renderer.ccl;

public abstract class Rotation extends Transformation {

    public static final Rotation[] sideRotations = {
            new Rotation() {
                @Override
                public void apply(Vector3 vec) {
                }

                @Override
                public Rotation inverse() {
                    return this;
                }
            },
            new Rotation() {
                @Override
                public void apply(Vector3 vec) {
                    vec.y = -vec.y;
                    vec.z = -vec.z;
                }

                @Override
                public Rotation inverse() {
                    return this;
                }
            },
            new Rotation() {
                @Override
                public void apply(Vector3 vec) {
                    double y = vec.y;
                    double z = vec.z;
                    vec.y = -z;
                    vec.z = y;
                }

                @Override
                public Rotation inverse() {
                    return sideRotations[3];
                }
            },
            new Rotation() {
                @Override
                public void apply(Vector3 vec) {
                    double y = vec.y;
                    double z = vec.z;
                    vec.y = z;
                    vec.z = -y;
                }

                @Override
                public Rotation inverse() {
                    return sideRotations[2];
                }
            },
            new Rotation() {
                @Override
                public void apply(Vector3 vec) {
                    double x = vec.x;
                    double y = vec.y;
                    vec.x = y;
                    vec.y = -x;
                }

                @Override
                public Rotation inverse() {
                    return sideRotations[5];
                }
            },
            new Rotation() {
                @Override
                public void apply(Vector3 vec) {
                    double x = vec.x;
                    double y = vec.y;
                    vec.x = -y;
                    vec.y = x;
                }

                @Override
                public Rotation inverse() {
                    return sideRotations[4];
                }
            }
    };

    @Override
    public void applyN(Vector3 normal) {
        apply(normal);
    }

    public abstract Rotation inverse();
}
