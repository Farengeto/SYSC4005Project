package model;

import java.io.File;
import java.util.*;

public class Simulation {

    //Random variables read from input files
    private static Queue<Double> servinsp1;
    private static Queue<Double> servinsp22;
    private static Queue<Double> servinsp23;
    private static Queue<Double> ws1;
    private static Queue<Double> ws2;
    private static Queue<Double> ws3;

    private static Random random; //random variable generator
    private static double clock; //current simulation time
    private static int totalProducts; //how many products to simulate
    private static PriorityQueue<Event> futureEvents; //List of queued events, sorted by time

    //Total outputs
    private static int numberProducts;
    private static int numberP1;
    private static int numberP2;
    private static int numberP3;

    //Workstation buffers
    private static int ws1BufferC1;
    private static int ws2BufferC1;
    private static int ws2BufferC2;
    private static int ws3BufferC1;
    private static int ws3BufferC3;

    //Idle inspector hold item
    private static Event inspector1Hold;
    private static Event inspector2Hold;

    public static void main(String[] args) {
        initialization();

        System.out.printf("%10s %10s %5s %5s %10s | %3s %3s %3s | %3s %3s %3s %3s %3s | %3s %3s %3s\n","","Event","Comp","Prod","Clock","C1","C2","C3","B11","B21","B22","B31","B33","P1","P2","P3");
        System.out.printf("%10s %10s %5s %5s %10.3f | %3d %3d %3d | %3d %3d %3d %3d %3d | %3d %3d %3d\n","","","","",clock,
                300-servinsp1.size(), 300-servinsp22.size(), 300-servinsp23.size(), ws1BufferC1, ws2BufferC1, ws2BufferC2, ws3BufferC1, ws3BufferC3, numberP1, numberP2, numberP3);
        while (numberProducts < totalProducts) {
            Event event = futureEvents.poll();

            if (event == null)
                break; //No more queued events, terminate

            clock = event.getEventTime();

            if (event.getEventType() == EventType.Arrival)
                processArrivalEvent(event);
            else if (event.getEventType() == EventType.Departure)
                processDepartureEvent(event);
        }
    }

    /**
     * Initialize the simulation
     */
    public static void initialization() {
        loadInputData();
        futureEvents = new PriorityQueue<>();
        random = new Random(100980888L);
        clock = 0.0;

        inspector1Hold = null;
        inspector2Hold = null;

        ws1BufferC1 = 0;
        ws2BufferC1 = 0;
        ws2BufferC2 = 0;
        ws3BufferC1 = 0;
        ws3BufferC3 = 0;
        numberProducts = 0;
        numberP1 = 0;
        numberP2 = 0;
        numberP3 = 0;

        if (totalProducts == 0) return;

        //Queue starting arrival events
        //Null warning, but totalProducts would cancel if any are empty
        futureEvents.add(new Event(EventType.Arrival, ComponentType.C1, null, servinsp1.poll()));
        if (random.nextBoolean())
            futureEvents.add(new Event(EventType.Arrival, ComponentType.C2, null, servinsp22.poll()));
        else
            futureEvents.add(new Event(EventType.Arrival, ComponentType.C3, null, servinsp23.poll()));
    }

    /**
     * Load the input distribution data from their files
     */
    private static void loadInputData() {
        //Initialize Queues
        servinsp1 = new LinkedList<>();
        servinsp22 = new LinkedList<>();
        servinsp23 = new LinkedList<>();
        ws1 = new LinkedList<>();
        ws2 = new LinkedList<>();
        ws3 = new LinkedList<>();

        //Read input files
        //Set total products based on when you run out of inputs
        totalProducts = readInputFile("servinsp1.dat", servinsp1);
        totalProducts = Math.min(totalProducts, readInputFile("servinsp22.dat", servinsp22));
        totalProducts = Math.min(totalProducts, readInputFile("servinsp23.dat", servinsp23));
        totalProducts = Math.min(totalProducts, readInputFile("ws1.dat", ws1));
        totalProducts = Math.min(totalProducts, readInputFile("ws2.dat", ws2));
        totalProducts = Math.min(totalProducts, readInputFile("ws3.dat", ws3));
    }

