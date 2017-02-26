package kpi.ipt.labs.distributed.twophasecommit.domain;

import java.util.Date;

public class FlyBooking {

    private int id;
    private String clientName;
    private String flyNumber;
    private String from;
    private String to;
    private Date date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getFlyNumber() {
        return flyNumber;
    }

    public void setFlyNumber(String flyNumber) {
        this.flyNumber = flyNumber;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
