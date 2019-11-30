package com.VegaSolutions.lpptransit.travanaserver.Objects;


public class CalBusInfo {

    private String bus_name;
    private String bus_unit_id;
    private double average_speed;
    private int traffic_flow;                     // -1 - data is not set (imposible), 0 - bus is (almost) not moving (grey color?), 1 - bus is moving really slow (congestion?) (red color), 2 - (orange color), 3 - bus is fast as it needs to be (green color)
    private double current_speed;

    private long time_stamp_milis;                // Date last_updated = new Date(time_stamps_milis), date in milliseconds when data was updated.

    public String getBus_name() {
        return bus_name;
    }

    public String getBus_unit_id() {
        return bus_unit_id;
    }

    public double getAverage_speed() {
        return average_speed;
    }

    public int getTraffic_flow() {
        return traffic_flow;
    }

    public double getCurrent_speed() {
        return current_speed;
    }

    public long getTime_stamp_milis() {
        return time_stamp_milis;
    }

    @Override
    public String toString() {
        return "CalBusInfo{" +
                "bus_name='" + bus_name + '\'' +
                ", bus_unit_id='" + bus_unit_id + '\'' +
                ", average_speed=" + average_speed +
                ", traffic_flow=" + traffic_flow +
                ", current_speed=" + current_speed +
                ", time_stamp_milis=" + time_stamp_milis +
                '}';
    }
}
