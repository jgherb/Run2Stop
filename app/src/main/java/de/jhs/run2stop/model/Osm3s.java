
package de.jhs.run2stop.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Osm3s {

    @SerializedName("timestamp_osm_base")
    @Expose
    private String timestampOsmBase;
    @SerializedName("copyright")
    @Expose
    private String copyright;

    /**
     * 
     * @return
     *     The timestampOsmBase
     */
    public String getTimestampOsmBase() {
        return timestampOsmBase;
    }

    /**
     * 
     * @param timestampOsmBase
     *     The timestamp_osm_base
     */
    public void setTimestampOsmBase(String timestampOsmBase) {
        this.timestampOsmBase = timestampOsmBase;
    }

    /**
     * 
     * @return
     *     The copyright
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     * 
     * @param copyright
     *     The copyright
     */
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

}
