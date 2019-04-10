package model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SimulationHistoricalData extends SimulationRandom {
    private static boolean dataLoaded = false;
    private static List<Double> servinsp1;
    private static List<Double> servinsp22;
    private static List<Double> servinsp23;
    private static List<Double> ws1;
    private static List<Double> ws2;
    private static List<Double> ws3;

    private int nextServinsp1 = 0;
    private int nextServinsp22 = 0;
    private int nextServinsp23 = 0;
    private int nextWs1 = 0;
    private int nextWs2 = 0;
    private int nextWs3 = 0;

    public SimulationHistoricalData(long seed) {
        super(seed); //still need seed for the inspector 2 components

        //Only load the first time needed
        if (!dataLoaded) {
            servinsp1 = loadSimulationDataFile("servinsp1.dat");
            servinsp22 = loadSimulationDataFile("servinsp22.dat");
            servinsp23 = loadSimulationDataFile("servinsp23.dat");
            ws1 = loadSimulationDataFile("ws1.dat");
            ws2 = loadSimulationDataFile("ws2.dat");
            ws3 = loadSimulationDataFile("ws3.dat");
            dataLoaded = true;
        }
    }

    private static List<Double> loadSimulationDataFile(String file) {
        List<Double> toReturn = new ArrayList<>();
        Scanner sc = null;
        try {
            sc = new Scanner(new File(file));
            while (sc.hasNext()) { //File should be entirely numbers, so if this doesn't work we want an exception
                toReturn.add(sc.nextDouble());
            }
        } catch (Exception e) {
            System.err.println("Simulation file error: " + file);
        } finally {
            if (sc != null)
                sc.close();
        }
        return toReturn;
    }

    public double nextServinsp1() {
        return servinsp1.get(nextServinsp1++ % servinsp1.size());
    }

    public double nextServinsp22() {
        return servinsp22.get(nextServinsp22++ % servinsp22.size());
    }

    public double nextServinsp23() {
        return servinsp23.get(nextServinsp23++ % servinsp23.size());
    }

    public double nextWs1() {
        return ws1.get(nextWs1++ % ws1.size());
    }

    public double nextWs2() {
        return ws2.get(nextWs2++ % ws2.size());
    }

    public double nextWs3() {
        return ws3.get(nextWs3++ % ws3.size());
    }
}
