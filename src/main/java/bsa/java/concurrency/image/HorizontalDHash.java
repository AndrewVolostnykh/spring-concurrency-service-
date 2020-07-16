package bsa.java.concurrency.image;

import bsa.java.concurrency.exception.HorizontalHashCalculatingException;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

@Service
public class HorizontalDHash {
    public long calculateHash(byte[] image) {
        try {
            return hashCalculator(imageProcessor(image));
        } catch (Exception ex) {
            throw new HorizontalHashCalculatingException("HorizontalDHash met a problem: " + ex.getMessage());
        }
    }

    private BufferedImage imageProcessor(byte[] image) throws Exception {
        var bufferedImage = ImageIO.read(new ByteArrayInputStream(image));
        var result = new BufferedImage(8, 9, Image.SCALE_SMOOTH);
        result.getGraphics().drawImage(bufferedImage.getScaledInstance(8,9, BufferedImage.TYPE_BYTE_GRAY), 0, 0, null);
        return result;
    }

    private long hashCalculator(BufferedImage processedImage) {
        long hash = 0;
        for(int row = 1; row < 8; row++) {
            for (int col = 0; col < 9; col++) {
                var prev = brightScore(processedImage.getRGB(row - 1, col));
                var curr = brightScore(processedImage.getRGB(row, col));
                hash |= curr > prev ? 1 : 0;
                hash <<= 1;
            }
        }

        return hash;
    }

    private int brightScore(int rgb) {
        return rgb & 0b11111111;
    }
}
