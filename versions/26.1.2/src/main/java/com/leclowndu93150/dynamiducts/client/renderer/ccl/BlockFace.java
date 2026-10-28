/*
 * Derived from CodeChickenLib 4.6.1.526 by ChickenBones and covers1624.
 * https://github.com/TheCBProject/CodeChickenLib
 * Modified for DynamiDucts on 2026-10-26.
 *
 * This file is licensed under the GNU Lesser General Public License v2.1.
 * https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 */

package com.leclowndu93150.dynamiducts.client.renderer.ccl;

public class BlockFace {

    private final Vertex5[] verts = {new Vertex5(), new Vertex5(), new Vertex5(), new Vertex5()};

    public Vertex5[] getVertices() {
        return verts;
    }

    public BlockFace loadCuboidFace(Cuboid6 c, int side) {
        double x1 = c.min.x;
        double x2 = c.max.x;
        double y1 = c.min.y;
        double y2 = c.max.y;
        double z1 = c.min.z;
        double z2 = c.max.z;

        switch (side) {
            case 0 -> {
                verts[0].set(x1, y1, z2, x1, z2);
                verts[1].set(x1, y1, z1, x1, z1);
                verts[2].set(x2, y1, z1, x2, z1);
                verts[3].set(x2, y1, z2, x2, z2);
            }
            case 1 -> {
                verts[0].set(x2, y2, z2, x2, z2);
                verts[1].set(x2, y2, z1, x2, z1);
                verts[2].set(x1, y2, z1, x1, z1);
                verts[3].set(x1, y2, z2, x1, z2);
            }
            case 2 -> {
                verts[0].set(x1, y1, z1, 1 - x1, 1 - y1);
                verts[1].set(x1, y2, z1, 1 - x1, 1 - y2);
                verts[2].set(x2, y2, z1, 1 - x2, 1 - y2);
                verts[3].set(x2, y1, z1, 1 - x2, 1 - y1);
            }
            case 3 -> {
                verts[0].set(x2, y1, z2, x2, 1 - y1);
                verts[1].set(x2, y2, z2, x2, 1 - y2);
                verts[2].set(x1, y2, z2, x1, 1 - y2);
                verts[3].set(x1, y1, z2, x1, 1 - y1);
            }
            case 4 -> {
                verts[0].set(x1, y1, z2, z2, 1 - y1);
                verts[1].set(x1, y2, z2, z2, 1 - y2);
                verts[2].set(x1, y2, z1, z1, 1 - y2);
                verts[3].set(x1, y1, z1, z1, 1 - y1);
            }
            case 5 -> {
                verts[0].set(x2, y1, z1, 1 - z1, 1 - y1);
                verts[1].set(x2, y2, z1, 1 - z1, 1 - y2);
                verts[2].set(x2, y2, z2, 1 - z2, 1 - y2);
                verts[3].set(x2, y1, z2, 1 - z2, 1 - y1);
            }
        }
        return this;
    }
}
