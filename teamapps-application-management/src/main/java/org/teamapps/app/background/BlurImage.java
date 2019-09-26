/*
 * Copyright (c) 2016 teamapps.org (see code comments for author's name)
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

package org.teamapps.app.background;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.util.Random;



/**
 * A filter which applies Gaussian blur to an image. This is a subclass of ConvolveFilter
 * which simply creates a kernel with a Gaussian distribution for blurring.
 * @author Jerry Huxtable
 *
 * Disclaimer
 * There's source code in Java for pretty well everything I talk about here.
 * I make no claims that these are optimised in any way -
 * I've opted for simplicity over speed everywhere and you'll probably
 * be able to make most of these thing go faster with a bit of effort.
 * You can use the source code for anything you want, including
 * commercial purposes, but there's no liability.
 * If your nuclear power station or missile system fails because
 * of an improper blur, it's not my fault.
 *
 * http://www.jhlabs.com/ip/blurring.html
 */
public class BlurImage extends ConvolveFilter {

	private final static Logger logger = LoggerFactory.getLogger(BlurImage.class);

    protected float radius;
    protected Kernel kernel;

	/**
     * Construct a Gaussian filter
     */
    public BlurImage() {
        this(2);
    }

    /**
     * Construct a Gaussian filter
     * @param radius blur radius in pixels
     */
    public BlurImage(float radius) {
        setRadius(radius);
    }


    public void createBlurredImage(File originalFile, File blurredFile) throws IOException {
        BufferedImage blurredImage = ImageIO.read(originalFile);
        int blurRadius = blurredImage.getWidth() / 50;
        setRadius(blurRadius);
        filter(blurredImage, blurredImage);
        ImageIO.write(blurredImage, "jpg", blurredFile);
    }

    /**
     * Set the radius of the kernel, and hence the amount of blur. The bigger the radius, the longer this filter will take.
     * @param radius the radius of the blur in pixels.
     */
    public void setRadius(float radius) {
        this.radius = radius;
        kernel = makeKernel(radius);
    }

    /**
     * Get the radius of the kernel.
     * @return the radius
     */
    public float getRadius() {
        return radius;
    }

    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        int width = src.getWidth();
        int height = src.getHeight();

        if ( dst == null )
            dst = createCompatibleDestImage( src, null );

        int[] inPixels = new int[width*height];
        int[] outPixels = new int[width*height];
        src.getRGB( 0, 0, width, height, inPixels, 0, width );

        convolveAndTranspose(kernel, inPixels, outPixels, width, height, alpha, CLAMP_EDGES);
        convolveAndTranspose(kernel, outPixels, inPixels, height, width, alpha, CLAMP_EDGES);

        dst.setRGB( 0, 0, width, height, inPixels, 0, width );
        return dst;
    }

    public static void convolveAndTranspose(Kernel kernel, int[] inPixels, int[] outPixels, int width, int height, boolean alpha, int edgeAction) {
        float[] matrix = kernel.getKernelData( null );
        int cols = kernel.getWidth();
        int cols2 = cols/2;

        for (int y = 0; y < height; y++) {
            int index = y;
            int ioffset = y*width;
            for (int x = 0; x < width; x++) {
                float r = 0, g = 0, b = 0, a = 0;
                int moffset = cols2;
                for (int col = -cols2; col <= cols2; col++) {
                    float f = matrix[moffset+col];

                    if (f != 0) {
                        int ix = x+col;
                        if ( ix < 0 ) {
                            if ( edgeAction == CLAMP_EDGES )
                                ix = 0;
                            else if ( edgeAction == WRAP_EDGES )
                                ix = (x+width) % width;
                        } else if ( ix >= width) {
                            if ( edgeAction == CLAMP_EDGES )
                                ix = width-1;
                            else if ( edgeAction == WRAP_EDGES )
                                ix = (x+width) % width;
                        }
                        int rgb = inPixels[ioffset+ix];
                        a += f * ((rgb >> 24) & 0xff);
                        r += f * ((rgb >> 16) & 0xff);
                        g += f * ((rgb >> 8) & 0xff);
                        b += f * (rgb & 0xff);
                    }
                }
                int ia = alpha ? PixelUtils.clamp((int)(a+0.5)) : 0xff;
                int ir = PixelUtils.clamp((int)(r+0.5));
                int ig = PixelUtils.clamp((int)(g+0.5));
                int ib = PixelUtils.clamp((int)(b+0.5));
                outPixels[index] = (ia << 24) | (ir << 16) | (ig << 8) | ib;
                index += height;
            }
        }
    }

    /**
     * Make a Gaussian blur kernel.
     */
    public static Kernel makeKernel(float radius) {
        int r = (int)Math.ceil(radius);
        int rows = r*2+1;
        float[] matrix = new float[rows];
        float sigma = radius/3;
        float sigma22 = 2*sigma*sigma;
        float sigmaPi2 = 2* ImageMath.PI*sigma;
        float sqrtSigmaPi2 = (float)Math.sqrt(sigmaPi2);
        float radius2 = radius*radius;
        float total = 0;
        int index = 0;
        for (int row = -r; row <= r; row++) {
            float distance = row*row;
            if (distance > radius2)
                matrix[index] = 0;
            else
                matrix[index] = (float)Math.exp(-(distance)/sigma22) / sqrtSigmaPi2;
            total += matrix[index];
            index++;
        }
        for (int i = 0; i < rows; i++)
            matrix[i] /= total;

        return new Kernel(rows, 1, matrix);
    }

    public String toString() {
        return "Blur/Gaussian Blur...";
    }
}

