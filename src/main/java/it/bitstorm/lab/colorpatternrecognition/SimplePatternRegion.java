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

import java.awt.Rectangle;

/**
 * Questa classe è usata del descrivere la regione sottoposta ad
 * analisi. Adesso implementa un semplice rettangolo, può essere estesa
 * per il supporto a diverse tipologie di forme.
 *
 * @author guido d'albore
 */
public class SimplePatternRegion extends Rectangle {

    public SimplePatternRegion(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
    
}
