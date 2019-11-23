package fork_blur;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * ForkBlur implements a simple horizontal image blur. It averages pixels in the
 * source array and writes them to a destination array. The sThreshold value
 * determines whether the blurring will be performed directly or split into two
 * tasks.
 * <p>
 * This is not the recommended way to blur images; it is only intended to
 * illustrate the use of the Fork/Join framework.
 */
public class ForkBlur extends RecursiveAction {
    private static final int BLUR_WIDTH = 15; // Processing window size, should be odd.
    private static final int THRESHOLD = 10000;

    private int[] source;
    private int start;
    private int length;
    private int[] destination;

    private ForkBlur(int[] source, int start, int length, int[] destination) {
        this.source = source;
        this.start = start;
        this.length = length;
        this.destination = destination;
    }

    // Plumbing follows.
    public static void main(String[] args) throws Exception {
        String srcName = "red-tulips.jpg";
        File srcFile = new File(resourceUri(srcName));
        BufferedImage image = ImageIO.read(srcFile);

        System.out.println("Source image: " + srcName);

        BufferedImage blurredImage = blur(image);

        String dstName = "blurred-tulips.jpg";
        File dstFile = new File(dstName);
        ImageIO.write(blurredImage, "jpg", dstFile);

        System.out.println("Output image: " + dstName);
    }

    private static URI resourceUri(String resourceName) throws URISyntaxException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        URL resource = contextClassLoader.getResource(resourceName);
        if (resource == null) {
            System.err.println("Resource not found: " + resourceName);
            System.exit(0);
        }
        return resource.toURI();
    }

    private static BufferedImage blur(BufferedImage srcImage) {
        int w = srcImage.getWidth();
        int h = srcImage.getHeight();

        int[] src = srcImage.getRGB(0, 0, w, h, null, 0, w);
        int[] dst = new int[src.length];

        System.out.println("Array size is " + src.length);
        System.out.println("Threshold is " + THRESHOLD);

        int processors = Runtime.getRuntime().availableProcessors();
        System.out.printf("%d processor%s available%n", processors, processors != 1 ? "s are" : " is");

        ForkBlur fb = new ForkBlur(src, 0, src.length, dst);

        ForkJoinPool pool = new ForkJoinPool();

        long startTime = System.currentTimeMillis();
        pool.invoke(fb);
        long endTime = System.currentTimeMillis();

        System.out.println("Image blur took " + (endTime - startTime) + " milliseconds.");

        BufferedImage dstImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        dstImage.setRGB(0, 0, w, h, dst, 0, w);

        return dstImage;
    }

    @Override
    protected void compute() {
        if (length < THRESHOLD) {
            computeDirectly();
            return;
        }

        int split = length / 2;

        invokeAll(
                new ForkBlur(source, start, split, destination),
                new ForkBlur(source, start + split, length - split, destination)
        );
    }

    // Average pixels from source, write results into destination.
    private void computeDirectly() {
        int sidePixels = (BLUR_WIDTH - 1) / 2;
        for (int index = start; index < start + length; index++) {
            // Calculate average.
            float rt = 0, gt = 0, bt = 0;
            for (int mi = -sidePixels; mi <= sidePixels; mi++) {
                int mindex = Math.min(Math.max(mi + index, 0), source.length - 1);
                int pixel = source[mindex];
                rt += (float) ((pixel & 0x00ff0000) >> 16) / BLUR_WIDTH;
                gt += (float) ((pixel & 0x0000ff00) >> 8) / BLUR_WIDTH;
                bt += (float) (pixel & 0x000000ff) / BLUR_WIDTH;
            }

            // Re-assemble destination pixel.
            int dpixel = (0xff000000)
                    | (((int) rt) << 16)
                    | (((int) gt) << 8)
                    | (int) bt;
            destination[index] = dpixel;
        }
    }
}