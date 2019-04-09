package model;

public class Insp1RoutingRotatingPriority implements Insp1Routing {
    //C1 priority value
    private int priority;

    public Insp1RoutingRotatingPriority(){
        priority = 1;
    }

    @Override
    public ProductType determineC1Destination(int ws1BufferC1, int ws2BufferC1, int ws3BufferC1) {
        if (ws1BufferC1 >= 2 && ws2BufferC1 >= 2 && ws3BufferC1 >= 2) //All buffers full
            return null;
        switch (priority) {
            case 1:
                //change priority for next case
                priority = 2;
            {
                if (ws1BufferC1 <= ws2BufferC1 && ws1BufferC1 <= ws3BufferC1)
                    return ProductType.P1;
                else if (ws2BufferC1 <= ws3BufferC1)
                    return ProductType.P2;
                else
                    return ProductType.P3;
            }
            case 2:
                //change priority for next case
                priority = 3;
            {
                if (ws2BufferC1 <= ws1BufferC1 && ws2BufferC1 <= ws3BufferC1)
                    return ProductType.P2;
                else if (ws3BufferC1 <= ws1BufferC1)
                    return ProductType.P3;
                else
                    return ProductType.P1;
            }
            case 3:
                //change priority for next case
                priority = 1;
            {
                if (ws3BufferC1 <= ws1BufferC1 && ws3BufferC1 <= ws2BufferC1)
                    return ProductType.P3;
                else if (ws1BufferC1 <= ws2BufferC1)
                    return ProductType.P1;
                else
                    return ProductType.P2;
            }
            default:
                return null;
        }
    }
}
