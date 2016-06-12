
package de.jhs.run2stop.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Departure implements Serializable {

    @SerializedName("line")
    @Expose
    private String line;
    @SerializedName("direction")
    @Expose
    private String direction;
    @SerializedName("countdown")
    @Expose
    private String countdown;
    @SerializedName("realtime")
    @Expose
    private String realtime;
    @SerializedName("timetable")
    @Expose
    private String timetable;
    @SerializedName("delay")
    @Expose
    private String delay;
    @SerializedName("platform")
    @Expose
    private String platform;

    /**
     * 
     * @return
     *     The line
     */
    public String getLine() {
        return line;
    }

    /**
     * 
     * @param line
     *     The line
     */
    public void setLine(String line) {
        this.line = line;
    }

    /**
     * 
     * @return
     *     The direction
     */
    public String getDirection() {
        return direction;
    }

    /**
     * 
     * @param direction
     *     The direction
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     * 
     * @return
     *     The countdown
     */
    public String getCountdown() {
        return countdown;
    }

    /**
     * 
     * @param countdown
     *     The countdown
     */
    public void setCountdown(String countdown) {
        this.countdown = countdown;
    }

    /**
     * 
     * @return
     *     The realtime
     */
    public String getRealtime() {
        return realtime;
    }

    /**
     * 
     * @param realtime
     *     The realtime
     */
    public void setRealtime(String realtime) {
        this.realtime = realtime;
    }

    /**
     * 
     * @return
     *     The timetable
     */
    public String getTimetable() {
        return timetable;
    }

    /**
     * 
     * @param timetable
     *     The timetable
     */
    public void setTimetable(String timetable) {
        this.timetable = timetable;
    }

    /**
     * 
     * @return
     *     The delay
     */
    public String getDelay() {
        return delay;
    }

    /**
     * 
     * @param delay
     *     The delay
     */
    public void setDelay(String delay) {
        this.delay = delay;
    }

    /**
     * 
     * @return
     *     The platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * 
     * @param platform
     *     The platform
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

}
