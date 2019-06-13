package sample.imageFilters;

import java.nio.ByteBuffer;

public class MirrorFilter {
    public static int[][] fromByteBufferToPixels(ByteBuffer byteBuffer, int H, int W, int bpp) {
        int[][] pixels = new int[H][W];

        int k = 0;
        for (int i = H; i >= 0; i--) {
            for (int j = W; j >= 0; j--) {

                pixels[i][j] = (byteBuffer.get(k) * 65536) //R
                        + (byteBuffer.get(k + 1) * 256) // G
                        + byteBuffer.get(k + 2); // B
                k += bpp;
            }
        }
        return pixels;
    }

    public static ByteBuffer byteBufferToByteBuffer(ByteBuffer byteBuffer, int H, int W, int bpp) {

        return byteBuffer;
    }

    public static int[][] fromArrayToArray(int[][] pixels, int H, int W) {
        int[][] pxls = new int[H][W];
        for(int i = 0; i < H; i++) {
            for (int j = 0; j < W; j++) {
                pxls[i][j] = pixels[i][W - 1 - j];
            }
        }
//        for (int i = 0; i < H; i++) {
//            for (int j = 0; j < W / 2 + 1; j++) {
//                int tmp = pixels[i][W - 1 - j];
//                pixels[i][W - 1 - j] = pixels[i][j];
//                pixels[i][j] = tmp;
//            }
//        }
        return pxls;
    }

}