    /**
     * Read the simulation input data from its file and load it into a queue
     *
     * @param filename The name of the data file
     * @param queue    The queue to load the data into
     * @return The length of the data added to the queue
     */
    private static int readInputFile(String filename, Queue<Double> queue) {
        int count = 0;
        try {
            Scanner sc = new Scanner(new File(filename));
            while (sc.hasNextLine()) {
                String next = sc.nextLine();
                if (next != null && next.length() > 0) {
                    try {
                        double value = Double.parseDouble(next);
                        queue.add(value);
                        count++;
                    } catch (Exception e) {
                    }
                }
            }
            sc.close();
        } catch (Exception e) {
        }
        return count;
    }

    /**
     * Process an arrival event and queue further events
     * Triggered when an inspector finishes
     * @param event
     */
    private static void processArrivalEvent(Event event) {
        ProductType destinationProduct = null;
        switch (event.getComponentType()) {
            case C1:
                destinationProduct = determineC1Destination();
                if(destinationProduct == null){
                    inspector1Hold = event;
                    break;
                }
                switch (destinationProduct) {
                    case P1:
                        ws1BufferC1++;
                        break;
                    case P2:
                        ws2BufferC1++;
                        break;
                    case P3:
                        ws3BufferC1++;
                        break;
                }
                break;
            case C2:
                if (ws2BufferC2 < 2) {
                    ws2BufferC2++;
                    destinationProduct = ProductType.P2;
                } else {
                    inspector2Hold = event;
                }
                break;
            case C3:
                if (ws3BufferC3 < 2) {
                    ws3BufferC3++;
                    destinationProduct = ProductType.P3;
                } else {
                    inspector2Hold = event;
                }
                break;
        }

        System.out.printf("%10s %10s %5s %5s %10.3f | %3d %3d %3d | %3d %3d %3d %3d %3d | %3d %3d %3d\n","Process",event.getEventType(),event.getComponentType(),event.getProductType(),clock,
                300-servinsp1.size(), 300-servinsp22.size(), 300-servinsp23.size(), ws1BufferC1, ws2BufferC1, ws2BufferC2, ws3BufferC1, ws3BufferC3, numberP1, numberP2, numberP3);

        //Set up arrivals and departures if not on hold
        if(destinationProduct != null){
            scheduleArrivalEvent(event);
            scheduleDepartureEvent(destinationProduct);
        }
    }

    /**
     * Schedule a departure event, if applicable
     * @param event The event of the last inspection
     */
    private static void scheduleArrivalEvent(Event event) {
        ComponentType nextComponent = null;
        Double nextTime = null;
        if (event.getComponentType() == ComponentType.C1) { //Inspector 1
            if (inspector1Hold == null) {
                nextComponent = ComponentType.C1;
                nextTime = servinsp1.poll();
            }
        } else { //Inspector 2
            if (inspector2Hold == null) {
                if (random.nextBoolean()) {
                    nextComponent = ComponentType.C2;
                    nextTime = servinsp22.poll();
                } else {
                    nextComponent = ComponentType.C3;
                    nextTime = servinsp23.poll();
                }
            }
        }

        if (nextTime != null) { //skip if sample distribution depleted
            Event newEvent = new Event(EventType.Arrival, nextComponent, null, clock + nextTime);
            futureEvents.add(newEvent);
            System.out.printf("%10s %10s %5s %5s %10.3f | %3d %3d %3d | %3d %3d %3d %3d %3d | %3d %3d %3d\n","Schedule",newEvent.getEventType(),newEvent.getComponentType(),newEvent.getProductType(),clock,
                    300-servinsp1.size(), 300-servinsp22.size(), 300-servinsp23.size(), ws1BufferC1, ws2BufferC1, ws2BufferC2, ws3BufferC1, ws3BufferC3, numberP1, numberP2, numberP3);
        }
    }