class ConvolveFilter extends AbstractBufferedImageOp {

    static final long serialVersionUID = 2239251672685254626L;

    public static int ZERO_EDGES = 0;
    public static int CLAMP_EDGES = 1;
    public static int WRAP_EDGES = 2;

    protected Kernel kernel = null;
    public boolean alpha = true;
    private int edgeAction = CLAMP_EDGES;

    /**
     * Construct a filter with a null kernel. This is only useful if you're going to change the kernel later on.
     */
    public ConvolveFilter() {
        this(new float[9]);
    }

    /**
     * Construct a filter with the given 3x3 kernel.
     * @param matrix an array of 9 floats containing the kernel
     */
    public ConvolveFilter(float[] matrix) {
        this(new Kernel(3, 3, matrix));
    }

    /**
     * Construct a filter with the given kernel.
     * @param rows	the number of rows in the kernel
     * @param cols	the number of columns in the kernel
     * @param matrix	an array of rows*cols floats containing the kernel
     */
    public ConvolveFilter(int rows, int cols, float[] matrix) {
        this(new Kernel(cols, rows, matrix));
    }

    /**
     * Construct a filter with the given 3x3 kernel.
     * //@param matrix an array of 9 floats containing the kernel
     */
    public ConvolveFilter(Kernel kernel) {
        this.kernel = kernel;
    }

    public void setKernel(Kernel kernel) {
        this.kernel = kernel;
    }

    public Kernel getKernel() {
        return kernel;
    }

    public void setEdgeAction(int edgeAction) {
        this.edgeAction = edgeAction;
    }

    public int getEdgeAction() {
        return edgeAction;
    }

    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        int width = src.getWidth();
        int height = src.getHeight();

        if ( dst == null )
            dst = createCompatibleDestImage( src, null );

        int[] inPixels = new int[width*height];
        int[] outPixels = new int[width*height];
        getRGB( src, 0, 0, width, height, inPixels );

        convolve(kernel, inPixels, outPixels, width, height, alpha, edgeAction);

