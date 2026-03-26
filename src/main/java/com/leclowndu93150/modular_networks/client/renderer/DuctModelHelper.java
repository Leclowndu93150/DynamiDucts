package com.leclowndu93150.modular_networks.client.renderer;

import codechicken.lib.render.BlockRenderer;
import codechicken.lib.render.CCModel;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.Vertex5;
import codechicken.lib.vec.uv.UV;
import com.mojang.blaze3d.vertex.VertexFormat;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

final class DuctModelHelper {

    private static final double RENDER_OFFSET = 1.0 / 1024.0;
    private static final BlockRenderer.BlockFace FACE = new BlockRenderer.BlockFace();

    private static final Vector3[] AXES = {
            new Vector3(0, -1, 0),
            new Vector3(0, 1, 0),
            new Vector3(0, 0, -1),
            new Vector3(0, 0, 1),
            new Vector3(-1, 0, 0),
            new Vector3(1, 0, 0)
    };

    private static final int[][] ORTHOGONALS = {
            {6, 6, 4, 5, 2, 3},
            {6, 6, 4, 5, 2, 3},
            {4, 5, 6, 6, 0, 1},
            {5, 4, 6, 6, 1, 0},
            {2, 3, 0, 1, 6, 6},
            {3, 2, 1, 0, 6, 6}
    };

    private static final int[][] EDGE_PAIRS = {
            {0, 2}, {0, 3}, {0, 4}, {0, 5},
            {1, 2}, {1, 3}, {1, 4}, {1, 5},
            {2, 4}, {2, 5}, {3, 4}, {3, 5}
    };

    private static final int[][] CORNER_TRIPLETS = {
            {0, 2, 4}, {0, 2, 5}, {0, 3, 4}, {0, 3, 5},
            {1, 2, 4}, {1, 2, 5}, {1, 3, 4}, {1, 3, 5}
    };

    private static final int[][] ORTHOG_AXES = {
            {2, 4}, {2, 4}, {0, 4}, {0, 4}, {0, 2}, {0, 2}
    };

    private DuctModelHelper() {
    }

    static void finalizeModel(CCModel model) {
        model.computeNormals().shrinkUVs(RENDER_OFFSET);
    }

    static LinkedList<Vertex5> addSideFaces(LinkedList<Vertex5> vecs, Cuboid6 bounds, int sideMask) {
        for (int side = 0; side < 6; side++) {
            if ((sideMask & (1 << side)) == 0) {
                addSideFace(vecs, bounds, side);
            }
        }
        return vecs;
    }

    static LinkedList<Vertex5> addSideFace(LinkedList<Vertex5> vecs, Cuboid6 bounds, int side) {
        FACE.loadCuboidFace(bounds.copy().add(Vector3.CENTER), side);
        for (Vertex5 vertex : FACE.getVertices()) {
            vecs.add(new Vertex5(vertex.vec.copy().subtract(Vector3.CENTER), vertex.uv.copy()));
        }
        return vecs;
    }

    static LinkedList<Vertex5> apply(LinkedList<Vertex5> vertices, Transformation transform) {
        LinkedList<Vertex5> transformed = new LinkedList<>();
        for (Vertex5 vertex : vertices) {
            transformed.add(vertex.copy().apply(transform));
        }
        return transformed;
    }

    static LinkedList<Vertex5> simplifyModel(LinkedList<Vertex5> vertices) {
        LinkedList<Face> faces = new LinkedList<>();
        Iterator<Vertex5> iter = vertices.iterator();
        while (iter.hasNext()) {
            Face face = Face.loadFromIterator(iter);
            faces.removeIf(face::attemptToCombine);
            faces.add(face);
        }

        LinkedList<Vertex5> out = new LinkedList<>();
        for (Face face : faces) {
            Collections.addAll(out, face.verts);
        }
        return out;
    }

    static final class OctagonalTubeGen {

        private final double size;
        private final double innerSize;
        private final boolean frameOnly;
        private final Vector3[] octoFace;

        OctagonalTubeGen(double size, double innerSize, boolean frameOnly) {
            this.size = size;
            this.innerSize = innerSize;
            this.frameOnly = frameOnly;
            this.octoFace = new Vector3[8];

            octoFace[0] = new Vector3(-size, -0.5, -innerSize);
            octoFace[1] = new Vector3(-innerSize, -0.5, -size);
            octoFace[2] = new Vector3(innerSize, -0.5, -size);
            octoFace[3] = new Vector3(size, -0.5, -innerSize);
            octoFace[4] = new Vector3(size, -0.5, innerSize);
            octoFace[5] = new Vector3(innerSize, -0.5, size);
            octoFace[6] = new Vector3(-innerSize, -0.5, size);
            octoFace[7] = new Vector3(-size, -0.5, innerSize);
        }

