### Introduction

A simple project which aims to detect, from an external source (webcam or other), the status (in percentage) of a progress bar diplayed on a screen (e.g. smartphone).

This project has been developed for the automation of testing activities.

You can find the full project article into my blog http://www.bitstorm.it/.

### Details

Small footprint and simply to understand, developed with Java Advanced Imaging API (JAI) and nothing else.

The following diagram shows you how the project works. Basically, starting from a series of still images and by defining the area (x, y, width, height) to check, the recognitor analyzes in which point the progress bar is. The analyzing is done by a basic pixel linear interpolation and with a soft tollerence based on the detected hue.

### How to install

The project is based on Maven project manager. All subsequent maven commands shall be run from the directory containing the project (where the pom.xml file is located):

If you desire to compile and try the recognitor, you should type the command:

``` bash
    # mvn clean install 
```

If you desire to run a demonstration test, you should type the command:

``` bash
    # mvn test 
```

### How to use

Check the source code SimplePatternTest.java which contains a real case used to run the unit test.

The still testing images was derived from an AVI movie with FFMPEG tool. Once installed FFMPEG, the command used to extract single frames from a video file is the following:

``` bash
    # ffmpeg -i mymovie.avi -r 5 -f image2 frame_%3d.png 
```

This last command will generate from the "mymovie.avi" movie a sequence of PNG images called in sequence frame_001.png, frame_002.png and so on. 