        setRGB( dst, 0, 0, width, height, outPixels );
        return dst;
    }

    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel dstCM) {
        if ( dstCM == null )
            dstCM = src.getColorModel();
        return new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight()), dstCM.isAlphaPremultiplied(), null);
    }

    public Rectangle2D getBounds2D( BufferedImage src ) {
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }

    public Point2D getPoint2D( Point2D srcPt, Point2D dstPt ) {
        if ( dstPt == null )
            dstPt = new Point2D.Double();
        dstPt.setLocation( srcPt.getX(), srcPt.getY() );
        return dstPt;
    }

    public RenderingHints getRenderingHints() {
        return null;
    }

    public static void convolve(Kernel kernel, int[] inPixels, int[] outPixels, int width, int height, int edgeAction) {
        convolve(kernel, inPixels, outPixels, width, height, true, edgeAction);
    }

    public static void convolve(Kernel kernel, int[] inPixels, int[] outPixels, int width, int height, boolean alpha, int edgeAction) {
        if (kernel.getHeight() == 1)
            convolveH(kernel, inPixels, outPixels, width, height, alpha, edgeAction);
        else if (kernel.getWidth() == 1)
            convolveV(kernel, inPixels, outPixels, width, height, alpha, edgeAction);
        else
            convolveHV(kernel, inPixels, outPixels, width, height, alpha, edgeAction);
    }

    /**
     * Convolve with a 2D kernel
     */
    public static void convolveHV(Kernel kernel, int[] inPixels, int[] outPixels, int width, int height, boolean alpha, int edgeAction) {
        int index = 0;
        float[] matrix = kernel.getKernelData( null );
        int rows = kernel.getHeight();
        int cols = kernel.getWidth();
        int rows2 = rows/2;
        int cols2 = cols/2;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float r = 0, g = 0, b = 0, a = 0;

                for (int row = -rows2; row <= rows2; row++) {
                    int iy = y+row;
                    int ioffset;
                    if (0 <= iy && iy < height)
                        ioffset = iy*width;
                    else if ( edgeAction == CLAMP_EDGES )
                        ioffset = y*width;
                    else if ( edgeAction == WRAP_EDGES )
                        ioffset = ((iy+height) % height) * width;
                    else
                        continue;
                    int moffset = cols*(row+rows2)+cols2;
                    for (int col = -cols2; col <= cols2; col++) {
                        float f = matrix[moffset+col];

                        if (f != 0) {
                            int ix = x+col;
                            if (!(0 <= ix && ix < width)) {
                                if ( edgeAction == CLAMP_EDGES )
                                    ix = x;
                                else if ( edgeAction == WRAP_EDGES )
                                    ix = (x+width) % width;
                                else
                                    continue;
                            }
                            int rgb = inPixels[ioffset+ix];
                            a += f * ((rgb >> 24) & 0xff);
                            r += f * ((rgb >> 16) & 0xff);
                            g += f * ((rgb >> 8) & 0xff);
                            b += f * (rgb & 0xff);
                        }
                    }
                }
                int ia = alpha ? PixelUtils.clamp((int)(a+0.5)) : 0xff;
                int ir = PixelUtils.clamp((int)(r+0.5));
                int ig = PixelUtils.clamp((int)(g+0.5));
                int ib = PixelUtils.clamp((int)(b+0.5));
                outPixels[index++] = (ia << 24) | (ir << 16) | (ig << 8) | ib;
            }
        }
    }

    /**
     * Convolve with a kernel consisting of one row
     */
    public static void convolveH(Kernel kernel, int[] inPixels, int[] outPixels, int width, int height, boolean alpha, int edgeAction) {
        int index = 0;
        float[] matrix = kernel.getKernelData( null );
        int cols = kernel.getWidth();
        int cols2 = cols/2;

        for (int y = 0; y < height; y++) {
            int ioffset = y*width;
            for (int x = 0; x < width; x++) {
                float r = 0, g = 0, b = 0, a = 0;
                int moffset = cols2;
                for (int col = -cols2; col <= cols2; col++) {
                    float f = matrix[moffset+col];

                    if (f != 0) {
                        int ix = x+col;
                        if ( ix < 0 ) {
                            if ( edgeAction == CLAMP_EDGES )
                                ix = 0;
                            else if ( edgeAction == WRAP_EDGES )
                                ix = (x+width) % width;
                        } else if ( ix >= width) {
                            if ( edgeAction == CLAMP_EDGES )
                                ix = width-1;
                            else if ( edgeAction == WRAP_EDGES )
                                ix = (x+width) % width;
                        }
                        int rgb = inPixels[ioffset+ix];
                        a += f * ((rgb >> 24) & 0xff);
                        r += f * ((rgb >> 16) & 0xff);
                        g += f * ((rgb >> 8) & 0xff);
                        b += f * (rgb & 0xff);
                    }
                }
                int ia = alpha ? PixelUtils.clamp((int)(a+0.5)) : 0xff;
                int ir = PixelUtils.clamp((int)(r+0.5));
                int ig = PixelUtils.clamp((int)(g+0.5));
                int ib = PixelUtils.clamp((int)(b+0.5));
                outPixels[index++] = (ia << 24) | (ir << 16) | (ig << 8) | ib;
            }
        }
    }

    /**
     * Convolve with a kernel consisting of one column
     */
    public static void convolveV(Kernel kernel, int[] inPixels, int[] outPixels, int width, int height, boolean alpha, int edgeAction) {
        int index = 0;
        float[] matrix = kernel.getKernelData( null );
        int rows = kernel.getHeight();
        int rows2 = rows/2;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float r = 0, g = 0, b = 0, a = 0;

                for (int row = -rows2; row <= rows2; row++) {
                    int iy = y+row;
                    int ioffset;
                    if ( iy < 0 ) {
                        if ( edgeAction == CLAMP_EDGES )
                            ioffset = 0;
                        else if ( edgeAction == WRAP_EDGES )
                            ioffset = ((y+height) % height)*width;
                        else
                            ioffset = iy*width;
                    } else if ( iy >= height) {
                        if ( edgeAction == CLAMP_EDGES )
                            ioffset = (height-1)*width;
                        else if ( edgeAction == WRAP_EDGES )
                            ioffset = ((y+height) % height)*width;
                        else
                            ioffset = iy*width;
                    } else
                        ioffset = iy*width;

                    float f = matrix[row+rows2];

                    if (f != 0) {
                        int rgb = inPixels[ioffset+x];
                        a += f * ((rgb >> 24) & 0xff);
                        r += f * ((rgb >> 16) & 0xff);
                        g += f * ((rgb >> 8) & 0xff);
                        b += f * (rgb & 0xff);
                    }
                }
                int ia = alpha ? PixelUtils.clamp((int)(a+0.5)) : 0xff;
                int ir = PixelUtils.clamp((int)(r+0.5));
                int ig = PixelUtils.clamp((int)(g+0.5));
                int ib = PixelUtils.clamp((int)(b+0.5));
                outPixels[index++] = (ia << 24) | (ir << 16) | (ig << 8) | ib;
            }
        }
    }

    public String toString() {
        return "Blur/Convolve...";
    }
}

class ImageMath {

    public final static float PI = (float)Math.PI;
    public final static float HALF_PI = (float)Math.PI/2.0f;
    public final static float QUARTER_PI = (float)Math.PI/4.0f;
    public final static float TWO_PI = (float)Math.PI*2.0f;

    /**
     * Apply a bias to a number in the unit interval, moving numbers towards 0 or 1
     * according to the bias parameter.
     * @param a the number to bias
     * @param b the bias parameter. 0.5 means no change, smaller values bias towards 0, larger towards 1.
     * @return the output value
     */
    public static float bias(float a, float b) {
        return a/((1.0f/b-2)*(1.0f-a)+1);
    }

    /**
     * A variant of the gamma function.
     * @param a the number to apply gain to
     * @param b the gain parameter. 0.5 means no change, smaller values reduce gain, larger values increase gain.
     * @return the output value
     */
    public static float gain(float a, float b) {

        float c = (1.0f/b-2.0f) * (1.0f-2.0f*a);
        if (a < 0.5)
            return a/(c+1.0f);
        else
            return (c-a)/(c-1.0f);
    }

