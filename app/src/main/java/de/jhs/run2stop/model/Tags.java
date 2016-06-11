
package de.jhs.run2stop.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Tags {

    @SerializedName("highway")
    @Expose
    private String highway;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("public_transport")
    @Expose
    private String publicTransport;

    /**
     * 
     * @return
     *     The highway
     */
    public String getHighway() {
        return highway;
    }

    /**
     * 
     * @param highway
     *     The highway
     */
    public void setHighway(String highway) {
        this.highway = highway;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The publicTransport
     */
    public String getPublicTransport() {
        return publicTransport;
    }

    /**
     * 
     * @param publicTransport
     *     The public_transport
     */
    public void setPublicTransport(String publicTransport) {
        this.publicTransport = publicTransport;
    }

}
