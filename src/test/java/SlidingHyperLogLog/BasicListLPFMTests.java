package SlidingHyperLogLog;

public class BasicListLPFMTests extends LPFMTests {

    @Override
    LPFMFactory getLPFMFactory() {
        return new BasicListLPFMFactory();
    }
}