    /**
     * Schedule a departure event, if applicable
     * @param type The product to attempt to schedule assembly for
     */
    private static void scheduleDepartureEvent(ProductType type){
        //If true, that workstation is already running, can't schedule now
        if(futureEvents.stream().anyMatch(e -> e.getProductType() == type)){
            return;
        }

        //check sufficient buffer
            Double nextTime = null;
        switch (type){
            case P1:
                if(ws1BufferC1 > 0){
                    nextTime = ws1.poll();
                    if(nextTime != null){
                        ws1BufferC1--;
                    }
                }
                break;
            case P2:
                if(ws2BufferC1 > 0 && ws2BufferC2 > 0){
                    nextTime = ws2.poll();
                    if(nextTime != null){
                        ws2BufferC1--;
                        ws2BufferC2--;
                    }
                }
                break;
            case P3:
                if(ws3BufferC1 > 0 && ws3BufferC3 > 0){
                    nextTime = ws3.poll();
                    if(nextTime != null){
                        ws3BufferC1--;
                        ws3BufferC3--;
                    }
                }
                break;
        }

        if(nextTime != null){ //skip if sample distribution depleted
            Event newEvent = new Event(EventType.Departure, null, type, clock + nextTime);
            futureEvents.add(newEvent);
            System.out.printf("%10s %10s %5s %5s %10.3f | %3d %3d %3d | %3d %3d %3d %3d %3d | %3d %3d %3d\n","Schedule",newEvent.getEventType(),newEvent.getComponentType(),newEvent.getProductType(),clock,
                    300-servinsp1.size(), 300-servinsp22.size(), 300-servinsp23.size(), ws1BufferC1, ws2BufferC1, ws2BufferC2, ws3BufferC1, ws3BufferC3, numberP1, numberP2, numberP3);
            //processHoldEvent();
        }
    }

    /**
     * Check for a hold and cancel if applicable
     */
    /*private static void processHoldEvent(){
        if(inspector1Hold != null){
            ProductType holdDestination = determineC1Destination();
            if(holdDestination != null){
                switch (holdDestination) {
                    case P1:
                        ws1BufferC1++;
                        break;
                    case P2:
                        ws2BufferC1++;
                        break;
                    case P3:
                        ws3BufferC1++;
                        break;
                }
                scheduleArrivalEvent(inspector1Hold);
                inspector1Hold = null;
            }
        }

        if(inspector2Hold != null){
            boolean canLiftHold = false;
            if(inspector2Hold.getComponentType() == ComponentType.C2 && ws2BufferC2 < 2){
                ws2BufferC2++;
                canLiftHold = true;
            }
            else if(inspector2Hold.getComponentType() == ComponentType.C3 && ws3BufferC3 < 2){
                ws3BufferC3++;
                canLiftHold = true;
            }
            if(canLiftHold) {
                scheduleArrivalEvent(inspector2Hold);
                inspector2Hold = null;
            }
        }
    }*/

    /**
     * Process a departure event
     * Triggered when a workstation finished
     * @param event
     */
    private static void processDepartureEvent(Event event) {
        switch (event.getProductType()){
            case P1:
                numberP1++;
                break;
            case P2:
                numberP2++;
                break;
            case P3:
                numberP3++;
                break;
        }
        System.out.printf("%10s %10s %5s %5s %10.3f | %3d %3d %3d | %3d %3d %3d %3d %3d | %3d %3d %3d\n","Process",event.getEventType(),event.getComponentType(),event.getProductType(),clock,
                300-servinsp1.size(), 300-servinsp22.size(), 300-servinsp23.size(), ws1BufferC1, ws2BufferC1, ws2BufferC2, ws3BufferC1, ws3BufferC3, numberP1, numberP2, numberP3);
        scheduleDepartureEvent(event.getProductType());
    }

    private static ProductType determineC1Destination() {
        if(ws1BufferC1 >= 2 && ws2BufferC1 >= 2 && ws3BufferC1 >= 2) //All buffers full
            return null;
        else if (ws1BufferC1 <= ws2BufferC1 && ws1BufferC1 <= ws3BufferC1)
            return ProductType.P1;
        else if (ws2BufferC1 <= ws3BufferC1)
            return ProductType.P2;
        else
            return ProductType.P3;
    }
}
