
package de.jhs.run2stop.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Info {

    @SerializedName("stopid")
    @Expose
    private String stopid;
    @SerializedName("stopname")
    @Expose
    private String stopname;
    @SerializedName("stopplace")
    @Expose
    private String stopplace;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    /**
     * 
     * @return
     *     The stopid
     */
    public String getStopid() {
        return stopid;
    }

    /**
     * 
     * @param stopid
     *     The stopid
     */
    public void setStopid(String stopid) {
        this.stopid = stopid;
    }

    /**
     * 
     * @return
     *     The stopname
     */
    public String getStopname() {
        return stopname;
    }

    /**
     * 
     * @param stopname
     *     The stopname
     */
    public void setStopname(String stopname) {
        this.stopname = stopname;
    }

    /**
     * 
     * @return
     *     The stopplace
     */
    public String getStopplace() {
        return stopplace;
    }

    /**
     * 
     * @param stopplace
     *     The stopplace
     */
    public void setStopplace(String stopplace) {
        this.stopplace = stopplace;
    }

    /**
     * 
     * @return
     *     The timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * 
     * @param timestamp
     *     The timestamp
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
