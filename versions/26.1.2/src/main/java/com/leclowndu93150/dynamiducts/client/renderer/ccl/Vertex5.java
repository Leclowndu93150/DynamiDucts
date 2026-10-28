/*
 * Derived from CodeChickenLib 4.6.1.526 by ChickenBones and covers1624.
 * https://github.com/TheCBProject/CodeChickenLib
 * Modified for DynamiDucts on 2026-10-26.
 *
 * This file is licensed under the GNU Lesser General Public License v2.1.
 * https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 */

package com.leclowndu93150.dynamiducts.client.renderer.ccl;

public class Vertex5 {

    public final Vector3 vec;
    public final UV uv;

    public Vertex5() {
        this(new Vector3(), new UV());
    }

    public Vertex5(Vector3 vec, UV uv) {
        this.vec = vec;
        this.uv = uv;
    }

    public Vertex5(Vector3 vec, double u, double v) {
        this(vec, new UV(u, v));
    }

    public Vertex5(double x, double y, double z, double u, double v) {
        this(new Vector3(x, y, z), new UV(u, v));
    }

    public Vertex5(Vertex5 vertex) {
        this(vertex.vec.copy(), vertex.uv.copy());
    }

    public Vertex5 set(double x, double y, double z, double u, double v) {
        vec.set(x, y, z);
        uv.u = u;
        uv.v = v;
        return this;
    }

    public Vertex5 apply(Transformation transformation) {
        transformation.apply(vec);
        return this;
    }

    public Vertex5 copy() {
        return new Vertex5(this);
    }
}
