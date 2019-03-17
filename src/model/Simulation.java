package model;

import java.util.*;

public class Simulation {
    private static SimulationRandom simRandom; //random variable generator
    private static double clock; //current simulation time
    private static int totalProducts; //how many products to simulate
    private static PriorityQueue<Event> futureEvents; //List of queued events, sorted by time

    //Total inputs
    private static int numberC1;
    private static int numberC2;
    private static int numberC3;

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
                numberC1, numberC2, numberC3, ws1BufferC1, ws2BufferC1, ws2BufferC2, ws3BufferC1, ws3BufferC3, numberP1, numberP2, numberP3);
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
    private static void initialization() {
        totalProducts = 1000;
        futureEvents = new PriorityQueue<>();
        simRandom = new SimulationRandom(100980888L);
        clock = 0.0;

        inspector1Hold = null;
        inspector2Hold = null;

        ws1BufferC1 = 0;
        ws2BufferC1 = 0;
        ws2BufferC2 = 0;
        ws3BufferC1 = 0;
        ws3BufferC3 = 0;
        numberC1 = 0;
        numberC2 = 0;
        numberC3 = 0;
        numberProducts = 0;
        numberP1 = 0;
        numberP2 = 0;
        numberP3 = 0;

        if (totalProducts == 0) return;

        //Queue starting arrival events
        //Null warning, but totalProducts would cancel if any are empty
        futureEvents.add(new Event(EventType.Arrival, ComponentType.C1, null, simRandom.nextServinsp1()));
        numberC1++;
        if (simRandom.nextInsp2Comp() == ComponentType.C2) {
            futureEvents.add(new Event(EventType.Arrival, ComponentType.C2, null, simRandom.nextServinsp22()));
            numberC2++;
        } else {
            futureEvents.add(new Event(EventType.Arrival, ComponentType.C3, null, simRandom.nextServinsp23()));
            numberC3++;
        }
    }

    /**
     * Process an arrival event and queue further events
     * Triggered when an inspector finishes
     * @param event The arrival event to be processed
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
                numberC1, numberC2, numberC3, ws1BufferC1, ws2BufferC1, ws2BufferC2, ws3BufferC1, ws3BufferC3, numberP1, numberP2, numberP3);

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
        double nextTime = Double.NaN;
        if (event.getComponentType() == ComponentType.C1) { //Inspector 1
            if (inspector1Hold == null) {
                nextComponent = ComponentType.C1;
                nextTime = simRandom.nextServinsp1();
                numberC1++;
            }
        } else { //Inspector 2
            if (inspector2Hold == null) {
                nextComponent = simRandom.nextInsp2Comp();
                if (nextComponent == ComponentType.C2) {
                    nextTime = simRandom.nextServinsp22();
                    numberC2++;
                } else {
                    nextTime = simRandom.nextServinsp23();
                    numberC3++;
                }
            }
        }

        if (!Double.isNaN(nextTime) && nextComponent != null) {
            Event newEvent = new Event(EventType.Arrival, nextComponent, null, clock + nextTime);
            futureEvents.add(newEvent);
            System.out.printf("%10s %10s %5s %5s %10.3f | %3d %3d %3d | %3d %3d %3d %3d %3d | %3d %3d %3d\n","Schedule",newEvent.getEventType(),newEvent.getComponentType(),newEvent.getProductType(),clock,
                    numberC1, numberC2, numberC3, ws1BufferC1, ws2BufferC1, ws2BufferC2, ws3BufferC1, ws3BufferC3, numberP1, numberP2, numberP3);
        }
    }

    /**
     * Schedule a departure event, if applicable
     * @param type The product to attempt to schedule assembly for
     */
    private static void scheduleDepartureEvent(ProductType type) {
        //If true, that workstation is already running, can't schedule now
        if (futureEvents.stream().anyMatch(e -> e.getProductType() == type)) {
            return;
        }

        //check sufficient buffer
        double nextTime = Double.NaN;
        switch (type) {
            case P1:
                if (ws1BufferC1 > 0) {
                    nextTime = simRandom.nextWs1();
                    ws1BufferC1--;
                }
                break;
            case P2:
                if (ws2BufferC1 > 0 && ws2BufferC2 > 0) {
                    nextTime = simRandom.nextWs2();
                    ws2BufferC1--;
                    ws2BufferC2--;
                }
                break;
            case P3:
                if (ws3BufferC1 > 0 && ws3BufferC3 > 0) {
                    nextTime = simRandom.nextWs3();
                    ws3BufferC1--;
                    ws3BufferC3--;
                }
                break;
            default:
                throw new IllegalArgumentException();
        }

        if(!Double.isNaN(nextTime)) { //check the product is being made
            Event newEvent = new Event(EventType.Departure, null, type, clock + nextTime);
            futureEvents.add(newEvent);
            System.out.printf("%10s %10s %5s %5s %10.3f | %3d %3d %3d | %3d %3d %3d %3d %3d | %3d %3d %3d\n", "Schedule", newEvent.getEventType(), newEvent.getComponentType(), newEvent.getProductType(), clock,
                    numberC1, numberC2, numberC3, ws1BufferC1, ws2BufferC1, ws2BufferC2, ws3BufferC1, ws3BufferC3, numberP1, numberP2, numberP3);
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
     * @param event The departure event to be processed
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
                numberC1, numberC2, numberC3, ws1BufferC1, ws2BufferC1, ws2BufferC2, ws3BufferC1, ws3BufferC3, numberP1, numberP2, numberP3);
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
