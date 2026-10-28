/*
 * Derived from CodeChickenLib 4.6.1.526 by ChickenBones and covers1624.
 * https://github.com/TheCBProject/CodeChickenLib
 * Modified for DynamiDucts on 2026-10-26.
 *
 * This file is licensed under the GNU Lesser General Public License v2.1.
 * https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 */

package com.leclowndu93150.dynamiducts.client.renderer.ccl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class CCRenderState {

    private static final ThreadLocal<CCRenderState> INSTANCES = ThreadLocal.withInitial(CCRenderState::new);

    public int baseColour;
    public int brightness;
    public int overlay;

    private final Vector3 position = new Vector3();
    private final Vector3 normal = new Vector3();
    private final UV uv = new UV();
    private final Vector3f output = new Vector3f();
    private VertexConsumer consumer;
    private VertexFormat format;
    private Matrix4f pose;
    private Vector3f diffuseLight0;
    private Vector3f diffuseLight1;

    private CCRenderState() {
    }

    public static CCRenderState instance() {
        return INSTANCES.get();
    }

    public void reset() {
        baseColour = -1;
        diffuseLight0 = null;
        diffuseLight1 = null;
    }

    public void setDiffuseLighting(Vector3f light0, Vector3f light1) {
        diffuseLight0 = light0;
        diffuseLight1 = light1;
    }

    public void bind(VertexConsumer consumer, VertexFormat format) {
        this.consumer = consumer;
        this.format = format;
        this.pose = null;
    }

    public void bind(VertexConsumer consumer, VertexFormat format, PoseStack poseStack) {
        bind(consumer, format);
        pose = new Matrix4f(poseStack.last().pose());
    }

    public void bind(RenderType renderType, MultiBufferSource source, PoseStack poseStack) {
        bind(source.getBuffer(renderType), renderType.format(), poseStack);
    }

    void render(CCModel model, int start, int end, Transformation transformation, IconTransformation icon) {
        for (int i = start; i < end; i++) {
            Vertex5 vertex = model.verts[i];
            position.set(vertex.vec);
            normal.set(model.normals[i]);
            uv.set(vertex.uv);
            transformation.apply(position);
            transformation.applyN(normal);
            icon.apply(uv);
            writeVertex();
        }
    }

    private void writeVertex() {
        for (VertexFormatElement element : format.getElements()) {
            switch (element.usage()) {
                case POSITION -> {
                    output.set(position.x, position.y, position.z);
                    if (pose != null) {
                        pose.transformPosition(output);
                    }
                    consumer.addVertex(output.x, output.y, output.z);
                }
                case UV -> {
                    switch (element.index()) {
                        case 0 -> consumer.setUv((float) uv.u, (float) uv.v);
                        case 1 -> consumer.setOverlay(overlay);
                        case 2 -> consumer.setLight(brightness);
                    }
                }
                case COLOR -> writeColour();
                case NORMAL -> {
                    output.set(normal.x, normal.y, normal.z);
                    if (pose != null) {
                        pose.transformDirection(output).normalize();
                    }
                    consumer.setNormal(output.x, output.y, output.z);
                }
            }
        }
    }

    private void writeColour() {
        if (diffuseLight0 == null) {
            consumer.setColor(baseColour >>> 24, baseColour >> 16 & 0xFF, baseColour >> 8 & 0xFF, baseColour & 0xFF);
            return;
        }
        float shade = diffuseShade();
        consumer.setColor(shaded(baseColour >>> 24, shade), shaded(baseColour >> 16 & 0xFF, shade), shaded(baseColour >> 8 & 0xFF, shade), baseColour & 0xFF);
    }

    private float diffuseShade() {
        float light0 = Math.max(0.0F, (float) (normal.x * diffuseLight0.x() + normal.y * diffuseLight0.y() + normal.z * diffuseLight0.z()));
        float light1 = Math.max(0.0F, (float) (normal.x * diffuseLight1.x() + normal.y * diffuseLight1.y() + normal.z * diffuseLight1.z()));
        return Math.min(1.0F, (light0 + light1) * 0.6F + 0.4F);
    }

    private static int shaded(int channel, float shade) {
        return Math.round(channel * shade);
    }
}