    /**
     * The step function. Returns 0 below a threshold, 1 above.
     * @param a the threshold position
     * @param x the input parameter
     * @return the output value - 0 or 1
     */
    public static float step(float a, float x) {
        return (x < a) ? 0.0f : 1.0f;
    }

    /**
     * The pulse function. Returns 1 between two thresholds, 0 outside.
     * @param a the lower threshold position
     * @param b the upper threshold position
     * @param x the input parameter
     * @return the output value - 0 or 1
     */
    public static float pulse(float a, float b, float x) {
        return (x < a || x >= b) ? 0.0f : 1.0f;
    }

    /**
     * A smoothed pulse function. A cubic function is used to smooth the step between two thresholds.
     * @param a1 the lower threshold position for the start of the pulse
     * @param a2 the upper threshold position for the start of the pulse
     * @param b1 the lower threshold position for the end of the pulse
     * @param b2 the upper threshold position for the end of the pulse
     * @param x the input parameter
     * @return the output value
     */
    public static float smoothPulse(float a1, float a2, float b1, float b2, float x) {
        if (x < a1 || x >= b2)
            return 0;
        if (x >= a2) {
            if (x < b1)
                return 1.0f;
            x = (x - b1) / (b2 - b1);
            return 1.0f - (x*x * (3.0f - 2.0f*x));
        }
        x = (x - a1) / (a2 - a1);
        return x*x * (3.0f - 2.0f*x);
    }

    /**
     * A smoothed step function. A cubic function is used to smooth the step between two thresholds.
     * @param a the lower threshold position
     * @param b the upper threshold position
     * @param x the input parameter
     * @return the output value
     */
    public static float smoothStep(float a, float b, float x) {
        if (x < a)
            return 0;
        if (x >= b)
            return 1;
        x = (x - a) / (b - a);
        return x*x * (3 - 2*x);
    }

    /**
     * A "circle up" function. Returns y on a unit circle given 1-x. Useful for forming bevels.
     * @param x the input parameter in the range 0..1
     * @return the output value
     */
    public static float circleUp(float x) {
        x = 1-x;
        return (float)Math.sqrt(1-x*x);
    }

    /**
     * A "circle down" function. Returns 1-y on a unit circle given x. Useful for forming bevels.
     * @param x the input parameter in the range 0..1
     * @return the output value
     */
    public static float circleDown(float x) {
        return 1.0f-(float)Math.sqrt(1-x*x);
    }

    /**
     * Clamp a value to an interval.
     * @param a the lower clamp threshold
     * @param b the upper clamp threshold
     * @param x the input parameter
     * @return the clamped value
     */
    public static float clamp(float x, float a, float b) {
        return (x < a) ? a : (x > b) ? b : x;
    }

    /**
     * Clamp a value to an interval.
     * @param a the lower clamp threshold
     * @param b the upper clamp threshold
     * @param x the input parameter
     * @return the clamped value
     */
    public static int clamp(int x, int a, int b) {
        return (x < a) ? a : (x > b) ? b : x;
    }

    /**
     * Return a mod b. This differs from the % operator with respect to negative numbers.
     * @param a the dividend
     * @param b the divisor
     * @return a mod b
     */
    public static double mod(double a, double b) {
        int n = (int)(a/b);

        a -= n*b;
        if (a < 0)
            return a + b;
        return a;
    }

    /**
     * Return a mod b. This differs from the % operator with respect to negative numbers.
     * @param a the dividend
     * @param b the divisor
     * @return a mod b
     */
    public static float mod(float a, float b) {
        int n = (int)(a/b);

        a -= n*b;
        if (a < 0)
            return a + b;
        return a;
    }

    /**
     * Return a mod b. This differs from the % operator with respect to negative numbers.
     * @param a the dividend
     * @param b the divisor
     * @return a mod b
     */
    public static int mod(int a, int b) {
        int n = a/b;

        a -= n*b;
        if (a < 0)
            return a + b;
        return a;
    }

    /**
     * The triangle function. Returns a repeating triangle shape in the range 0..1 with wavelength 1.0
     * @param x the input parameter
     * @return the output value
     */
    public static float triangle(float x) {
        float r = mod(x, 1.0f);
        return 2.0f*(r < 0.5 ? r : 1-r);
    }

    /**
     * Linear interpolation.
     * @param t the interpolation parameter
     * @param a the lower interpolation range
     * @param b the upper interpolation range
     * @return the interpolated value
     */
    public static float lerp(float t, float a, float b) {
        return a + t * (b - a);
    }

    /**
     * Linear interpolation.
     * @param t the interpolation parameter
     * @param a the lower interpolation range
     * @param b the upper interpolation range
     * @return the interpolated value
     */
    public static int lerp(float t, int a, int b) {
        return (int)(a + t * (b - a));
    }

