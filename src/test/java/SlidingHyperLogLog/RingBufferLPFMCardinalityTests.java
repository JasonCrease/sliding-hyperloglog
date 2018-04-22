package SlidingHyperLogLog;

public class RingBufferLPFMCardinalityTests extends CardinalityTests {

    @Override
    public LPFMFactory getLpfmFactory() {
        return new RingBufferLPFMFactory();
    }
}
