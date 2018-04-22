package SlidingHyperLogLog;

public interface LPFMFactory<T extends LPFM>
{
    T buildLPFM(long windowSize);
}
