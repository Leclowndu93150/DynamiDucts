/*
 * Derived from CodeChickenLib 4.6.1.526 by ChickenBones and covers1624.
 * https://github.com/TheCBProject/CodeChickenLib
 * Modified for DynamiDucts on 2026-10-26.
 *
 * This file is licensed under the GNU Lesser General Public License v2.1.
 * https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 */

package com.leclowndu93150.dynamiducts.client.renderer.ccl;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class IconTransformation {

    public final TextureAtlasSprite icon;

    public IconTransformation(TextureAtlasSprite icon) {
        this.icon = icon;
    }

    public void apply(UV uv) {
        uv.u = icon.getU((float) uv.u);
        uv.v = icon.getV((float) uv.v);
    }
}
