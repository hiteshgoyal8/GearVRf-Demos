/* Copyright 2015 Samsung Electronics Co., LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gearvrf.complexscene;

import android.graphics.Color;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMaterialShaderManager;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRShaderTemplate;

import java.io.IOException;

public class SampleMain extends GVRMain {

    private GVRContext mGVRContext;
    private ColorShader mColorShader;

    @Override
    public GVRMain.SplashMode getSplashMode() {
        return SplashMode.NONE;
    }

    @Override
    public void onInit(GVRContext gvrContext) throws IOException {

        // save context for possible use in onStep(), even though that's empty
        // in this sample
        mGVRContext = gvrContext;
        mColorShader = new ColorShader(mGVRContext);

        // set background color
        GVRScene scene = gvrContext.getMainScene();
        scene.getMainCameraRig().getLeftCamera()
                .setBackgroundColor(Color.WHITE);
        scene.getMainCameraRig().getRightCamera()
                .setBackgroundColor(Color.WHITE);

        float NORMAL_CURSOR_SIZE = 0.4f;
        float CURSOR_Z_POSITION = -9.0f;
        int CURSOR_RENDER_ORDER = 100000;
        
        GVRSceneObject cursor = new GVRSceneObject(mGVRContext,
                mGVRContext.createQuad(NORMAL_CURSOR_SIZE, NORMAL_CURSOR_SIZE),
                mGVRContext.getAssetLoader().loadTexture(new GVRAndroidResource(mGVRContext,
                        "cursor_idle.png")));
        cursor.getTransform().setPositionZ(CURSOR_Z_POSITION);
        cursor.getRenderData().setRenderingOrder(
                GVRRenderData.GVRRenderingOrder.OVERLAY);
        cursor.getRenderData().setDepthTest(false);
        cursor.getRenderData().setRenderingOrder(CURSOR_RENDER_ORDER);
        mGVRContext.getMainScene().getMainCameraRig().addChildObject(cursor);
        
        try {

            GVRMesh mesh = mGVRContext.getAssetLoader().loadMesh(new GVRAndroidResource(mGVRContext,
                    "bunny.obj"));

            final int OBJECTS_CNT = 5;
            for (int x=-OBJECTS_CNT; x<=OBJECTS_CNT; ++x) {
                for (int y=-OBJECTS_CNT; y<=OBJECTS_CNT; ++y) {
                    GVRSceneObject sceneObject = getColorMesh(1.0f, mesh);
                    sceneObject.getTransform().setPosition(1.0f*x, 1.0f*y, -5.0f);
                    sceneObject.getTransform().setScale(0.5f, 0.5f, 1.0f);
                    scene.addSceneObject(sceneObject);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean processKeyEvent(int keyCode) {
        return false;
    }

    @Override
    public void onStep() {
    }

    private GVRSceneObject getColorMesh(float scale, GVRMesh mesh) {
        GVRMaterial material = new GVRMaterial(mGVRContext,
                GVRMaterial.GVRShaderType.BeingGenerated.ID);
        material.setVec4(ColorShader.COLOR_KEY, 1.0f,
                0.0f, 1.0f, 1.0f);

        GVRRenderData renderData = new GVRRenderData(mGVRContext);
        renderData.setMaterial(material);
        renderData.setMesh(mesh);
        GVRSceneObject meshObject = new GVRSceneObject(mGVRContext);
        meshObject.attachRenderData(renderData);
        meshObject.getTransform().setScale(scale, scale, scale);
        meshObject.getRenderData().setMaterial(material);

        GVRMaterialShaderManager shaderManager = mGVRContext.getMaterialShaderManager();
        GVRShaderTemplate shaderTemplate = shaderManager.retrieveShaderTemplate(ColorShader.class);
        shaderTemplate.bindShader(mGVRContext, meshObject.getRenderData(), getGVRContext().getMainScene());

        return meshObject;
    }
    
}



