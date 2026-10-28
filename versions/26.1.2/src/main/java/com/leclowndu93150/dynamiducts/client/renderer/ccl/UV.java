/*
 * Derived from CodeChickenLib 4.6.1.526 by ChickenBones and covers1624.
 * https://github.com/TheCBProject/CodeChickenLib
 * Modified for DynamiDucts on 2026-10-26.
 *
 * This file is licensed under the GNU Lesser General Public License v2.1.
 * https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 */

package com.leclowndu93150.dynamiducts.client.renderer.ccl;

public class UV {

    public double u;
    public double v;

    public UV() {
    }

    public UV(double u, double v) {
        this.u = u;
        this.v = v;
    }

    public UV(UV uv) {
        this(uv.u, uv.v);
    }

    public UV set(UV uv) {
        u = uv.u;
        v = uv.v;
        return this;
    }

    public UV add(UV uv) {
        u += uv.u;
        v += uv.v;
        return this;
    }

    public UV multiply(double d) {
        u *= d;
        v *= d;
        return this;
    }

    public UV copy() {
        return new UV(this);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof UV uv && u == uv.u && v == uv.v;
    }

    @Override
    public int hashCode() {
        return 31 * Double.hashCode(u) + Double.hashCode(v);
    }
}