        private static int getBestSide(Vector3 vector) {
            int side = 0;
            double max = 0;
            for (int i = 2; i < 6; i++) {
                double value = Math.abs(vector.getSide(i));
                if (value > max) {
                    max = value;
                    side = i;
                }
            }
            return side;
        }

        private static Vertex5 toVertex5(Vector3 vector) {
            return toVertex5(vector, getBestSide(vector));
        }

        private static Vertex5 toVertex5(Vector3 vector, int side) {
            UV uv;
            if (side == 0 || side == 1) {
                uv = new UV(0.5 + vector.x, 0.5 + vector.z);
            } else if (side == 2 || side == 3) {
                uv = new UV(0.5 + vector.x, 0.5 + vector.y);
            } else if (side == 4 || side == 5) {
                uv = new UV(0.5 + vector.z, 0.5 + vector.y);
            } else {
                uv = new UV(0.5, 0.5);
            }
            return new Vertex5(vector, uv);
        }

        CCModel[] generateModels() {
            CCModel[] models = new CCModel[76];
            for (int mask = 0; mask < 64; mask++) {
                LinkedList<Vertex5> vertices = simplifyModel(generateIntersections(mask));
                int count = vertices.size();
                models[mask] = CCModel.newModel(VertexFormat.Mode.QUADS, count * 2);
                for (int i = 0; i < count; i++) {
                    models[mask].verts[i] = vertices.get(i);
                }
                CCModel.generateBackface(models[mask], 0, models[mask], count, count);
                finalizeModel(models[mask]);
            }

            models[64] = generateConnection();
            for (int side = 0; side < 6; side++) {
                if (side != 0) {
                    models[64 + side] = models[64].sidedCopy(0, side, Vector3.ZERO);
                }
                finalizeModel(models[64 + side]);
            }

            models[70] = generateSideFace();
            for (int side = 0; side < 6; side++) {
                if (side != 0) {
                    models[70 + side] = models[70].sidedCopy(0, side, Vector3.ZERO);
                }
                finalizeModel(models[70 + side]);
            }
            return models;
        }

        private CCModel generateSideFace() {
            CCModel model = CCModel.newModel(VertexFormat.Mode.QUADS, 24);

            model.verts[0] = toVertex5(octoFace[0].copy(), 0);
            model.verts[1] = toVertex5(octoFace[1].copy(), 0);
            model.verts[2] = toVertex5(octoFace[2].copy(), 0);
            model.verts[3] = toVertex5(octoFace[3].copy(), 0);

            model.verts[4] = toVertex5(octoFace[4].copy(), 0);
            model.verts[5] = toVertex5(octoFace[5].copy(), 0);
            model.verts[6] = toVertex5(octoFace[6].copy(), 0);
            model.verts[7] = toVertex5(octoFace[7].copy(), 0);

            model.verts[8] = toVertex5(octoFace[0].copy(), 0);
            model.verts[9] = toVertex5(octoFace[3].copy(), 0);
            model.verts[10] = toVertex5(octoFace[4].copy(), 0);
            model.verts[11] = toVertex5(octoFace[7].copy(), 0);

            for (int i = 0; i < 12; i++) {
                model.verts[i].vec.y = -0.5 * (frameOnly ? 0.75 : 0.99);
            }
            CCModel.generateBackface(model, 0, model, 12, 12);
            return model;
        }

        private CCModel generateConnection() {
            CCModel model = CCModel.newModel(VertexFormat.Mode.QUADS, 64);
            double inner = 0.375 * 0.99;
            double inflate = 1.01;

            for (int i = 0; i < 8; i++) {
                model.verts[i * 4] = new Vertex5(octoFace[i].copy().multiply(inflate, 1, inflate), 0.5 - innerSize, 0);
                model.verts[i * 4 + 1] = new Vertex5(octoFace[i].copy().multiply(inflate, 1, inflate).setSide(0, -inner), 0.5 - innerSize, 0.5 - inner);
                model.verts[i * 4 + 2] = new Vertex5(octoFace[(i + 1) % 8].copy().multiply(inflate, 1, inflate).setSide(0, -inner), 0.5 + innerSize, 0.5 - inner);
                model.verts[i * 4 + 3] = new Vertex5(octoFace[(i + 1) % 8].copy().multiply(inflate, 1, inflate), 0.5 + innerSize, 0);
            }
            CCModel.generateBackface(model, 0, model, 32, 32);
            return model;
        }