    /**
     * Linear interpolation of ARGB values.
     * @param t the interpolation parameter
     * @param rgb1 the lower interpolation range
     * @param rgb2 the upper interpolation range
     * @return the interpolated value
     */
    public static int mixColors(float t, int rgb1, int rgb2) {
        int a1 = (rgb1 >> 24) & 0xff;
        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >> 8) & 0xff;
        int b1 = rgb1 & 0xff;
        int a2 = (rgb2 >> 24) & 0xff;
        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >> 8) & 0xff;
        int b2 = rgb2 & 0xff;
        a1 = lerp(t, a1, a2);
        r1 = lerp(t, r1, r2);
        g1 = lerp(t, g1, g2);
        b1 = lerp(t, b1, b2);
        return (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
    }

    /**
     * Bilinear interpolation of ARGB values.
     * @param x the X interpolation parameter 0..1
     * @param y the y interpolation parameter 0..1
     * //@param rgb array of four ARGB values in the order NW, NE, SW, SE
     * @return the interpolated value
     */
    public static int bilinearInterpolate(float x, float y, int[] p) {
        float m0, m1;
        int a0 = (p[0] >> 24) & 0xff;
        int r0 = (p[0] >> 16) & 0xff;
        int g0 = (p[0] >> 8) & 0xff;
        int b0 = p[0] & 0xff;
        int a1 = (p[1] >> 24) & 0xff;
        int r1 = (p[1] >> 16) & 0xff;
        int g1 = (p[1] >> 8) & 0xff;
        int b1 = p[1] & 0xff;
        int a2 = (p[2] >> 24) & 0xff;
        int r2 = (p[2] >> 16) & 0xff;
        int g2 = (p[2] >> 8) & 0xff;
        int b2 = p[2] & 0xff;
        int a3 = (p[3] >> 24) & 0xff;
        int r3 = (p[3] >> 16) & 0xff;
        int g3 = (p[3] >> 8) & 0xff;
        int b3 = p[3] & 0xff;

        float cx = 1.0f-x;
        float cy = 1.0f-y;

        m0 = cx * a0 + x * a1;
        m1 = cx * a2 + x * a3;
        int a = (int)(cy * m0 + y * m1);

        m0 = cx * r0 + x * r1;
        m1 = cx * r2 + x * r3;
        int r = (int)(cy * m0 + y * m1);

        m0 = cx * g0 + x * g1;
        m1 = cx * g2 + x * g3;
        int g = (int)(cy * m0 + y * m1);

        m0 = cx * b0 + x * b1;
        m1 = cx * b2 + x * b3;
        int b = (int)(cy * m0 + y * m1);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Return the NTSC gray level of an RGB value.
     * //@param rgb1 the input pixel
     * @return the gray level (0-255)
     */
    public static int brightnessNTSC(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        return (int)(r*0.299f + g*0.587f + b*0.114f);
    }

    // Catmull-Rom splines
    private final static float m00 = -0.5f;
    private final static float m01 =  1.5f;
    private final static float m02 = -1.5f;
    private final static float m03 =  0.5f;
    private final static float m10 =  1.0f;
    private final static float m11 = -2.5f;
    private final static float m12 =  2.0f;
    private final static float m13 = -0.5f;
    private final static float m20 = -0.5f;
    private final static float m21 =  0.0f;
    private final static float m22 =  0.5f;
    private final static float m23 =  0.0f;
    private final static float m30 =  0.0f;
    private final static float m31 =  1.0f;
    private final static float m32 =  0.0f;
    private final static float m33 =  0.0f;

    /**
     * Compute a Catmull-Rom spline.
     * @param x the input parameter
     * @param numKnots the number of knots in the spline
     * @param knots the array of knots
     * @return the spline value
     */
    public static float spline(float x, int numKnots, float[] knots) {
        int span;
        int numSpans = numKnots - 3;
        float k0, k1, k2, k3;
        float c0, c1, c2, c3;

        if (numSpans < 1)
            throw new IllegalArgumentException("Too few knots in spline");

        x = clamp(x, 0, 1) * numSpans;
        span = (int)x;
        if (span > numKnots-4)
            span = numKnots-4;
        x -= span;

        k0 = knots[span];
        k1 = knots[span+1];
        k2 = knots[span+2];
        k3 = knots[span+3];

        c3 = m00*k0 + m01*k1 + m02*k2 + m03*k3;
        c2 = m10*k0 + m11*k1 + m12*k2 + m13*k3;
        c1 = m20*k0 + m21*k1 + m22*k2 + m23*k3;
        c0 = m30*k0 + m31*k1 + m32*k2 + m33*k3;

        return ((c3*x + c2)*x + c1)*x + c0;
    }

    /**
     * Compute a Catmull-Rom spline, but with variable knot spacing.
     * @param x the input parameter
     * @param numKnots the number of knots in the spline
     * @param xknots the array of knot x values
     * @param yknots the array of knot y values
     * @return the spline value
     */
    public static float spline(float x, int numKnots, int[] xknots, int[] yknots) {
        int span;
        int numSpans = numKnots - 3;
        float k0, k1, k2, k3;
        float c0, c1, c2, c3;

        if (numSpans < 1)
            throw new IllegalArgumentException("Too few knots in spline");

        for (span = 0; span < numSpans; span++)
            if (xknots[span+1] > x)
                break;
        if (span > numKnots-3)
            span = numKnots-3;
        float t = (float)(x-xknots[span]) / (xknots[span+1]-xknots[span]);
        span--;
        if (span < 0) {
            span = 0;
            t = 0;
        }

        k0 = yknots[span];
        k1 = yknots[span+1];
        k2 = yknots[span+2];
        k3 = yknots[span+3];

        c3 = m00*k0 + m01*k1 + m02*k2 + m03*k3;
        c2 = m10*k0 + m11*k1 + m12*k2 + m13*k3;
        c1 = m20*k0 + m21*k1 + m22*k2 + m23*k3;
        c0 = m30*k0 + m31*k1 + m32*k2 + m33*k3;

        return ((c3*t + c2)*t + c1)*t + c0;
    }

    /**
     * Compute a Catmull-Rom spline for RGB values.
     * @param x the input parameter
     * @param numKnots the number of knots in the spline
     * @param knots the array of knots
     * @return the spline value
     */
    public static int colorSpline(float x, int numKnots, int[] knots) {
        int span;
        int numSpans = numKnots - 3;
        float k0, k1, k2, k3;
        float c0, c1, c2, c3;

        if (numSpans < 1)
            throw new IllegalArgumentException("Too few knots in spline");

        x = clamp(x, 0, 1) * numSpans;
        span = (int)x;
        if (span > numKnots-4)
            span = numKnots-4;
        x -= span;

        int v = 0;
        for (int i = 0; i < 4; i++) {
            int shift = i * 8;

            k0 = (knots[span] >> shift) & 0xff;
            k1 = (knots[span+1] >> shift) & 0xff;
            k2 = (knots[span+2] >> shift) & 0xff;
            k3 = (knots[span+3] >> shift) & 0xff;

            c3 = m00*k0 + m01*k1 + m02*k2 + m03*k3;
            c2 = m10*k0 + m11*k1 + m12*k2 + m13*k3;
            c1 = m20*k0 + m21*k1 + m22*k2 + m23*k3;
            c0 = m30*k0 + m31*k1 + m32*k2 + m33*k3;
            int n = (int)(((c3*x + c2)*x + c1)*x + c0);
            if (n < 0)
                n = 0;
            else if (n > 255)
                n = 255;
            v |= n << shift;
        }

        return v;
    }

    /**
     * Compute a Catmull-Rom spline for RGB values, but with variable knot spacing.
     * @param x the input parameter
     * @param numKnots the number of knots in the spline
     * @param xknots the array of knot x values
     * @param yknots the array of knot y values
     * @return the spline value
     */
    public static int colorSpline(int x, int numKnots, int[] xknots, int[] yknots) {
        int span;
        int numSpans = numKnots - 3;
        float k0, k1, k2, k3;
        float c0, c1, c2, c3;

        if (numSpans < 1)
            throw new IllegalArgumentException("Too few knots in spline");

        for (span = 0; span < numSpans; span++)
            if (xknots[span+1] > x)
                break;
        if (span > numKnots-3)
            span = numKnots-3;
        float t = (float)(x-xknots[span]) / (xknots[span+1]-xknots[span]);
        span--;
        if (span < 0) {
            span = 0;
            t = 0;
        }

        int v = 0;
        for (int i = 0; i < 4; i++) {
            int shift = i * 8;

            k0 = (yknots[span] >> shift) & 0xff;
            k1 = (yknots[span+1] >> shift) & 0xff;
            k2 = (yknots[span+2] >> shift) & 0xff;
            k3 = (yknots[span+3] >> shift) & 0xff;

            c3 = m00*k0 + m01*k1 + m02*k2 + m03*k3;
            c2 = m10*k0 + m11*k1 + m12*k2 + m13*k3;
            c1 = m20*k0 + m21*k1 + m22*k2 + m23*k3;
            c0 = m30*k0 + m31*k1 + m32*k2 + m33*k3;
            int n = (int)(((c3*t + c2)*t + c1)*t + c0);
            if (n < 0)
                n = 0;
            else if (n > 255)
                n = 255;
            v |= n << shift;
        }

        return v;
    }

    /**
     * An implementation of Fant's resampling algorithm.
     * @param source the source pixels
     * @param dest the destination pixels
     * @param length the length of the scanline to resample
     * @param offset the start offset into the arrays
     * @param stride the offset between pixels in consecutive rows
     * @param out an array of output positions for each pixel
     */
    public static void resample(int[] source, int[] dest, int length, int offset, int stride, float[] out) {
        int i, j;
        float intensity;
        float sizfac;
        float inSegment;
        float outSegment;
        int a, r, g, b, nextA, nextR, nextG, nextB;
        float aSum, rSum, gSum, bSum;
        float[] in;
        int srcIndex = offset;
        int destIndex = offset;
        int lastIndex = source.length;
        int rgb;

        in = new float[length+1];
        i = 0;
        for (j = 0; j < length; j++) {
            while (out[i+1] < j)
                i++;
            in[j] = i + (float) (j - out[i]) / (out[i + 1] - out[i]);
        }
        in[length] = length;

        inSegment  = 1.0f;
        outSegment = in[1];
        sizfac = outSegment;
        aSum = rSum = gSum = bSum = 0.0f;
        rgb = source[srcIndex];
        a = (rgb >> 24) & 0xff;
        r = (rgb >> 16) & 0xff;
        g = (rgb >> 8) & 0xff;
        b = rgb & 0xff;
        srcIndex += stride;
        rgb = source[srcIndex];
        nextA = (rgb >> 24) & 0xff;
        nextR = (rgb >> 16) & 0xff;
        nextG = (rgb >> 8) & 0xff;
        nextB = rgb & 0xff;
        srcIndex += stride;
        i = 1;

        while (i < length) {
            float aIntensity = inSegment * a + (1.0f - inSegment) * nextA;
            float rIntensity = inSegment * r + (1.0f - inSegment) * nextR;
            float gIntensity = inSegment * g + (1.0f - inSegment) * nextG;
            float bIntensity = inSegment * b + (1.0f - inSegment) * nextB;
            if (inSegment < outSegment) {
                aSum += (aIntensity * inSegment);
                rSum += (rIntensity * inSegment);
                gSum += (gIntensity * inSegment);
                bSum += (bIntensity * inSegment);
                outSegment -= inSegment;
                inSegment = 1.0f;
                a = nextA;
                r = nextR;
                g = nextG;
                b = nextB;
                if (srcIndex < lastIndex)
                    rgb = source[srcIndex];
                nextA = (rgb >> 24) & 0xff;
                nextR = (rgb >> 16) & 0xff;
                nextG = (rgb >> 8) & 0xff;
                nextB = rgb & 0xff;
                srcIndex += stride;
            } else {
                aSum += (aIntensity * outSegment);
                rSum += (rIntensity * outSegment);
                gSum += (gIntensity * outSegment);
                bSum += (bIntensity * outSegment);
                dest[destIndex] =
                        ((int)Math.min(aSum/sizfac, 255) << 24) |
                                ((int)Math.min(rSum/sizfac, 255) << 16) |
                                ((int)Math.min(gSum/sizfac, 255) << 8) |
                                (int)Math.min(bSum/sizfac, 255);
                destIndex += stride;
                rSum = gSum = bSum = 0.0f;
                inSegment -= outSegment;
                outSegment = in[i+1] - in[i];
                sizfac = outSegment;
                i++;
            }
        }
    }

}

