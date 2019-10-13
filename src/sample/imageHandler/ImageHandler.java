package sample.imageHandler;

import sample.bmp.BMPWriter;
import sample.imageFilters.MirrorFilter;
import sample.tga.TGADecoder;
import sample.util.ConvertByteBufferToPixelsArray;

import java.awt.*;
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
    private int wImg, hImg;
    private Image image1 = null;
    private TGADecoder tgaDecoder;

    public ImageHandler() {

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

    public BufferedImage resize(double zoom){

        //                pixels[i][j] = (byteBuffer.get(k) * 65536) //R
        //                        + (byteBuffer.get(k + 1) * 256) // G
        //                        + byteBuffer.get(k + 2); // B



        image1 = new BufferedImage((int)(wImg * zoom), (int)(hImg * zoom), BufferedImage.TYPE_INT_RGB);

        for(int y = 1; y < hImg - 1; y++){
            for(int x = 1; x < wImg - 1; x++){
                if(zoom > 1) {
                    ((BufferedImage) image1).setRGB(x, y, getMiddleColor(pixels[y + 1][x], pixels[y - 1][x],
                            pixels[y][x - 1], pixels[y][x + 1]));
                    ((BufferedImage) image1).setRGB(x + 1, y, getMiddleColor(pixels[y + 1][x], pixels[y - 1][x],
                            pixels[y][x - 1], pixels[y][x + 1]));
                    ((BufferedImage) image1).setRGB(x, y + 1, getMiddleColor(pixels[y + 1][x], pixels[y - 1][x],
                            pixels[y][x - 1], pixels[y][x + 1]));
                }
                else{
                    ((BufferedImage) image1).setRGB(x, y, getMiddleColor(pixels[y + 1][x], pixels[y - 1][x],
                            pixels[y][x - 1], pixels[y][x + 1]));
                }
            }
        }

        //wImg *= zoom;
        //hImg *= zoom;
        this.zoom = zoom;

        return (BufferedImage) image1;
    }

    private int getMiddleColor(int up, int down, int left, int right){
        return (up + down + left + right) / 4;
    }

    public void MirrorFilter() {
        pixels = MirrorFilter.fromArrayToArray(pixels, tgaDecoder.getHeight(), tgaDecoder.getWidth());
        // panelFilter.setPixels(pixels, tgaDecoder.getWidth(), tgaDecoder.getHeight());
        setPixels(pixels, tgaDecoder.getWidth(), tgaDecoder.getHeight());
    }


    public BufferedImage setPixels(int[][] pixels, int W, int H) {

        this.pixels = pixels;

        zoom = 1;
        wImg = W;
        hImg = H;

        image1 = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < hImg; i++) {
            for (int j = 0; j < wImg; j++) {
                ((BufferedImage) image1).setRGB(j, i, pixels[i][j]);
            }
        }
        return (BufferedImage) image1;
    }

    public Image getImage() {
            return image1;
    }


}