        private LinkedList<Vertex5> generateIntersections(int connections) {
            LinkedList<Vertex5> vertices = new LinkedList<>();
            LinkedList<Vertex5> center = addSideFace(new LinkedList<>(), new Cuboid6(-innerSize, -size, -innerSize, innerSize, size, innerSize), 0);
            LinkedList<Vertex5> arm = new LinkedList<>();

            for (int i = 0; i < 8; i++) {
                if (frameOnly && (i % 2 == 0)) {
                    continue;
                }
                arm.add(toVertex5(octoFace[i].copy()));
                arm.add(toVertex5(octoFace[i].copy().setSide(0, -size)));
                arm.add(toVertex5(octoFace[(i + 1) % 8].copy().setSide(0, -size)));
                arm.add(toVertex5(octoFace[(i + 1) % 8].copy()));
            }

            for (int i = 0; i < 6; i++) {
                if ((connections & (1 << i)) != 0) {
                    vertices.addAll(apply(arm, Rotation.sideRotations[i]));
                } else {
                    vertices.addAll(apply(center, Rotation.sideRotations[i]));
                }
            }

            for (int i = 0; i < 6; i++) {
                for (int j = i + 1; j < 6; j++) {
                    if ((i ^ 1) == j) {
                        continue;
                    }

                    boolean a = (connections & (1 << i)) != 0;
                    boolean b = (connections & (1 << j)) != 0;

                    Vector3 v1 = AXES[i].copy();
                    Vector3 v2 = AXES[j].copy();
                    Vector3 v3 = v1.copy().crossProduct(v2);

                    if (!a && !b) {
                        vertices.add(toVertex5(v1.copy().multiply(size).add(v2.copy().multiply(innerSize)).add(v3.copy().multiply(innerSize)), i));
                        vertices.add(toVertex5(v1.copy().multiply(size).add(v2.copy().multiply(innerSize)).add(v3.copy().multiply(-innerSize)), i));
                        vertices.add(toVertex5(v1.copy().multiply(innerSize).add(v2.copy().multiply(size)).add(v3.copy().multiply(-innerSize)), i));
                        vertices.add(toVertex5(v1.copy().multiply(innerSize).add(v2.copy().multiply(size)).add(v3.copy().multiply(innerSize)), i));
                    } else if (!a && b) {
                        vertices.add(toVertex5(v1.copy().multiply(size).add(v2.copy().multiply(innerSize)).add(v3.copy().multiply(innerSize)), i));
                        vertices.add(toVertex5(v1.copy().multiply(size).add(v2.copy().multiply(innerSize)).add(v3.copy().multiply(-innerSize)), i));
                        vertices.add(toVertex5(v1.copy().multiply(size).add(v2.copy().multiply(size)).add(v3.copy().multiply(-innerSize)), i));
                        vertices.add(toVertex5(v1.copy().multiply(size).add(v2.copy().multiply(size)).add(v3.copy().multiply(innerSize)), i));
                    } else if (a && !b) {
                        vertices.add(toVertex5(v1.copy().multiply(size).add(v2.copy().multiply(size)).add(v3.copy().multiply(innerSize)), j));
                        vertices.add(toVertex5(v1.copy().multiply(size).add(v2.copy().multiply(size)).add(v3.copy().multiply(-innerSize)), j));
                        vertices.add(toVertex5(v1.copy().multiply(innerSize).add(v2.copy().multiply(size)).add(v3.copy().multiply(-innerSize)), j));
                        vertices.add(toVertex5(v1.copy().multiply(innerSize).add(v2.copy().multiply(size)).add(v3.copy().multiply(innerSize)), j));
                    }
                }
            }

            if (frameOnly) {
                return vertices;
            }

            for (int i = 0; i < 2; i++) {
                for (int j = 2; j < 4; j++) {
                    for (int k = 4; k < 6; k++) {
                        boolean up = (connections & (1 << i)) != 0;
                        boolean right = (connections & (1 << j)) != 0;
                        boolean left = (connections & (1 << k)) != 0;
                        int count = (up ? 1 : 0) + (right ? 1 : 0) + (left ? 1 : 0);

                        Vector3 v1 = AXES[i];
                        Vector3 v2 = AXES[j];
                        Vector3 v3 = AXES[k];

                        if (count == 3) {
                            Vector3 a1 = v1.copy().multiply(size).add(v2.copy().multiply(size)).add(v3.copy().multiply(innerSize));
                            Vector3 a2 = v1.copy().multiply(size).add(v2.copy().multiply(innerSize)).add(v3.copy().multiply(size));
                            Vector3 a3 = v1.copy().multiply(innerSize).add(v2.copy().multiply(size)).add(v3.copy().multiply(size));
                            vertices.add(toVertex5(a1, i));
                            vertices.add(toVertex5(a3, i));
                            vertices.add(toVertex5(a2, i));
                            vertices.add(toVertex5(a1, i));
                        } else if (count == 0) {
                            Vector3 a1 = v1.copy().multiply(size).add(v2.copy().multiply(innerSize)).add(v3.copy().multiply(innerSize));
                            Vector3 a2 = v1.copy().multiply(innerSize).add(v2.copy().multiply(innerSize)).add(v3.copy().multiply(size));
                            Vector3 a3 = v1.copy().multiply(innerSize).add(v2.copy().multiply(size)).add(v3.copy().multiply(innerSize));
                            vertices.add(toVertex5(a1, 0));
                            vertices.add(toVertex5(a3, 0));
                            vertices.add(toVertex5(a2, 0));
                            vertices.add(toVertex5(a1, 0));
                        } else if (count == 1) {
                            Vector3 a1;
                            Vector3 a2;
                            Vector3 a3;
                            if (up) {
                                a1 = v1;
                                a2 = v2;
                                a3 = v3;
                            } else if (right) {
                                a1 = v2;
                                a2 = v1;
                                a3 = v3;
                            } else {
                                a1 = v3;
                                a2 = v1;
                                a3 = v2;
                            }
                            vertices.add(toVertex5(a1.copy().multiply(innerSize).add(a2.copy().multiply(size)).add(a3.copy().multiply(innerSize)), 0));
                            vertices.add(toVertex5(a1.copy().multiply(size).add(a2.copy().multiply(size)).add(a3.copy().multiply(innerSize)), 0));
                            vertices.add(toVertex5(a1.copy().multiply(size).add(a2.copy().multiply(innerSize)).add(a3.copy().multiply(size)), 0));
                            vertices.add(toVertex5(a1.copy().multiply(innerSize).add(a2.copy().multiply(innerSize)).add(a3.copy().multiply(size)), 0));
                        } else if (count == 2) {
                            int dir;
                            Vector3 a1;
                            Vector3 a2;
                            Vector3 a3;
                            if (!up) {
                                dir = i;
                                a1 = v1;
                                a2 = v2;
                                a3 = v3;
                            } else if (!right) {
                                dir = j;
                                a1 = v2;
                                a2 = v1;
                                a3 = v3;
                            } else {
                                dir = k;
                                a1 = v3;
                                a2 = v1;
                                a3 = v2;
                            }
                            vertices.add(toVertex5(a1.copy().multiply(size).add(a2.copy().multiply(innerSize)).add(a3.copy().multiply(innerSize)), dir));
                            vertices.add(toVertex5(a1.copy().multiply(size).add(a2.copy().multiply(size)).add(a3.copy().multiply(innerSize)), dir));
                            vertices.add(toVertex5(a1.copy().multiply(innerSize).add(a2.copy().multiply(size)).add(a3.copy().multiply(size)), dir));
                            vertices.add(toVertex5(a1.copy().multiply(size).add(a2.copy().multiply(innerSize)).add(a3.copy().multiply(size)), dir));
                        }
                    }
                }
            }
            return vertices;
        }
    }

