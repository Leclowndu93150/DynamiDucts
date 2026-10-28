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
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.ModelBakery;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class CCRenderState {

    private static final ThreadLocal<CCRenderState> INSTANCES = ThreadLocal.withInitial(CCRenderState::new);
    private static final ThreadLocal<CCRenderState> WRITERS = ThreadLocal.withInitial(CCRenderState::new);

    public int baseColour;
    public int brightness;
    public int overlay;
    public ModelFeatureRenderer.CrumblingOverlay crumblingOverlay;

    private final Vector3 position = new Vector3();
    private final Vector3 normal = new Vector3();
    private final UV uv = new UV();
    private final Vector3f output = new Vector3f();
    private VertexConsumer consumer;
    private VertexFormat format;
    private Matrix4f pose;
    private List<Draw> draws;

    private CCRenderState() {
    }

    public static CCRenderState instance() {
        return INSTANCES.get();
    }

    public void reset() {
        baseColour = -1;
        crumblingOverlay = null;
    }

    public void bind(VertexConsumer consumer, VertexFormat format) {
        this.consumer = consumer;
        this.format = format;
        this.pose = null;
        this.draws = null;
    }

    public void bind(RenderType renderType, SubmitNodeCollector collector, PoseStack poseStack) {
        List<Draw> batch = new ArrayList<>();
        VertexFormat batchFormat = renderType.format();
        collector.submitCustomGeometry(poseStack, renderType, (submitPose, buffer) -> replay(batch, batchFormat, submitPose, buffer));
        ModelFeatureRenderer.CrumblingOverlay crumbling = crumblingOverlay;
        if (crumbling != null && renderType.affectsCrumbling()) {
            collector.submitCustomGeometry(poseStack, ModelBakery.DESTROY_TYPES.get(crumbling.progress()), (submitPose, buffer) ->
                    replay(batch, batchFormat, submitPose, new SheetedDecalTextureGenerator(buffer, crumbling.cameraPose(), 1.0F)));
        }
        consumer = null;
        format = null;
        pose = null;
        draws = batch;
    }

    void render(CCModel model, int start, int end, Transformation transformation, IconTransformation icon) {
        if (draws != null) {
            draws.add(new Draw(model, start, end, transformation, icon, baseColour, brightness, overlay));
            return;
        }
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

    private static void replay(List<Draw> batch, VertexFormat format, PoseStack.Pose submitPose, VertexConsumer buffer) {
        CCRenderState writer = WRITERS.get();
        writer.bind(buffer, format);
        writer.pose = submitPose.pose();
        for (Draw draw : batch) {
            writer.baseColour = draw.baseColour;
            writer.brightness = draw.brightness;
            writer.overlay = draw.overlay;
            writer.render(draw.model, draw.start, draw.end, draw.transformation, draw.icon);
        }
        writer.bind(null, null);
    }

    private void writeVertex() {
        for (VertexFormatElement element : format.getElements()) {
            if (element == VertexFormatElement.POSITION) {
                output.set(position.x, position.y, position.z);
                if (pose != null) {
                    pose.transformPosition(output);
                }
                consumer.addVertex(output.x, output.y, output.z);
            } else if (element == VertexFormatElement.UV0) {
                consumer.setUv((float) uv.u, (float) uv.v);
            } else if (element == VertexFormatElement.UV1) {
                consumer.setOverlay(overlay);
            } else if (element == VertexFormatElement.UV2) {
                consumer.setLight(brightness);
            } else if (element == VertexFormatElement.COLOR) {
                consumer.setColor(baseColour >>> 24, baseColour >> 16 & 0xFF, baseColour >> 8 & 0xFF, baseColour & 0xFF);
            } else if (element == VertexFormatElement.NORMAL) {
                output.set(normal.x, normal.y, normal.z);
                if (pose != null) {
                    pose.transformDirection(output).normalize();
                }
                consumer.setNormal(output.x, output.y, output.z);
            }
        }
    }

    private record Draw(CCModel model, int start, int end, Transformation transformation, IconTransformation icon,
                        int baseColour, int brightness, int overlay) {
    }
}
