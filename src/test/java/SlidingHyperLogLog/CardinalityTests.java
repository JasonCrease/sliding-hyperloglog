package SlidingHyperLogLog;

import org.testng.Assert;
import org.testng.annotations.Test;

public abstract class CardinalityTests {


    public abstract LPFMFactory getLpfmFactory();

    @Test
    public void mediumCardinalitySawWaveTest() {
        SlidingHyperLogLog shll = new SlidingHyperLogLog.Builder().setLpfmFactory(getLpfmFactory()).setRsd(0.01).setWindowSize(750_000).build();

        for (long t = 0; t < 1_000_000; t++)
            shll.offer(t, t % 132_000L);

        Assert.assertEquals(shll.cardinalitySince(0), 131_145);
        Assert.assertEquals(shll.cardinalitySince(500_000), 131_145);
        Assert.assertEquals(shll.cardinalitySince(900_000), 100_392);
        Assert.assertEquals(shll.cardinalitySince(990_000), 10_037);
        Assert.assertEquals(shll.cardinalitySince(1_100_000), 0);
    }

    @Test
    public void mediumCardinalitySawWaveTestLowRsdTest() {
        SlidingHyperLogLog shll = new SlidingHyperLogLog.Builder().setLpfmFactory(getLpfmFactory()).setRsd(0.002).setWindowSize(750_000).build();

        for (long t = 0; t < 1_000_000; t++)
            shll.offer(t, t % 132_000L);

        Assert.assertEquals(shll.cardinalitySince(0), 131726);
        Assert.assertEquals(shll.cardinalitySince(500_000), 131726);
        Assert.assertEquals(shll.cardinalitySince(900_000), 99771);
        Assert.assertEquals(shll.cardinalitySince(990_000), 10003);
        Assert.assertEquals(shll.cardinalitySince(999_999), 1);
        Assert.assertEquals(shll.cardinalitySince(1_000_000), 0);
        Assert.assertEquals(shll.cardinalitySince(1_100_000), 0);
    }

    @Test(enabled = false)
    public void mediumCardinalitySawWaveTestVeryLowRsdTest() {
        SlidingHyperLogLog shll = new SlidingHyperLogLog.Builder().setLpfmFactory(getLpfmFactory()).setRsd(0.0005).setWindowSize(7500).build();

        for (long t = 0; t < 10_000; t++)
            shll.offer(t, t % 945);

        Assert.assertEquals(shll.cardinalitySince(0), 131_145);
        Assert.assertEquals(shll.cardinalitySince(5000), 131_145);
        Assert.assertEquals(shll.cardinalitySince(9000), 100_392);
        Assert.assertEquals(shll.cardinalitySince(9900), 10_037);
        Assert.assertEquals(shll.cardinalitySince(9999), 1);
        Assert.assertEquals(shll.cardinalitySince(10000), 0);
    }

    @Test
    public void mediumCardinalitySawWaveTestHighRsdTest() {
        SlidingHyperLogLog shll = new SlidingHyperLogLog.Builder().setLpfmFactory(getLpfmFactory()).setRsd(0.1).setWindowSize(750_000).build();

        for (long t = 0; t < 1_000_000; t++)
            shll.offer(t, t % 132_000L);

        Assert.assertEquals(shll.cardinalitySince(0), 144621);
        Assert.assertEquals(shll.cardinalitySince(500_000), 144621);
        Assert.assertEquals(shll.cardinalitySince(900_000), 111822);
        Assert.assertEquals(shll.cardinalitySince(990_000), 9131);
        Assert.assertEquals(shll.cardinalitySince(1_100_000), 0);
    }

