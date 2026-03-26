package com.leclowndu93150.modular_networks.client.renderer;

import codechicken.lib.render.BlockRenderer;
import codechicken.lib.render.CCModel;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.Vertex5;

import java.util.LinkedList;

public class DuctModels {

    private static final float W = 0.1875F;
    private static final float SMALL_INNER_SCALE = 0.99F;
    private static final float LARGE_INNER_SCALE = 0.99F;
    private static final double RENDER_OFFSET = 1.0 / 1024.0;

    public static CCModel modelCenter;
    public static CCModel[][] modelConnection = new CCModel[3][6];
    public static CCModel[] modelOpaqueTubes;
    public static CCModel[] modelTransTubes;
    public static CCModel[] modelFluidTubes;
    public static CCModel[] modelLargeTubes;
    public static CCModel[] modelFrameConnection;
    public static CCModel[] modelFrame;
    public static CCModel[] modelTransportConnection;
    public static CCModel[] modelTransport;
    public static CCModel[] modelSideTubes;
    public static CCModel[] modelSideTubesInner;

    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;

        try {
            generateCenter();
            generateConnections();
            generateTubes();
            generateFrames();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate duct models.", e);
        }
    }

    private static void generateCenter() {
        modelCenter = CCModel.quadModel(48).generateBox(0, -3, -3, -3, 6, 6, 6, 0, 0, 32, 32, 16);
        CCModel.generateBackface(modelCenter, 0, modelCenter, 24, 24);
        modelCenter.computeNormals().shrinkUVs(RENDER_OFFSET);
    }

    private static void generateConnections() {
        modelConnection[0][1] = CCModel.quadModel(48)
                .generateBlock(0, new Cuboid6(0.3125, 0.6875, 0.3125, 0.6875, 1, 0.6875).expand(-RENDER_OFFSET));
        modelConnection[1][1] = CCModel.quadModel(24)
                .generateBox(0, -4, 4, -4, 8, 4 - RENDER_OFFSET, 8, 0, 0, 32, 32, 16).computeNormals();
        modelConnection[2][1] = CCModel.quadModel(24)
                .generateBox(0, -4, 4, -4, 8, 4 - RENDER_OFFSET, 8, 0, 16, 32, 32, 16).computeNormals();

        CCModel.generateBackface(modelConnection[0][1], 0, modelConnection[0][1], 24, 24);
        modelConnection[0][1].apply(new codechicken.lib.vec.Translation(-0.5, -0.5, -0.5));

        for (CCModel[] sideModels : modelConnection) {
            CCModel.generateSidedModels(sideModels, 1, Vector3.ZERO);
        }

        Scale[] mirrors = new Scale[]{new Scale(1, -1, 1), new Scale(1, 1, -1), new Scale(-1, 1, 1)};
        for (CCModel[] sideModels : modelConnection) {
            for (int s = 2; s < 6; s += 2) {
                sideModels[s] = sideModels[0].sidedCopy(0, s, Vector3.ZERO);
            }
            for (int s = 1; s < 6; s += 2) {
                sideModels[s] = sideModels[s - 1].backfacedCopy().apply(mirrors[s / 2]);
            }
        }

        for (CCModel[] sideModels : modelConnection) {
            for (CCModel model : sideModels) {
                model.computeNormals().shrinkUVs(RENDER_OFFSET);
            }
        }

        for (int type = 1; type <= 2; type++) {
            for (int side = 0; side < 6; side++) {
                modelConnection[type][side] = modelConnection[type][side].twoFacedCopy();
            }
        }
    }

    private static void generateTubes() {
        modelOpaqueTubes = genTubeModels(W, true);
        modelTransTubes = genTubeModels(W, false);
        modelFluidTubes = genTubeModels(W * SMALL_INNER_SCALE, false);
        modelLargeTubes = genTubeModels(0.21875F, true);
    }

    private static void generateFrames() {
        modelFrameConnection = new DuctModelHelper.OctagonalTubeGen(0.375, 0.1812, true).generateModels();
        modelFrame = new DuctModelHelper.OctagonalTubeGen(0.375 * LARGE_INNER_SCALE, 0.1812, false).generateModels();
        modelTransportConnection = new DuctModelHelper.OctagonalTubeGen(0.5 * LARGE_INNER_SCALE, 0.1812, true).generateModels();
        modelTransport = new DuctModelHelper.OctagonalTubeGen(0.5 * LARGE_INNER_SCALE * LARGE_INNER_SCALE, 0.1812, false).generateModels();
        modelSideTubes = new DuctModelHelper.SideTubeGen(W + 1.0E-4).generateModels();
        modelSideTubesInner = new DuctModelHelper.SideTubeGen(W + 1.0E-4).contract(0.999).generateModels();
    }

    private static CCModel[] genTubeModels(float w, boolean opaque) {
        Cuboid6 center = new Cuboid6(-w, -w, -w, w, w, w);
        Cuboid6[] duct = rotateCuboids(new Cuboid6(-w, -0.5, -w, w, -w, w));
        Cuboid6[] ductWCenter = rotateCuboids(new Cuboid6(-w, -0.5, -w, w, w, w));
        Cuboid6[] ductFullLength = rotateCuboids(new Cuboid6(-w, -0.5, -w, w, 0.5, w));

        int[][] orthogs = {{2, 3, 4, 5}, {2, 3, 4, 5}, {0, 1, 4, 5}, {0, 1, 4, 5}, {0, 1, 2, 3}, {0, 1, 2, 3}};

        CCModel[] models = new CCModel[64];
        BlockRenderer.BlockFace face = new BlockRenderer.BlockFace();

        for (int mask = 0; mask < 64; mask++) {
            LinkedList<Vertex5> verts = new LinkedList<>();

            for (int side = 0; side < 6; side++) {
                if (!opaque && ((mask & (1 << side)) != 0)) {
                    for (int i : orthogs[side]) {
                        if ((mask & (1 << i)) != 0) {
                            addSideFace(verts, face, duct[i], side);
                        }
                    }
                } else {
                    int singleIdx = -1;
                    int doubleIdx = -1;
                    for (int i : orthogs[side]) {
                        if ((mask & (1 << i)) != 0) {
                            singleIdx = i;
                            if ((mask & (1 << (i ^ 1))) != 0) {
                                doubleIdx = i;
                                break;
                            }
                        }
                    }

                    if (doubleIdx != -1) {
                        for (int i : orthogs[side]) {
                            if (i == doubleIdx) {
                                addSideFace(verts, face, ductFullLength[i], side);
                            } else if (i != (doubleIdx ^ 1) && (mask & (1 << i)) != 0) {
                                addSideFace(verts, face, duct[i], side);
                            }
                        }
                    } else if (singleIdx != -1) {
                        for (int i : orthogs[side]) {
                            if (i == singleIdx) {
                                addSideFace(verts, face, ductWCenter[i], side);
                            } else if ((mask & (1 << i)) != 0) {
                                addSideFace(verts, face, duct[i], side);
                            }
                        }
                    } else {
                        if ((mask & (1 << side)) == 0) {
                            addSideFace(verts, face, center, side);
                        }
                        for (int i : orthogs[side]) {
                            if ((mask & (1 << i)) != 0) {
                                addSideFace(verts, face, duct[i], side);
                            }
                        }
                    }
                }
            }

            int n = verts.size();
            models[mask] = CCModel.quadModel(n * 2);
            for (int j = 0; j < n; j++) {
                models[mask].verts[j] = verts.get(j);
            }
            CCModel.generateBackface(models[mask], 0, models[mask], n, n);
            models[mask].computeNormals().shrinkUVs(RENDER_OFFSET);
        }
        return models;
    }

    private static void addSideFace(LinkedList<Vertex5> verts, BlockRenderer.BlockFace face, Cuboid6 bounds, int side) {
        face.loadCuboidFace(bounds.copy().add(Vector3.CENTER), side);
        for (Vertex5 v : face.getVertices()) {
            verts.add(new Vertex5(v.vec.copy().subtract(Vector3.CENTER), v.uv.copy()));
        }
    }

    private static Cuboid6[] rotateCuboids(Cuboid6 downCube) {
        Cuboid6[] result = new Cuboid6[6];
        for (int i = 0; i < 6; i++) {
            result[i] = downCube.copy().apply(Rotation.sideRotations[i]);
        }
        return result;
    }
}
