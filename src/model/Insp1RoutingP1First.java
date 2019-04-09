package model;

public class Insp1RoutingP1First implements Insp1Routing {
    public Insp1RoutingP1First(){

    }

    @Override
    public ProductType determineC1Destination(int ws1BufferC1, int ws2BufferC1, int ws3BufferC1) {
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
