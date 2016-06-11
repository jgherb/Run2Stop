
package de.jhs.run2stop.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class BusData {

    @SerializedName("info")
    @Expose
    private Info info;
    @SerializedName("departures")
    @Expose
    private List<Departure> departures = new ArrayList<Departure>();

    /**
     * 
     * @return
     *     The info
     */
    public Info getInfo() {
        return info;
    }

    /**
     * 
     * @param info
     *     The info
     */
    public void setInfo(Info info) {
        this.info = info;
    }

    /**
     * 
     * @return
     *     The departures
     */
    public List<Departure> getDepartures() {
        return departures;
    }

    /**
     * 
     * @param departures
     *     The departures
     */
    public void setDepartures(List<Departure> departures) {
        this.departures = departures;
    }

}
