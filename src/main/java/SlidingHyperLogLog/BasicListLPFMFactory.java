package SlidingHyperLogLog;

public class BasicListLPFMFactory implements LPFMFactory<BasicListLPFM> {

    @Override
    public BasicListLPFM buildLPFM(long windowSize) {
        return new BasicListLPFM(windowSize);
    }
}
