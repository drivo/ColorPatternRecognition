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

import com.sun.media.jai.codec.PNGEncodeParam;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import org.apache.log4j.Logger;

/**
 * Questa classe implementa un semplice analizzatore di regione.
 * Il contetto di base è molto semplice. Data un'immagine, si delimita un regione
 * di analisi e poi si stabilisce un colore da tracciare (quello della progress bar).
 * L'analizzare avvia uno scanning orizzontale della regione e traccia la posizione
 * approssimativa della parte colorata rispetto al resto della regione.
 *
 * @author guido d'albore
 */
public class SimplePatternAnalyzer {
    Logger log = Logger.getLogger(SimplePatternAnalyzer.class);

    // 5% di tolleranza colore
    final static float  COLOR_TOLERANCE         = 0.05f;
    final static int    ANALYSYS_REGION_WIDTH   = 3;

    RenderedOp image;
    SimplePatternRegion region;
    float progression;
    SimplePatternColor patternColor;
    
    public SimplePatternAnalyzer(String fileName, SimplePatternRegion region, SimplePatternColor patternColor) {
        this.progression = 0.0f;
        this.patternColor = patternColor;

        log.debug("Sto caricando il file \"" + fileName + "\"...");
        try {
            // Carichiamo il file
            image = JAI.create("fileload", fileName);
            // Estraiamo la regione di interesse dall'immagine
            image = cropImage(image, region);
            // Ci serve la regione in formato RGB
            image = convertToRGB(image);

            log.debug("Progress bar width: " + image.getWidth());
            log.debug("Progress bar height: " + image.getHeight());

            ///////////////////////////////////////////////////////////////////////////////////////
            // Caso di debug, salva la regione (dovrebbe essere la progress bar)
            // in un file separato per ulteriori analisi ad occhio nudo
            File f = new File(fileName);
            String filename = f.getName().substring(0, f.getName().lastIndexOf('.'));
            String output = f.getParentFile() + "/" + filename + "-debug.png";
            JAI.create("filestore", image, output, "PNG", PNGEncodeParam.getDefaultEncodeParam(image));
            ///////////////////////////////////////////////////////////////////////////////////////

            // A questo punto abbiamo tutto quello che ci server per calcolare
            // la progressione della regione colorata
            calculateProgression();
        } catch(Exception e) {
            log.debug("Non posso caricare l'immagine!");
            e.printStackTrace();
        }
    }

    public void calculateProgression() {
        int lastProgressIndicator = 0;
        int hsv[];
        // L'algoritmo è basato sul concetto di tonalità e non di componente
        // primaria (e.g. Rosso, blu o verde). Questo ci da la possibilà di
        // svincolarci da una eventuale alterazione del colore dovuta a fattori di
        // saturazione e luminosità.
        int referenceHue = patternColor.getHue();

        // Ho pensato di mettere anche un filo di tolleranza per il matching
        // del colore.
        int lowerHue = referenceHue - (int)((float)360.0f * COLOR_TOLERANCE);
        int upperHue = referenceHue + (int)((float)360.0f * COLOR_TOLERANCE);

        // Questo ci riporta il valore percentuale di un singolo pixel analizzato
        float progressRatio = 100.0f / image.getWidth();

        log.debug("Progress ratio (quanto vale un pixel in percentuale di barra): " + progressRatio + "%");
        log.debug("Reference hue (tonalità di riferimento): " + referenceHue);
        log.debug("Upper hue: " + upperHue);
        log.debug("Lower hue: " + lowerHue);

        // Scanning orizzontale della regione. Questo è il cuore dell'algoritmo,
        // adesso in versione molto minimale, tanto per dare l'idea del concetto.
        //
        // Da ottimi risultati se l'immagine è di buona qualità.
        // Ha funzionato egregiamente (solamente un piccolo margine di errore) su
        // immagini di scarsissima qualità (tipo il video che mi hai mandato).
        //
        // Si procedere orizzontalmente per una quantità di pixel pari a
        // ANALYSYS_REGION_WIDTH (configurabile), fino alla fine della regione.
        // La sotto-regione analizzata è interpolata (i pixel non saranno mai
        // tutti uguali e dello stesso colore) e quindi valutata rispetto
        // alla tonalità media della sotto-regione.
        for(int x = 0; x < image.getWidth(); x += ANALYSYS_REGION_WIDTH) {
            log.debug("Nuova sotto-regione...");
            log.debug("Sto analizzando la sotto-regione (x,y,width,height): " + x + ",0," + ANALYSYS_REGION_WIDTH + "," + image.getHeight());

            Color interpolatedColor = getInterpolatedColor(image, new Rectangle(x, 0, ANALYSYS_REGION_WIDTH, image.getHeight()));
            log.debug("Colore interpolato della sotto-regione (R,G,B): " + interpolatedColor.getRed() + "," + interpolatedColor.getBlue() + ","+ interpolatedColor.getGreen());

            SimplePatternColor ci = new SimplePatternColor(interpolatedColor);
            log.debug("Colore interpolato della sotto-regione (H,S,V): " + ci.getHue() + "," + ci.getSaturation() + "," + ci.getBrightness());

            if((ci.getHue() >= lowerHue) && (ci.getHue() <= upperHue)) {
                lastProgressIndicator = x;
                log.debug("Color matching: positivo");
            } else {
                log.debug("Color matching: negativo");
            }
            
        }

        setProgression((progressRatio * (float)lastProgressIndicator)/100.0f);
    }
    
    public Color getInterpolatedColor(RenderedOp image, Rectangle rect) {
        Color c;
        int r = 0, g = 0, b = 0, count = 0;
        int baseX, baseY, limitX, limitY;
        BufferedImage bi;
        
        bi = image.getAsBufferedImage();
        baseX = (int)rect.getX();
        limitX = (int)Math.min(rect.getX() + rect.getWidth(), image.getWidth());
        baseY = (int)rect.getY();
        limitY = (int)Math.min(rect.getY()+rect.getHeight(), image.getHeight());

        // Semplice interpolazione di una regione grafica.
        for(int y = baseY; y < limitY; y++) {
            for(int x = baseX; x < limitX; x++) {
                c = new Color(bi.getRGB(x, y));
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
                count++;
            }
        }

        if(count != 0) {
            // Interpola i colori
            r /= count;
            g /= count;
            b /= count;
            c = new Color(r, g, b);
        } else {
            // Caso particolare, quando la regione è nulla e non ci sono pixel
            // da interpolare
            c = new Color(0);
        }
        
        return c;
    }

    public RenderedOp cropImage(RenderedOp image, Rectangle r) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(image);
        pb.add((float)r.getX());
        pb.add((float)r.getY());
        pb.add((float)r.getWidth());
        pb.add((float)r.getHeight());
        image = JAI.create("crop", pb);
        return image;
    }

    public RenderedOp convertToRGB(RenderedOp image) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(image);
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ColorModel cm;

        int bits[] = new int[] {8,8,8};

        cm = new ComponentColorModel(cs, bits, false, false,
                                                Transparency.OPAQUE,
                                                DataBuffer.TYPE_BYTE);

        pb.add(cm);
        image = JAI.create("ColorConvert", pb);
        return image;
    }
    
    public SimplePatternRegion getRegion() {
        return region;
    }

    public void setRegion(SimplePatternRegion region) {
        this.region = region;
    }

    public float getProgression() {
        return progression;
    }

    private void setProgression(float progression) {
        this.progression = progression;
    }

}