    static final class SideTubeGen {

        private final double s;
        private final double s2;
        private double h = 1;

        SideTubeGen(double s) {
            this(s, s + 0.09375);
        }

        SideTubeGen(double s, double s2) {
            this.s = s;
            this.s2 = s2;
        }

        SideTubeGen contract(double h) {
            this.h = h;
            return this;
        }

        CCModel[] generateModels() {
            CCModel[] models = new CCModel[70];
            for (int mask = 0; mask < 64; mask++) {
                LinkedList<Vertex5> vertices = simplifyModel(generateIntersections(mask));
                int count = vertices.size();
                models[mask] = CCModel.newModel(VertexFormat.Mode.QUADS, count * 2);
                for (int i = 0; i < count; i++) {
                    models[mask].verts[i] = vertices.get(i);
                }
                CCModel.generateBackface(models[mask], 0, models[mask], count, count);
                finalizeModel(models[mask]);
            }

            for (int side = 0; side < 6; side++) {
                LinkedList<Vertex5> vertices = simplifyModel(generateConnections(side));
                int count = vertices.size();
                models[64 + side] = CCModel.newModel(VertexFormat.Mode.QUADS, count);
                for (int i = 0; i < count; i++) {
                    models[64 + side].verts[i] = vertices.get(i);
                }
                finalizeModel(models[64 + side]);
            }
            return models;
        }

