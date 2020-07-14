package bsa.java.concurrency.image;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;


public class DHasher {
    public long calculateHash(byte[] image) {
        try {
            var img = ImageIO.read(new ByteArrayInputStream(image));
            return calculateDHash(preprocessImage(img));
        } catch (Exception err) {
            throw new RuntimeException(err.getMessage());
        }
    }

    private static BufferedImage preprocessImage(BufferedImage image) {
        var result = image.getScaledInstance(9, 9, Image.SCALE_SMOOTH);
        var output = new BufferedImage(9, 9, BufferedImage.TYPE_BYTE_GRAY);
        output.getGraphics().drawImage(result, 0, 0, null);

        return output;
    }

    private static int brightnessScore(int rgb) {
        return rgb & 0b11111111;
    }

    public static long calculateDHash(BufferedImage processedImage) {
        long hash = 0;
        for (var row = 1; row < 9; row++) {
            for (var col = 1; col < 9; col++) {
                var prev = brightnessScore(processedImage.getRGB(col - 1, row - 1));
                var current = brightnessScore(processedImage.getRGB(col, row));
                hash |= current > prev ? 1 : 0;
                hash <<= 1;
            }
        }

        return hash;
    }

    public static double matchPercent(long hash1, long hash2) { // this have to be in DB
        long hashesXor = hash1 ^ hash2;
        int numberOfOne = 0;
        for(; hashesXor != 0; numberOfOne++) {
            hashesXor &= (hashesXor - 1);
        }

        var result = numberOfOne / 64d;
        System.out.println("ones: " + numberOfOne);
        System.out.println("result: " + (1d - result));

        return 1 - result;
    }
}
