/*
 * Derived from CodeChickenLib 4.6.1.526 by ChickenBones and covers1624.
 * https://github.com/TheCBProject/CodeChickenLib
 * Modified for DynamiDucts on 2026-10-26.
 *
 * This file is licensed under the GNU Lesser General Public License v2.1.
 * https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 */

package com.leclowndu93150.dynamiducts.client.renderer.ccl;

public class CCModel {

    private static final int[] BACKFACE_ORDER = {0, 3, 2, 1};

    public final Vertex5[] verts;
    Vector3[] normals;

    private CCModel(int numVerts) {
        verts = new Vertex5[numVerts];
    }

    public static CCModel quadModel(int numVerts) {
        return new CCModel(numVerts);
    }

    public CCModel generateBox(int i, double x1, double y1, double z1, double w, double h, double d, double tx, double ty, double tw, double th, double f) {
        double u1;
        double v1;
        double u2;
        double v2;
        double x2 = x1 + w;
        double y2 = y1 + h;
        double z2 = z1 + d;
        x1 /= f;
        x2 /= f;
        y1 /= f;
        y2 /= f;
        z1 /= f;
        z2 /= f;

        u1 = (tx + d + w) / tw;
        v1 = (ty + d) / th;
        u2 = (tx + d * 2 + w) / tw;
        v2 = ty / th;
        verts[i++] = new Vertex5(x1, y1, z2, u1, v2);
        verts[i++] = new Vertex5(x1, y1, z1, u1, v1);
        verts[i++] = new Vertex5(x2, y1, z1, u2, v1);
        verts[i++] = new Vertex5(x2, y1, z2, u2, v2);

        u1 = (tx + d) / tw;
        v1 = (ty + d) / th;
        u2 = (tx + d + w) / tw;
        v2 = ty / th;
        verts[i++] = new Vertex5(x2, y2, z2, u2, v2);
        verts[i++] = new Vertex5(x2, y2, z1, u2, v1);
        verts[i++] = new Vertex5(x1, y2, z1, u1, v1);
        verts[i++] = new Vertex5(x1, y2, z2, u1, v2);

        u1 = (tx + d + w) / tw;
        v1 = (ty + d) / th;
        u2 = (tx + d) / tw;
        v2 = (ty + d + h) / th;
        verts[i++] = new Vertex5(x1, y2, z1, u2, v1);
        verts[i++] = new Vertex5(x2, y2, z1, u1, v1);
        verts[i++] = new Vertex5(x2, y1, z1, u1, v2);
        verts[i++] = new Vertex5(x1, y1, z1, u2, v2);

        u1 = (tx + d * 2 + w * 2) / tw;
        v1 = (ty + d) / th;
        u2 = (tx + d * 2 + w) / tw;
        v2 = (ty + d + h) / th;
        verts[i++] = new Vertex5(x1, y2, z2, u1, v1);
        verts[i++] = new Vertex5(x1, y1, z2, u1, v2);
        verts[i++] = new Vertex5(x2, y1, z2, u2, v2);
        verts[i++] = new Vertex5(x2, y2, z2, u2, v1);

        u1 = (tx + d) / tw;
        v1 = (ty + d) / th;
        u2 = tx / tw;
        v2 = (ty + d + h) / th;
        verts[i++] = new Vertex5(x1, y2, z2, u2, v1);
        verts[i++] = new Vertex5(x1, y2, z1, u1, v1);
        verts[i++] = new Vertex5(x1, y1, z1, u1, v2);
        verts[i++] = new Vertex5(x1, y1, z2, u2, v2);

        u1 = (tx + d * 2 + w) / tw;
        v1 = (ty + d) / th;
        u2 = (tx + d + w) / tw;
        v2 = (ty + d + h) / th;
        verts[i++] = new Vertex5(x2, y1, z2, u1, v2);
        verts[i++] = new Vertex5(x2, y1, z1, u2, v2);
        verts[i++] = new Vertex5(x2, y2, z1, u2, v1);
        verts[i] = new Vertex5(x2, y2, z2, u1, v1);

        return this;
    }

    public CCModel generateBlock(int i, double x1, double y1, double z1, double x2, double y2, double z2) {
        return generateBlock(i, new Cuboid6(x1, y1, z1, x2, y2, z2));
    }