        private Cuboid6 newCube(Vector3 min, Vector3 max) {
            double tmp;
            if (min.x > max.x) {
                tmp = min.x;
                min.x = max.x;
                max.x = tmp;
            }
            if (min.y > max.y) {
                tmp = min.y;
                min.y = max.y;
                max.y = tmp;
            }
            if (min.z > max.z) {
                tmp = min.z;
                min.z = max.z;
                max.z = tmp;
            }

            if (h < 1) {
                Vector3 mid = min.copy().add(max).multiply(0.5);
                min.x = min.x <= -0.5 || min.x >= 0.5 ? min.x : (min.x - mid.x) * h + mid.x;
                min.y = min.y <= -0.5 || min.y >= 0.5 ? min.y : (min.y - mid.y) * h + mid.y;
                min.z = min.z <= -0.5 || min.z >= 0.5 ? min.z : (min.z - mid.z) * h + mid.z;
                max.x = max.x <= -0.5 || max.x >= 0.5 ? max.x : (max.x - mid.x) * h + mid.x;
                max.y = max.y <= -0.5 || max.y >= 0.5 ? max.y : (max.y - mid.y) * h + mid.y;
                max.z = max.z <= -0.5 || max.z >= 0.5 ? max.z : (max.z - mid.z) * h + mid.z;
            }
            return new Cuboid6(min, max);
        }

        private LinkedList<Vertex5> generateConnections(int side) {
            LinkedList<Vertex5> vertices = new LinkedList<>();
            Vector3 a = AXES[side];
            Vector3 b = AXES[ORTHOG_AXES[side][0]];
            Vector3 c = AXES[ORTHOG_AXES[side][1]];

            for (int x = -1; x <= 1; x += 2) {
                for (int y = -1; y <= 1; y += 2) {
                    Cuboid6 cube = newCube(
                            a.copy().multiply(s2).add(b.copy().multiply(s * x)).add(c.copy().multiply(s * y)),
                            a.copy().multiply(h / 2).add(b.copy().multiply(s2 * x)).add(c.copy().multiply(s2 * y))
                    );
                    addSideFaces(vertices, cube, (1 << side) ^ 63);
                }
            }

            for (int other = 0; other < 6; other++) {
                if (side == other || (side ^ 1) == other) {
                    continue;
                }

                a = AXES[side];
                b = AXES[other];
                int orthog = ORTHOGONALS[side][other];
                c = AXES[orthog];

                Cuboid6 cube = newCube(
                        a.copy().multiply(h / 2 - (s2 - s)).add(b.copy().multiply(s)).add(c.copy().multiply(s)),
                        a.copy().multiply(h / 2).add(b.copy().multiply(s2)).add(c.copy().multiply(-s))
                );
                addSideFaces(vertices, cube, (1 << orthog) | (1 << (orthog ^ 1)));
            }
            return vertices;
        }

