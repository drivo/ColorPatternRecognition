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

import java.awt.Color;

/**
 * Questa classe è usata per descrivere il colore da analizzare all'interno
 * di una regione. L'analyzer userà del colore solamente la tonalità (hue).

 * Esempio: nel caso della progress bar, dovrebbe contenere il colore più vicino
 * a quello della barra (giallo).
 * 
 * @author guido d'albore
 */

public class SimplePatternColor extends Color {

    int h, s, v;
    
    public SimplePatternColor(int r, int g, int b) {
        super(r, g, b);
        loadHSV();
    }

    public SimplePatternColor(Color c) {
        this(c.getRed(), c.getGreen(), c.getBlue());
    }

    public SimplePatternColor(int hue) {
        super(Color.HSBtoRGB((float)hue/360.0f, 1, 1));
    }

    private void loadHSV() {
        float hsv[] = Color.RGBtoHSB(getRed(), getGreen(), getBlue(), null);

        // Qui de-normalizziamo i valori

        // La tonalità su scala circolare (360)
        h = (int)(hsv[0] * 360);
        // La saturazione è in percentuale
        s = (int)(hsv[1] * 100);
        // La brillantezza è in percentuale
        v = (int)(hsv[2] * 100);
    }
    
    public int getHue() {
        return h;
    }

    public int getSaturation() {
        return s;
    }


    public int getBrightness() {
        return v;
    }
}
