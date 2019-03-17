package model;

import java.util.Random;

public class SimulationRandom {
    private Random random;

    public SimulationRandom(long seed) {
        this.random = new Random(seed);
    }

    public double NextExponential(double rateParameter) {
        if (rateParameter <= 0)
            throw new IllegalArgumentException("Invalid rate parameter");

        return -Math.log(1 - random.nextDouble()) / rateParameter;
    }

    public double nextServinsp1(){
        return NextExponential(0.096544573);
    }

    public double nextServinsp22(){
        return NextExponential(0.06436289);
    }

    public double nextServinsp23(){
        return NextExponential(0.048466621);
    }

    public double nextWs1(){
        return NextExponential(0.217182777);
    }

    public double nextWs2(){
        return NextExponential(0.090150136);
    }

    public double nextWs3(){
        return NextExponential(0.113693469);
    }

    public ComponentType nextInsp2Comp(){
        return random.nextBoolean() ? ComponentType.C2 : ComponentType.C3;
    }
}