    // To test the different approximations because of rsd
    @Test
    public void approxBecauseOfMTest() {
        SlidingHyperLogLog pOf7 = new SlidingHyperLogLog.Builder().setLpfmFactory(getLpfmFactory()).setRsd(0.08).setWindowSize(6000).build();
        SlidingHyperLogLog pOf6 = new SlidingHyperLogLog.Builder().setLpfmFactory(getLpfmFactory()).setRsd(0.10).setWindowSize(6000).build();
        SlidingHyperLogLog pOf5 = new SlidingHyperLogLog.Builder().setLpfmFactory(getLpfmFactory()).setRsd(0.15).setWindowSize(6000).build();
        SlidingHyperLogLog pOf4 = new SlidingHyperLogLog.Builder().setLpfmFactory(getLpfmFactory()).setRsd(0.20).setWindowSize(6000).build();

        for (long t = 0; t < 9000; t++) {
            pOf7.offer(t, t);
            pOf6.offer(t, t);
            pOf5.offer(t, t);
            pOf4.offer(t, t);
        }

        Assert.assertEquals(pOf7.cardinalitySince(8000), 866);
        Assert.assertEquals(pOf6.cardinalitySince(8000), 897);
        Assert.assertEquals(pOf5.cardinalitySince(8000), 1165);
        Assert.assertEquals(pOf4.cardinalitySince(8000), 1384);

    }

    @Test
    public void mediumCardinalitySawWaveSkewedTest() {
        SlidingHyperLogLog shll = new SlidingHyperLogLog.Builder().setLpfmFactory(getLpfmFactory()).setRsd(0.01).setWindowSize(750_000).build();

        for (long t = 0; t < 1_000_000; t++)
            shll.offer(t, (long)Math.sqrt((t % 132_000L)));

        Assert.assertEquals(shll.cardinalitySince(0), 365);
        Assert.assertEquals(shll.cardinalitySince(500_000), 365);
        Assert.assertEquals(shll.cardinalitySince(900_000), 314);
        Assert.assertEquals(shll.cardinalitySince(990_000), 20);
        Assert.assertEquals(shll.cardinalitySince(1_100_000), 0);
    }

    @Test
    public void allUniqueTest() {
        SlidingHyperLogLog shll = new SlidingHyperLogLog.Builder().setLpfmFactory(getLpfmFactory()).setRsd(0.01).setWindowSize(100_000).build();

        for (long t = 0; t < 100_000; t++)
            shll.offer(t, t);

        Assert.assertEquals(shll.cardinalitySince(0), 100134);
        Assert.assertEquals(shll.cardinalitySince(95000), 5018);
        Assert.assertEquals(shll.cardinalitySince(99000), 1009);
        Assert.assertEquals(shll.cardinalitySince(99900), 101);
        Assert.assertEquals(shll.cardinalitySince(120000), 0);
    }

    @Test
    public void blockedTest() {
        SlidingHyperLogLog shll = new SlidingHyperLogLog.Builder().setLpfmFactory(getLpfmFactory()).setRsd(0.01).setWindowSize(750_000).build();

        for (long t = 0; t < 1_000_000; t++)
            shll.offer(t, (long)(t / 10));

        Assert.assertEquals(shll.cardinalitySince(0), 84_377);
        Assert.assertEquals(shll.cardinalitySince(250_000), 75_584);
        Assert.assertEquals(shll.cardinalitySince(500_000), 50_230);
        Assert.assertEquals(shll.cardinalitySince(900_000), 9996);
        Assert.assertEquals(shll.cardinalitySince(990_000), 1009);
        Assert.assertEquals(shll.cardinalitySince(1_200_000), 0);
    }

    @Test
    public void skewedAndBlockedTest() {
        SlidingHyperLogLog shll = new SlidingHyperLogLog.Builder().setRsd(0.01).setWindowSize(750_000).setLpfmFactory(getLpfmFactory()).build();

        for (long t = 0; t < 1_000_000; t++)
            shll.offer(t, (long)Math.sqrt(t));

        Assert.assertEquals(shll.cardinalitySince(250_000), 507);
        Assert.assertEquals(shll.cardinalitySince(640_000), 200);
        Assert.assertEquals(shll.cardinalitySince(810_000), 101);
        Assert.assertEquals(shll.cardinalitySince(990_000), 6);
        Assert.assertEquals(shll.cardinalitySince(1_200_000), 0);
    }

