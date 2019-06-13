package sample.util;

import java.nio.ByteBuffer;

public class ConvertByteBufferToPixelsArray {
    public static int[][] convert(ByteBuffer byteBuffer, int W, int H, int bpp) {
        int[][] pixels = new int[H][W];

        int k = 0;
        for (int i = 0; i < H; i++) {
            for (int j = 0; j < W; j++) {

                pixels[i][j] = (byteBuffer.get(k) * 65536) //R
                        + (byteBuffer.get(k + 1) * 256) // G
                        + byteBuffer.get(k + 2); // B
                k += bpp;
            }
        }
        return pixels;
    }
}
