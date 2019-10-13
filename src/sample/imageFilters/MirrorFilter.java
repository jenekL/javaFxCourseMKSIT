package sample.imageFilters;

import java.nio.ByteBuffer;

public class MirrorFilter {
    public static ByteBuffer byteBufferToByteBuffer(ByteBuffer byteBuffer, int H, int W, int bpp) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(byteBuffer.remaining());
        int imageSize = W * H * bpp;
        for (int i = 0; i < imageSize - bpp; i += bpp) {

            buffer.put(byteBuffer.get(imageSize - i - 2));
            buffer.put(byteBuffer.get(imageSize - i - 1));
            buffer.put(byteBuffer.get(imageSize - i));
            if (bpp > 3) {
                buffer.put(byteBuffer.get(imageSize - i - 3));
            }
        }
        return buffer;
    }

    public static int[][] fromArrayToArray(int[][] pixels, int H, int W) {
        int[][] pxls = new int[H][W];
        for (int i = 0; i < H; i++) {
            for (int j = 0; j < W; j++) {
                pxls[i][j] = pixels[i][W - 1 - j];
            }
        }
        return pxls;
    }

}