    @Test
    public void bigUniqueWithDoublesTest() {
        SlidingHyperLogLog shll = new SlidingHyperLogLog.Builder().setRsd(0.01).setWindowSize(100_000).setLpfmFactory(getLpfmFactory()).build();

        for (long t = 0; t < 1_000_000; t++)
            shll.offer(t, (double)t);

        Assert.assertEquals(shll.cardinalitySince(900_000), 100140);
        Assert.assertEquals(shll.cardinalitySince(920_000), 80_402);
        Assert.assertEquals(shll.cardinalitySince(950_000), 50_557);
        Assert.assertEquals(shll.cardinalitySince(990_000), 9959);
        Assert.assertEquals(shll.cardinalitySince(999_990), 10);
    }

    @Test(enabled = false)
    public void bigUniquePerfTest() {
        SlidingHyperLogLog shll = new SlidingHyperLogLog.Builder().setRsd(0.01).setWindowSize(50_000_000).setLpfmFactory(getLpfmFactory()).build();

        for (long t = 0; t < 100_000_000; t++)
            shll.offer(t, t);
    }

    @Test
    public void lowCardinalityTest() {
        SlidingHyperLogLog shll = new SlidingHyperLogLog.Builder().setRsd(0.01).setWindowSize(100_000).setLpfmFactory(getLpfmFactory()).build();

        for (long t = 0; t < 100_000; t++)
            shll.offer(t, (int)(t % 100));

        Assert.assertEquals(shll.cardinalitySince(99_500), 101);
        Assert.assertEquals(shll.cardinalitySince(99_000), 101);
        Assert.assertEquals(shll.cardinalitySince(99_900), 101);
        Assert.assertEquals(shll.cardinalitySince(99_950), 50);
    }

    @Test
    public void lowCardinalityDoublesTest() {
        SlidingHyperLogLog shll = new SlidingHyperLogLog.Builder().setRsd(0.01).setWindowSize(100_000).setLpfmFactory(getLpfmFactory()).build();

        for (long t = 0; t < 100_000; t++)
            shll.offer(t, (double)(t % 100));

        Assert.assertEquals(shll.cardinalitySince(99_500), 100);
        Assert.assertEquals(shll.cardinalitySince(99_000), 100);
        Assert.assertEquals(shll.cardinalitySince(99_900), 100);
        Assert.assertEquals(shll.cardinalitySince(99_950), 50);
    }

    @Test
    public void veryLowCardinalityTest() {
        SlidingHyperLogLog shll = new SlidingHyperLogLog.Builder().setRsd(0.01).setWindowSize(100_000).setLpfmFactory(getLpfmFactory()).build();

        for (long t = 0; t < 100_000; t++)
            shll.offer(t, t % 10);

        Assert.assertEquals(shll.cardinalitySince(99_500), 10);
        Assert.assertEquals(shll.cardinalitySince(99_000), 10);
        Assert.assertEquals(shll.cardinalitySince(99_900), 10);
        Assert.assertEquals(shll.cardinalitySince(99_950), 10);
        Assert.assertEquals(shll.cardinalitySince(99_998),  2);
    }

    @Test
    public void oneCardinalityTest() {
        SlidingHyperLogLog shll = new SlidingHyperLogLog.Builder().setRsd(0.01).setWindowSize(50_000).setLpfmFactory(getLpfmFactory()).build();

        for (long t = 0; t < 100_000; t++)
            shll.offer(t, "hello");

        Assert.assertEquals(shll.cardinalitySince(0), 1);
        Assert.assertEquals(shll.cardinalitySince(50_000), 1);
        Assert.assertEquals(shll.cardinalitySince(75_000), 1);
        Assert.assertEquals(shll.cardinalitySince(99_999), 1);
    }
}