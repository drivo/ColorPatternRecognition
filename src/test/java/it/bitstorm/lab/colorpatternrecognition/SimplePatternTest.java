/* 
Copyright 2011 (C) by Guido D'Albore (guido@bitstorm.it)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package it.bitstorm.lab.colorpatternrecognition;

import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author guido d'albore
 */
public class SimplePatternTest {
    Logger log = Logger.getLogger(SimplePatternTest.class);
    
    // Directory dei campioni estratti dal filmato con ffmpeg
    final String basepath;

    final static String resources[] = {
        "frame_041.png",
        "frame_042.png",
        "frame_043.png",
        "frame_044.png",
        "frame_045.png",
        "frame_046.png",
        "frame_047.png",
        "frame_048.png",
        "frame_049.png",
        "frame_050.png",
        "frame_051.png",
        "frame_052.png",
        "frame_053.png",
        "frame_054.png",
        "frame_055.png",
        "frame_056.png",
        "frame_057.png",
        "frame_058.png",
        "frame_059.png",
        "frame_060.png",
        "frame_061.png",
    };
    
    SimplePatternRegion region  = new SimplePatternRegion(128, 74, 75, 3);
    SimplePatternColor  color   = new SimplePatternColor(200, 198, 179);   // Tonalità giallo

    public SimplePatternTest() {
        basepath = System.getProperty("user.dir") + "/samples/";
        log.debug("Samples base directory: " + basepath);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void analyzeProgressBar() {
        float progression = 0;
        boolean isLoadingFinished = false;
        boolean isLoadingStarted = false;
        SimplePatternAnalyzer spa;

        log.info("**************************");
        log.info("A NEW TEST IS STARTING....");
        log.info("**************************");

        for(String imagefile : resources) {
            spa = new SimplePatternAnalyzer(basepath + imagefile, region, color);
            log.info("Analyzing \"" + imagefile + "\"...");
            log.info("Progression bar is on " + spa.getProgression()*100.0f + "%");
            log.info("Analysis:");

            if(spa.getProgression() > progression) {
                // Case 1: loading phase
                if(progression == 0) {
                    log.info("Progression bar: loading phase started.");
                    isLoadingStarted = true;
                } else {
                    log.info("Progression bar: loading phase.");
                }
            } else {
                if(spa.getProgression() < progression) {
                    // Case 2: end of loading phase
                    if(spa.getProgression() == 0.0f) {
                        log.info("Progression bar: loading phase finished! The page has been loaded!");
                        isLoadingFinished = true;
                    } else {
                        log.info("Progression bar: loading phase.");
                    }
                } else {
                    if(spa.getProgression() == 0.0f) {
                        // Case 3: waiting for loading phase
                        log.info("Progression bar: waiting for loading phase.");
                    } else {
                        log.info("Progression bar: loading phase.");
                    }
                }
            }

            if(!isLoadingFinished) {
                progression = spa.getProgression();
            }

            log.info("-----------------------------------------");
        }

        Assert.assertTrue("The progress bar has never finished.", isLoadingFinished);
        Assert.assertTrue("The progress bar has never started.", isLoadingStarted);

        log.info("************************************");
        log.info("TEST PASSED.");
        log.info("************************************");
    }
}