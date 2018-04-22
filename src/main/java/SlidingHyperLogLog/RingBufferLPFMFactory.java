package SlidingHyperLogLog;

public class RingBufferLPFMFactory implements LPFMFactory<RingBufferLPFM> {

    @Override
    public RingBufferLPFM buildLPFM(long windowSize) {
        return new RingBufferLPFM(windowSize);
    }
}
