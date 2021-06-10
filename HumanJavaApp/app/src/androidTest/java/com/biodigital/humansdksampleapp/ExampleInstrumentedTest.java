package com.biodigital.humansdksampleapp;

import android.content.Context;
import android.graphics.Bitmap;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import androidx.test.InstrumentationRegistry;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.UiThreadTestRule;

import com.biodigital.humansdk.HKAnnotation;
import com.biodigital.humansdk.HKCamera;
import com.biodigital.humansdk.HKColor;
import com.biodigital.humansdk.HKHuman;
import com.biodigital.humansdk.HKHumanInterface;
import com.biodigital.humansdk.HKServices;
import com.biodigital.humansdk.HKServicesInterface;
import com.biodigital.humansdk.HKTimeline;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleInstrumentedTest implements HKHumanInterface, HKServicesInterface {

    public HKHuman human;

    public HKServices humanKit;
    private CountDownLatch apiReady = new CountDownLatch(1);
    private CountDownLatch sceneInit = new CountDownLatch(1);
    private CountDownLatch loaded = new CountDownLatch(1);


    @Rule
    public UiThreadTestRule uiThreadTestRule = new UiThreadTestRule();

    @Before
    public void setup() throws Throwable {
        uiThreadTestRule.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Context appContext = InstrumentationRegistry.getTargetContext();
                humanKit = new HKServices(appContext, "49a7a5f85eece6b9dc9610059908391bb2d61e22", null, ExampleInstrumentedTest.this);
                human =  new HKHuman(appContext);
            }
        });
        human.setInterface(this);
    }

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testSceneLoaded() throws Exception {
        apiReady.await();
        human.load("production/maleAdult/brain_v02.json");
        sceneInit.await();
        System.out.println("** loaded!!");
        assertEquals(human.scene.title,"Brain");
        assertTrue(human.scene.objectIds.size() > 0);
    }

    @Override
    public void onAnimationComplete() {

    }

    @Override
    public void onAnnotationCreated(String annotationId) {

    }

    @Override
    public void onAnnotationDestroyed(String annotationId) {

    }

    @Override
    public void onAnnotationsShown(Boolean isShown) {

    }

    @Override
    public void onAnnotationUpdated(HKAnnotation annotation) {

    }

    @Override
    public void onCameraUpdated(HKCamera camera) {

    }

    @Override
    public void onChapterTransition(String chapterID) {

    }

    @Override
    public void onObjectColor(String objectId, HKColor color) {

    }

    @Override
    public void onObjectDeselected(String objectID) {

    }

    @Override
    public void onObjectPicked(String objectId, double[] position) {

    }

    @Override
    public void onObjectSelected(String objectID) {

    }

//    @Override
//    public void onSceneCapture() {
//
//    }

    @Override
    public void onTestReady() {
        System.out.println("***** GOT TEST READY");
        apiReady.countDown();
    }

    @Override
    public void onSceneInit(String title) {
        System.out.println("***** GOT SCENE INIT");
        sceneInit.countDown();
    }

    @Override
    public void onSceneRestore() {

    }

    @Override
    public void onScreenshot(Bitmap image) {

    }

    @Override
    public void onTimelineUpdated(HKTimeline timeline) {

    }

    @Override
    public void onXrayEnabled(Boolean isEnabled) {

    }

    @Override
    public void onModelsLoaded() {

    }

    @Override
    public void onValidSDK() {
        System.out.println("valid SDK! :)");
    }

    @Override
    public void onInvalidSDK() {
        System.out.println("invalid SDK :(");
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.biodigital.humansdk.test", appContext.getPackageName());
    }
}