    public CCModel generateBlock(int i, Cuboid6 bounds) {
        BlockFace face = new BlockFace();
        for (int side = 0; side < 6; side++) {
            for (Vertex5 vertex : face.loadCuboidFace(bounds, side).getVertices()) {
                verts[i++] = vertex.copy();
            }
        }
        return this;
    }

    public CCModel computeNormals() {
        if (normals == null) {
            normals = new Vector3[verts.length];
        }
        for (int i = 0; i < verts.length; i += 4) {
            Vector3 diff1 = verts[i + 1].vec.copy().subtract(verts[i].vec);
            Vector3 diff2 = verts[i + 3].vec.copy().subtract(verts[i].vec);
            normals[i] = diff1.crossProduct(diff2).normalize();
            for (int d = 1; d < 4; d++) {
                normals[i + d] = normals[i].copy();
            }
        }
        return this;
    }

    public CCModel apply(Transformation transformation) {
        for (Vertex5 vert : verts) {
            vert.apply(transformation);
        }
        if (normals != null) {
            for (Vector3 normal : normals) {
                transformation.applyN(normal);
            }
        }
        return this;
    }

    public CCModel shrinkUVs(double d) {
        for (int k = 0; k < verts.length; k += 4) {
            UV center = new UV();
            for (int i = 0; i < 4; i++) {
                center.add(verts[k + i].uv);
            }
            center.multiply(0.25);
            for (int i = 0; i < 4; i++) {
                UV uv = verts[k + i].uv;
                uv.u += uv.u < center.u ? d : -d;
                uv.v += uv.v < center.v ? d : -d;
            }
        }
        return this;
    }

    public CCModel sidedCopy(int side1, int side2) {
        return copy().apply(Rotation.sideRotations[side1].inverse()).apply(Rotation.sideRotations[side2]);
    }

    public static void generateSidedModels(CCModel[] models, int side) {
        for (int s = 0; s < 6; s++) {
            if (s != side) {
                models[s] = models[side].sidedCopy(side, s);
            }
        }
    }

    public CCModel backfacedCopy() {
        return generateBackface(this, 0, copy(), 0, verts.length);
    }

    public CCModel twoFacedCopy() {
        CCModel model = quadModel(verts.length * 2);
        copy(this, 0, model, 0, verts.length);
        return generateBackface(model, 0, model, verts.length, verts.length);
    }

    public CCModel copy() {
        CCModel model = quadModel(verts.length);
        copy(this, 0, model, 0, verts.length);
        return model;
    }

    public static void copy(CCModel src, int srcPos, CCModel dst, int destPos, int length) {
        for (int k = 0; k < length; k++) {
            dst.verts[destPos + k] = src.verts[srcPos + k].copy();
        }
        if (src.normals != null) {
            for (int k = 0; k < length; k++) {
                copyNormal(src, srcPos + k, dst, destPos + k);
            }
        }
    }

    public static CCModel generateBackface(CCModel src, int srcPos, CCModel dst, int destPos, int length) {
        for (int i = 0; i < length; i++) {
            int base = (i / 4) * 4;
            int si = srcPos + base + i % 4;
            int di = destPos + base + BACKFACE_ORDER[i % 4];
            dst.verts[di] = src.verts[si].copy();
            if (src.normals != null) {
                copyNormal(src, si, dst, di);
            }
            if (dst.normals != null && dst.normals[di] != null) {
                dst.normals[di].negate();
            }
        }
        return dst;
    }

    private static void copyNormal(CCModel src, int srcIndex, CCModel dst, int destIndex) {
        if (dst.normals == null) {
            dst.normals = new Vector3[dst.verts.length];
        }
        Vector3 normal = src.normals[srcIndex];
        dst.normals[destIndex] = normal == null ? null : normal.copy();
    }

    public void render(CCRenderState state, Transformation transformation, IconTransformation icon) {
        render(state, 0, verts.length, transformation, icon);
    }

    public void render(CCRenderState state, int start, int end, Transformation transformation, IconTransformation icon) {
        state.render(this, start, end, transformation, icon);
    }
}
