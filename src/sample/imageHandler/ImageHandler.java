package sample.imageHandler;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.layout.Pane;
import sample.bmp.BMPWriter;
import sample.exceptions.OutOfBorderException;
import sample.imageFilters.MirrorFilter;
import sample.tga.TGADecoder;
import sample.util.ConvertByteBufferToPixelsArray;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ImageHandler {
    private int[][] pixels = null;
    private int[][] cuttedPixels = null;
    private double zoom = 1;
    private int xImg, yImg, wImg, hImg; // переменные в которых хранятся координаты части картинки которую над показывать.
    private Image image1 = null;
    private TGADecoder tgaDecoder;

    public ImageHandler() {

    }

    public int getxImg() {
        return xImg;
    }

    public void setxImg(int xImg) {
        this.xImg = xImg;
    }

    public int getyImg() {
        return yImg;
    }

    public void setyImg(int yImg) {
        this.yImg = yImg;
    }

    public int getwImg() {
        return wImg;
    }

    public void setwImg(int wImg) {
        this.wImg = wImg;
    }

    public int gethImg() {
        return hImg;
    }

    public void sethImg(int hImg) {
        this.hImg = hImg;
    }

    public void openFile(String path) {
        InputStream is = null;
        try {
            is = new BufferedInputStream(
                    new FileInputStream(path));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        tgaDecoder = new TGADecoder(is);

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(tgaDecoder.getImageSizeInBytes());
        tgaDecoder.decode(byteBuffer);

        pixels = ConvertByteBufferToPixelsArray.convert(byteBuffer,
                tgaDecoder.getWidth(), tgaDecoder.getHeight(), tgaDecoder.getBytesPerPixel());

        setPixels(pixels, tgaDecoder.getWidth(), tgaDecoder.getHeight());
    }

    public BufferedImage getCut(int x, int y, int w, int h) {

        BufferedImage img1 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        cuttedPixels = new int[h][w];
        int k, l = 0;

        for (int i = y; i < h + y; i++) {
            k = 0;
            for (int j = x; j < x + w; j++) {
                cuttedPixels[l][k] = pixels[i][j];
                img1.setRGB(k++, l, pixels[i][j]);
            }
            l++;
        }

        return img1;
    }

    public int[][] convertIntoMatrix(BufferedImage img){
        int height = img.getHeight();
        int width = img.getWidth();

        int[][] matrix = new int[height + 1][width + 1];

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                matrix[i][j] = img.getRGB(j,i);
            }
        }
        return matrix;
    }

    public void saveFile(String path) {
        BMPWriter.save(pixels, tgaDecoder.getBytesPerPixel(), path);
    }

    public void placeImage(int x, int y, int w, int h){
        int k, l =0;
        for (int i = y; i < y + h; i++) {
            k = 0;
            for (int j = x; j < x + w; j++) {
                pixels[i][j] = cuttedPixels[l][k++];
            }
            l++;
        }
        setPixels(pixels, wImg, hImg);
    }

    public BufferedImage resize(int zoom){

        wImg *= zoom;
        hImg *= zoom;

        int[][]newpixels = new int[hImg][wImg];


        return (BufferedImage) image1;
    }

    public void MirrorFilter() {
        pixels = MirrorFilter.fromArrayToArray(pixels, tgaDecoder.getHeight(), tgaDecoder.getWidth());
        // panelFilter.setPixels(pixels, tgaDecoder.getWidth(), tgaDecoder.getHeight());
        setPixels(pixels, tgaDecoder.getWidth(), tgaDecoder.getHeight());
    }


    public BufferedImage setPixels(int[][] pixels, int W, int H) {

        this.pixels = pixels;

        zoom = 1;
        xImg = 0;
        yImg = 0;
        wImg = W;
        hImg = H;

        image1 = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < hImg; i++) {
            for (int j = 0; j < wImg; j++) {
                //((BufferedImage) image1).setRGB(j, hImg - 1 - i, pixels[i][j]);
                ((BufferedImage) image1).setRGB(j, i, pixels[i][j]);
            }
        }
        return (BufferedImage) image1;
    }

    public Image getImage() {
        if (image1 != null) {
            return image1;
        }
        return null;
    }


}
