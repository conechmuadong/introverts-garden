package ie.app.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Phase {
    String name;
    public float threshHold = 0;
    public String startTime, endTime;

    public Phase() {
    }

    public Phase(String name, float threshHold, String startTime, String endTime) {
        this.name = name;
        this.threshHold = threshHold;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public float getThreshHold() {
        return threshHold;
    }

    public void setThreshHold(float threshHold) {
        this.threshHold = threshHold;
    }

    public Date getStartTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name + "\nthreshHold = " + threshHold + "\nstart: " + startTime + "\nend: " + endTime + "\n";
    }
}
