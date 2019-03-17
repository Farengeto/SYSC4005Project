package model;

public class Event implements Comparable<Event>{
    private EventType eventType;
    private ComponentType componentType;
    private ProductType productType;
    private double eventTime;

    public Event(EventType eventType, ComponentType componentType, ProductType productType, double eventTime){
        this.eventType = eventType;
        this.componentType = componentType;
        this.productType = productType;
        this.eventTime = eventTime;
    }

    public EventType getEventType(){
        return eventType;
    }

    public void setEventType(EventType eventType){
        this.eventType = eventType;
    }

    public ComponentType getComponentType(){
        return componentType;
    }

    public void setComponentType(ComponentType componentType){
        this.componentType = componentType;
    }

    public ProductType getProductType(){
        return productType;
    }

    public void setProductType(ProductType productType){
        this.productType = productType;
    }

    public double getEventTime(){
        return eventTime;
    }

    public void setEventTime(double eventTime){
        this.eventTime = eventTime;
    }

    public int compareTo(Event event){
        if(eventTime == event.eventTime)
            return 0;
        else if(eventTime > event.eventTime)
            return 1;
        else
            return -1;
    }
}
