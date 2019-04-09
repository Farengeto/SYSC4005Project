package model;

public class SimulationOutput {
    private boolean useHistorical;
    private long seed;
    private double clock;
    private int totalProducts;
    private double p1Throughput;
    private double p2Throughput;
    private double p3Throughput;
    private double insp1Idle;
    private double insp2Idle;

    public SimulationOutput(boolean useHistorical, long seed, double clock, int numberP1, int numberP2, int numberP3, double insp1Active, double insp2Active){
        this.useHistorical = useHistorical;
        this.seed = seed;
        this.clock = clock;
        totalProducts = numberP1 + numberP2 + numberP3;
        p1Throughput = (numberP1 / clock);
        p2Throughput = (numberP2 / clock);
        p3Throughput = (numberP3 / clock);
        insp1Idle = ((clock - insp1Active) / clock);
        insp2Idle = ((clock - insp2Active) / clock);
    }

    public void printOutput(){
        System.out.println("------------------------------");
        System.out.println("Random Seed:     " + (useHistorical ? "Historical Data" : seed));
        System.out.println("Total Products:  " + totalProducts);
        System.out.printf("Simulation Time: %.4f minutes\n", clock);
        System.out.println();
        System.out.println("Throughput:");
        System.out.printf("%2s: %8.4f units/minute\n", ProductType.P1, p1Throughput);
        System.out.printf("%2s: %8.4f units/minute\n", ProductType.P2, p2Throughput);
        System.out.printf("%2s: %8.4f units/minute\n", ProductType.P3, p3Throughput);
        System.out.println();
        System.out.println("Inspector Idle Probability:");
        System.out.printf("Inspector 1: %8.4f%%\n", insp1Idle * 100);
        System.out.printf("Inspector 2: %8.4f%%\n", insp2Idle * 100);
        System.out.println("------------------------------");
    }

    public void printRawOutput(){
        System.out.print((useHistorical ? "Historical Data" : seed));
        System.out.print("\t" + clock);
        System.out.print("\t" + p1Throughput);
        System.out.print("\t" + p2Throughput);
        System.out.print("\t" + p3Throughput);
        System.out.print("\t" + (insp1Idle * 100) + "%");
        System.out.print("\t" + (insp2Idle * 100) + "%");
        System.out.println();
    }
}
