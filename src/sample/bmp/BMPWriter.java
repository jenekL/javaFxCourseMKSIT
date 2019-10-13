package sample.bmp;

import java.io.FileOutputStream;

public class BMPWriter {

    public static void save(int[][] pixels, int BytesPerPixel, String filePath) {
        try (ByteDataOutput out1 = new ByteDataOutput(new FileOutputStream(filePath))) {
            int height = pixels.length;
            int width = pixels[0].length;
            int rowSize = (width * 3 + 3) / 4 * 4;  // 3 bytes per pixel in RGB888, round up to multiple of 4
            int imageSize = rowSize * height;

            // BITMAP FILE HEADER
            out1.writeBytes(new byte[]{'B', 'M'});       // FileType
            out1.writeInt32(14 + 40 + imageSize);     // FileSize
            out1.writeInt16(0);                       // Reserved1
            out1.writeInt16(0);                       // Reserved2
            out1.writeInt32(14 + 40);                 // BitmapOffset

            // BITMAP INFO HEADER
            out1.writeInt32(40);                      // Size
            out1.writeInt32(width);                      // Width
            out1.writeInt32(height);                     // Height
            out1.writeInt16(1);                       // Planes
            out1.writeInt16(BytesPerPixel * 8);       // BitsPerPixel
            out1.writeInt32(0);                       // Compression
            out1.writeInt32(imageSize);                  // SizeOfBitmap
            out1.writeInt32(0);                       // HorzResolution
            out1.writeInt32(0);                       // VertResolution
            out1.writeInt32(0);                       // ColorsUsed
            out1.writeInt32(0);                       // ColorsImportant

            byte[] row = new byte[rowSize];
            for (int y = height - 1; y >= 0; y--) {
                for (int x = 0; x < width * 3; x += 3) {
                    int color = pixels[y][x/3];
                    row[x] = (byte) (color);  // Blue
                    row[x + 1] = (byte) (color >>> 8);  // Green
                    row[x + 2] = (byte) (color >>> 16);  // Red
                }
                out1.writeBytes(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
