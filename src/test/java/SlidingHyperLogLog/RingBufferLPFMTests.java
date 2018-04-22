package SlidingHyperLogLog;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RingBufferLPFMTests extends LPFMTests {

    @Test
    public void toStringTest() {
        RingBufferLPFM ringBufferLPFM = new RingBufferLPFM(100);

        for (int t = 0; t < 10000; t++)
            ringBufferLPFM.offer(t, t % 354);
        Assert.assertEquals(ringBufferLPFM.toString(), "_ts=[9999, 9203, 9557, 9911], _rs=[87, 353, 353, 353], _start=3, _end=1");
    }

    @Override
    LPFMFactory getLPFMFactory() {
        return new RingBufferLPFMFactory();
    }
}
