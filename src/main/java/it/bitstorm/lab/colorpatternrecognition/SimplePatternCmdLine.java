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

import org.apache.log4j.Logger;

/**
 * @author guido d'albore (guido@bitstorm.it)
 */
public class SimplePatternCmdLine {
    static Logger log = Logger.getLogger(SimplePatternCmdLine.class);

    public static void main(String[] args) {
        if(args.length < 5) {
            log.info("Progress bar recognitor v0.1 - Developed by Guido D'Albore (guido@bitstorm.it)");
            log.info("\nUsage:");
            log.info("\tjava -jar JavaAdvancedImage.jar <imagepath> <region-x> <region-y> <region-width> <region-height>");
            log.info("\nExample (analyze the region [x=10,y=10,width=200,height=10] of frame1.png):");
            log.info("\tjava -jar JavaAdvancedImage.jar frame1.png 10 10 200 10");
            System.exit(0);
        }

        String imagePath = args[0];
        int regionX = 0;
        int regionY = 0;
        int regionWidth = 0;
        int regionHeight = 0;

        // Facciamo dei check basilari sui parametri
        try {
            regionX = Integer.parseInt(args[1]);
            regionY = Integer.parseInt(args[2]);
            regionWidth = Integer.parseInt(args[3]);
            regionHeight = Integer.parseInt(args[4]);
        } catch(Exception e) {
            log.info("La regione che hai specificato non è valida.");
            System.exit(-1);
        }
        
        // Qui analizziamo solamente un fermo immagine
        // L'analyzer cercherà una barra di colore giallo (cambia l'ultimo parametro per cambiare configurazione)
        SimplePatternAnalyzer spa = new SimplePatternAnalyzer(  imagePath,
                                                                new SimplePatternRegion(regionX, regionY, regionWidth, regionHeight),
                                                                new SimplePatternColor(200, 198, 179));

        // Questa è la regione della barra gialla, la sua posizione può variare in base alla registrazione video
        // ed in base alla qualità del filmato.
        Float p = spa.getProgression();

        log.info("***************************************");
        log.info("La progress bar è al " + p*100.0f + "%");
        log.info("***************************************");
    }
}