class PixelUtils {

    public final static int REPLACE = 0;
    public final static int NORMAL = 1;
    public final static int MIN = 2;
    public final static int MAX = 3;
    public final static int ADD = 4;
    public final static int SUBTRACT = 5;
    public final static int DIFFERENCE = 6;
    public final static int MULTIPLY = 7;
    public final static int HUE = 8;
    public final static int SATURATION = 9;
    public final static int VALUE = 10;
    public final static int COLOR = 11;
    public final static int SCREEN = 12;
    public final static int AVERAGE = 13;
    public final static int OVERLAY = 14;
    public final static int CLEAR = 15;
    public final static int EXCHANGE = 16;
    public final static int DISSOLVE = 17;
    public final static int DST_IN = 18;
    public final static int ALPHA = 19;
    public final static int ALPHA_TO_GRAY = 20;

    private static Random randomGenerator = new Random();

    /**
     * Clamp a value to the range 0..255
     */
    public static int clamp(int c) {
        if (c < 0)
            return 0;
        if (c > 255)
            return 255;
        return c;
    }

    public static int interpolate(int v1, int v2, float f) {
        return clamp((int)(v1+f*(v2-v1)));
    }

    public static int brightness(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        return (r+g+b)/3;
    }

    public static boolean nearColors(int rgb1, int rgb2, int tolerance) {
        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >> 8) & 0xff;
        int b1 = rgb1 & 0xff;
        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >> 8) & 0xff;
        int b2 = rgb2 & 0xff;
        return Math.abs(r1-r2) <= tolerance && Math.abs(g1-g2) <= tolerance && Math.abs(b1-b2) <= tolerance;
    }

    private final static float hsb1[] = new float[3];//FIXME-not thread safe
    private final static float hsb2[] = new float[3];//FIXME-not thread safe

    // Return rgb1 painted onto rgb2
    public static int combinePixels(int rgb1, int rgb2, int op) {
        return combinePixels(rgb1, rgb2, op, 0xff);
    }

    public static int combinePixels(int rgb1, int rgb2, int op, int extraAlpha, int channelMask) {
        return (rgb2 & ~channelMask) | combinePixels(rgb1 & channelMask, rgb2, op, extraAlpha);
    }

    public static int combinePixels(int rgb1, int rgb2, int op, int extraAlpha) {
        if (op == REPLACE)
            return rgb1;
        int a1 = (rgb1 >> 24) & 0xff;
        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >> 8) & 0xff;
        int b1 = rgb1 & 0xff;
        int a2 = (rgb2 >> 24) & 0xff;
        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >> 8) & 0xff;
        int b2 = rgb2 & 0xff;

        switch (op) {
            case NORMAL:
                break;
            case MIN:
                r1 = Math.min(r1, r2);
                g1 = Math.min(g1, g2);
                b1 = Math.min(b1, b2);
                break;
            case MAX:
                r1 = Math.max(r1, r2);
                g1 = Math.max(g1, g2);
                b1 = Math.max(b1, b2);
                break;
            case ADD:
                r1 = clamp(r1+r2);
                g1 = clamp(g1+g2);
                b1 = clamp(b1+b2);
                break;
            case SUBTRACT:
                r1 = clamp(r2-r1);
                g1 = clamp(g2-g1);
                b1 = clamp(b2-b1);
                break;
            case DIFFERENCE:
                r1 = clamp(Math.abs(r1-r2));
                g1 = clamp(Math.abs(g1-g2));
                b1 = clamp(Math.abs(b1-b2));
                break;
            case MULTIPLY:
                r1 = clamp(r1*r2/255);
                g1 = clamp(g1*g2/255);
                b1 = clamp(b1*b2/255);
                break;
            case DISSOLVE:
                if ((randomGenerator.nextInt() & 0xff) <= a1) {
                    r1 = r2;
                    g1 = g2;
                    b1 = b2;
                }
                break;
            case AVERAGE:
                r1 = (r1+r2)/2;
                g1 = (g1+g2)/2;
                b1 = (b1+b2)/2;
                break;
            case HUE:
            case SATURATION:
            case VALUE:
            case COLOR:
                Color.RGBtoHSB(r1, g1, b1, hsb1);
                Color.RGBtoHSB(r2, g2, b2, hsb2);
                switch (op) {
                    case HUE:
                        hsb2[0] = hsb1[0];
                        break;
                    case SATURATION:
                        hsb2[1] = hsb1[1];
                        break;
                    case VALUE:
                        hsb2[2] = hsb1[2];
                        break;
                    case COLOR:
                        hsb2[0] = hsb1[0];
                        hsb2[1] = hsb1[1];
                        break;
                }
                rgb1 = Color.HSBtoRGB(hsb2[0], hsb2[1], hsb2[2]);
                r1 = (rgb1 >> 16) & 0xff;
                g1 = (rgb1 >> 8) & 0xff;
                b1 = rgb1 & 0xff;
                break;
            case SCREEN:
                r1 = 255 - ((255 - r1) * (255 - r2)) / 255;
                g1 = 255 - ((255 - g1) * (255 - g2)) / 255;
                b1 = 255 - ((255 - b1) * (255 - b2)) / 255;
                break;
            case OVERLAY:
                int m, s;
                s = 255 - ((255 - r1) * (255 - r2)) / 255;
                m = r1 * r2 / 255;
                r1 = (s * r1 + m * (255 - r1)) / 255;
                s = 255 - ((255 - g1) * (255 - g2)) / 255;
                m = g1 * g2 / 255;
                g1 = (s * g1 + m * (255 - g1)) / 255;
                s = 255 - ((255 - b1) * (255 - b2)) / 255;
                m = b1 * b2 / 255;
                b1 = (s * b1 + m * (255 - b1)) / 255;
                break;
            case CLEAR:
                r1 = g1 = b1 = 0xff;
                break;
            case DST_IN:
                r1 = clamp((r2*a1)/255);
                g1 = clamp((g2*a1)/255);
                b1 = clamp((b2*a1)/255);
                a1 = clamp((a2*a1)/255);
                return (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
            case ALPHA:
                a1 = a1*a2/255;
                return (a1 << 24) | (r2 << 16) | (g2 << 8) | b2;
            case ALPHA_TO_GRAY:
                int na = 255-a1;
                return (a1 << 24) | (na << 16) | (na << 8) | na;
        }
        if (extraAlpha != 0xff || a1 != 0xff) {
            a1 = a1*extraAlpha/255;
            int a3 = (255-a1)*a2/255;
            r1 = clamp((r1*a1+r2*a3)/255);
            g1 = clamp((g1*a1+g2*a3)/255);
            b1 = clamp((b1*a1+b2*a3)/255);
            a1 = clamp(a1+a3);
        }
        return (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
    }

}

