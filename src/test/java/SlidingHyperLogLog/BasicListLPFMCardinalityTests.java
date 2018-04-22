package SlidingHyperLogLog;

public class BasicListLPFMCardinalityTests extends CardinalityTests {

    @Override
    public LPFMFactory getLpfmFactory() {
        return new BasicListLPFMFactory();
    }
}
