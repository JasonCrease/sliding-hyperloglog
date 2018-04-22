package SlidingHyperLogLog;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Random;

public abstract class LPFMTests {

    abstract LPFMFactory getLPFMFactory();

    @Test
    public void descreasingRTest() {
        LPFM lpfm = getLPFMFactory().buildLPFM(10_000);

        for(int i = 0; i <= 20_000; i++)
            lpfm.offer(i, 100_000 - i);

        Assert.assertEquals(lpfm.getMaxSince(10000), 90_000);
        Assert.assertEquals(lpfm.getMaxSince(13000), 87_000);
        Assert.assertEquals(lpfm.getMaxSince(19999), 80_001);
        Assert.assertEquals(lpfm.getMaxSince(20000), 80_000);
        Assert.assertEquals(lpfm.getMaxSince(20001), 0);
    }

    @Test
    public void sawWaveRTest() {
        LPFM lpfm = getLPFMFactory().buildLPFM(1000);

        for(int i = 0; i < 1000; i++)
            lpfm.offer(i, 100 - (i % 100));

        Assert.assertEquals(lpfm.getMaxSince(0), 100);
        Assert.assertEquals(lpfm.getMaxSince(300), 100);
        Assert.assertEquals(lpfm.getMaxSince(950), 50);
        Assert.assertEquals(lpfm.getMaxSince(999), 1);
        Assert.assertEquals(lpfm.getMaxSince(1000), 0);
        Assert.assertEquals(lpfm.getMaxSince(1200), 0);
    }

    @Test
    public void lotsOfTheSameTest() {
        LPFM lpfm = getLPFMFactory().buildLPFM(50_000);

        Random r = new Random(35);

        for(int i = 0; i <= 100_000; i++)
            lpfm.offer(i, 5);

        Assert.assertEquals(lpfm.getMaxSince(50_000), 5);
        Assert.assertEquals(lpfm.getMaxSince(70_000), 5);
        Assert.assertEquals(lpfm.getMaxSince(100_000), 5);
    }

    @Test
    public void randomLowCardinalityTest() {
        LPFM lpfm = getLPFMFactory().buildLPFM(1000);

        Random r = new Random(613);

        for(int i = 0; i <= 1000; i++)
            lpfm.offer(i, r.nextInt(10));

        Assert.assertEquals(lpfm.getMaxSince(0), 9);
        Assert.assertEquals(lpfm.getMaxSince(300), 9);
        Assert.assertEquals(lpfm.getMaxSince(950), 9);
        Assert.assertEquals(lpfm.getMaxSince(998), 6);
        Assert.assertEquals(lpfm.getMaxSince(999), 1);
        Assert.assertEquals(lpfm.getMaxSince(1000), 1);
        Assert.assertEquals(lpfm.getMaxSince(1001), 0);
    }

    @Test
    public void randomHighCardinalityTest() {
        LPFM lpfm = getLPFMFactory().buildLPFM(50_000);

        Random r = new Random(35);

        for(int i = 0; i <= 90_000; i++)
            lpfm.offer(i, r.nextInt(1_000_000));

        Assert.assertEquals(lpfm.getMaxSince(50_000), 999_996);
        Assert.assertEquals(lpfm.getMaxSince(80_000), 999_955);
        Assert.assertEquals(lpfm.getMaxSince(85_000), 999_682);
        Assert.assertEquals(lpfm.getMaxSince(89_999), 906_808);
        Assert.assertEquals(lpfm.getMaxSince(90_000),  54_725);
        Assert.assertEquals(lpfm.getMaxSince(90_001), 0);
    }

    @Test
    public void randomBigTimestampsTest() {

        long minT = (long)2E13;
        long maxT = (long)2E14;
        long gapT = (long)1E10;

        // Repeat 1000 times to find bugs
        for (int trial = 0; trial < 100; trial++) {
            LPFM lpfm = getLPFMFactory().buildLPFM(maxT);
            Random r = new Random(trial + 5314312);

            int largestEver = 0;
            int mostRecent = 0;

            for (long t = minT; t <= maxT; t += gapT) {
                mostRecent = (int)(1 - Math.log( r.nextDouble()) * 2);
                lpfm.offer(t, mostRecent);
                if (mostRecent > largestEver)
                    largestEver = mostRecent;
            }

            Assert.assertEquals(lpfm.getMaxSince(minT), largestEver, "Failed at trial " + trial);
            Assert.assertEquals(lpfm.getMaxSince(maxT), mostRecent, "Failed at trial " + trial);
        }
    }

    @Test
    public void increasingRTest() {
        LPFM lpfm = getLPFMFactory().buildLPFM(10000);

        for(int i = 0; i <= 20000; i++)
            lpfm.offer(i, i);

        Assert.assertEquals(lpfm.getMaxSince(10000), 20000);
        Assert.assertEquals(lpfm.getMaxSince(13000), 20000);
        Assert.assertEquals(lpfm.getMaxSince(19999), 20000);
        Assert.assertEquals(lpfm.getMaxSince(20000), 20000);
        Assert.assertEquals(lpfm.getMaxSince(20001), 0);
    }

    @Test
    public void fastIncreaseingRTest() {
        LPFM lpfm = getLPFMFactory().buildLPFM(10000);

        for(int i = 0; i <= 20000; i++)
            lpfm.offer(i, i / 10);

        Assert.assertEquals(lpfm.getMaxSince(10000), 2000);
        Assert.assertEquals(lpfm.getMaxSince(13000), 2000);
        Assert.assertEquals(lpfm.getMaxSince(19999), 2000);
        Assert.assertEquals(lpfm.getMaxSince(20000), 2000);
        Assert.assertEquals(lpfm.getMaxSince(20001), 0);
    }

    @Test
    public void slowSteppingDownRTest() {
        LPFM lpfm = getLPFMFactory().buildLPFM(10000);

        for(int i = 0; i <= 20000; i++)
            lpfm.offer(i, 30 - (i / 1000));

        Assert.assertEquals(lpfm.getMaxSince(10000), 20);
        Assert.assertEquals(lpfm.getMaxSince(13000), 17);
        Assert.assertEquals(lpfm.getMaxSince(19999), 11);
        Assert.assertEquals(lpfm.getMaxSince(20000), 10);
        Assert.assertEquals(lpfm.getMaxSince(20001), 0);
    }
}