abstract class AbstractBufferedImageOp implements BufferedImageOp {

    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel dstCM) {
        if ( dstCM == null )
            dstCM = src.getColorModel();
        return new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight()), dstCM.isAlphaPremultiplied(), null);
    }

    public Rectangle2D getBounds2D( BufferedImage src ) {
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }

    public Point2D getPoint2D( Point2D srcPt, Point2D dstPt ) {
        if ( dstPt == null )
            dstPt = new Point2D.Double();
        dstPt.setLocation( srcPt.getX(), srcPt.getY() );
        return dstPt;
    }

    public RenderingHints getRenderingHints() {
        return null;
    }

    /**
     * A convenience method for getting ARGB pixels from an image. This tries to avoid the performance
     * penalty of BufferedImage.getRGB unmanaging the image.
     */
    public int[] getRGB( BufferedImage image, int x, int y, int width, int height, int[] pixels ) {
        int type = image.getType();
        if ( type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB )
            return (int [])image.getRaster().getDataElements( x, y, width, height, pixels );
        return image.getRGB( x, y, width, height, pixels, 0, width );
    }

    /**
     * A convenience method for setting ARGB pixels in an image. This tries to avoid the performance
     * penalty of BufferedImage.setRGB unmanaging the image.
     */
    public void setRGB( BufferedImage image, int x, int y, int width, int height, int[] pixels ) {
        int type = image.getType();
        if ( type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB )
            image.getRaster().setDataElements( x, y, width, height, pixels );
        else
            image.setRGB( x, y, width, height, pixels, 0, width );
    }
}