        private LinkedList<Vertex5> generateIntersections(int connections) {
            LinkedList<Vertex5> vertices = new LinkedList<>();

            for (int side = 0; side < 6; side++) {
                if ((connections & (1 << side)) == 0) {
                    continue;
                }

                Vector3 a = AXES[side];
                Vector3 b = AXES[ORTHOG_AXES[side][0]];
                Vector3 c = AXES[ORTHOG_AXES[side][1]];

                for (int x = -1; x <= 1; x += 2) {
                    for (int y = -1; y <= 1; y += 2) {
                        Cuboid6 cube = newCube(
                                a.copy().multiply(s2).add(b.copy().multiply(s * x)).add(c.copy().multiply(s * y)),
                                a.copy().multiply(h / 2).add(b.copy().multiply(s2 * x)).add(c.copy().multiply(s2 * y))
                        );
                        addSideFaces(vertices, cube, (1 << side) | (1 << (side ^ 1)));
                    }
                }
            }

            for (int[] pair : EDGE_PAIRS) {
                if (((connections & (1 << pair[0])) != 0) != ((connections & (1 << pair[1])) != 0)) {
                    continue;
                }

                Vector3 a = AXES[pair[0]];
                Vector3 b = AXES[pair[1]];
                int orthog = ORTHOGONALS[pair[0]][pair[1]];
                Vector3 c = AXES[orthog];
                Cuboid6 cube = newCube(
                        a.copy().multiply(s).add(b.copy().multiply(s)).add(c.copy().multiply(s)),
                        a.copy().multiply(s2).add(b.copy().multiply(s2)).add(c.copy().multiply(-s))
                );
                addSideFaces(vertices, cube, (1 << orthog) | (1 << (orthog ^ 1)));
            }

            for (int[] corner : CORNER_TRIPLETS) {
                Vector3 a = AXES[corner[0]];
                Vector3 b = AXES[corner[1]];
                Vector3 c = AXES[corner[2]];
                Cuboid6 cube = newCube(
                        a.copy().multiply(s).add(b.copy().multiply(s)).add(c.copy().multiply(s)),
                        a.copy().multiply(s2).add(b.copy().multiply(s2)).add(c.copy().multiply(s2))
                );

                int mask = ((1 << corner[0]) & connections) | ((1 << corner[1]) & connections) | ((1 << corner[2]) & connections);
                if (((connections & (1 << corner[1])) != 0) == ((connections & (1 << corner[2])) != 0)) {
                    mask |= 1 << (corner[0] ^ 1);
                }
                if (((connections & (1 << corner[0])) != 0) == ((connections & (1 << corner[2])) != 0)) {
                    mask |= 1 << (corner[1] ^ 1);
                }
                if (((connections & (1 << corner[0])) != 0) == ((connections & (1 << corner[1])) != 0)) {
                    mask |= 1 << (corner[2] ^ 1);
                }
                addSideFaces(vertices, cube, mask);
            }

            return vertices;
        }
    }

    private static final class Face {

        private final Vertex5[] verts;

        private Face(Vertex5[] verts) {
            this.verts = verts;
        }

        private static Face loadFromIterator(Iterator<Vertex5> iterator) {
            Face face = new Face(new Vertex5[4]);
            for (int i = 0; i < 4; i++) {
                face.verts[i] = iterator.next();
            }
            return face;
        }

        private Vertex5 vec(int i) {
            return verts[(i % 4 + 4) % 4];
        }

        private void setVec(int i, Vertex5 vertex) {
            verts[(i % 4 + 4) % 4] = vertex;
        }

        private void reverse() {
            Vertex5 tmp = verts[1];
            verts[1] = verts[3];
            verts[3] = tmp;
        }

        private boolean equalVert(Vertex5 a, Vertex5 b) {
            return a.vec.equalsT(b.vec) && a.uv.equals(b.uv);
        }

        private boolean attemptToCombine(Face other) {
            if (attemptToCombineUnflipped(other)) {
                return true;
            }
            reverse();
            if (attemptToCombineUnflipped(other)) {
                return true;
            }
            reverse();
            other.reverse();
            if (attemptToCombineUnflipped(other)) {
                return true;
            }
            reverse();
            if (attemptToCombineUnflipped(other)) {
                return true;
            }
            reverse();
            other.reverse();
            return false;
        }

        private boolean attemptToCombineUnflipped(Face other) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (!equalVert(vec(i), other.vec(j)) || !equalVert(vec(i + 1), other.vec(j - 1))) {
                        continue;
                    }

                    Vector3 l1 = vec(i - 1).vec.copy().subtract(vec(i).vec).normalize();
                    Vector3 l2 = vec(i + 2).vec.copy().subtract(vec(i + 1).vec).normalize();
                    Vector3 l3 = other.vec(j).vec.copy().subtract(other.vec(j + 1).vec).normalize();
                    Vector3 l4 = other.vec(j - 1).vec.copy().subtract(other.vec(j - 2).vec).normalize();

                    if (l1.equalsT(l3) && l2.equalsT(l4)) {
                        setVec(i, other.vec(j + 1));
                        setVec(i + 1, other.vec(j - 2));
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
