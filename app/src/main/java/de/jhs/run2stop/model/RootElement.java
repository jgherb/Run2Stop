
package de.jhs.run2stop.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class RootElement {

    @SerializedName("version")
    @Expose
    private Double version;
    @SerializedName("generator")
    @Expose
    private String generator;
    @SerializedName("osm3s")
    @Expose
    private Osm3s osm3s;
    @SerializedName("elements")
    @Expose
    private List<Element> elements = new ArrayList<Element>();

    /**
     * 
     * @return
     *     The version
     */
    public Double getVersion() {
        return version;
    }

    /**
     * 
     * @param version
     *     The version
     */
    public void setVersion(Double version) {
        this.version = version;
    }

    /**
     * 
     * @return
     *     The generator
     */
    public String getGenerator() {
        return generator;
    }

    /**
     * 
     * @param generator
     *     The generator
     */
    public void setGenerator(String generator) {
        this.generator = generator;
    }

    /**
     * 
     * @return
     *     The osm3s
     */
    public Osm3s getOsm3s() {
        return osm3s;
    }

    /**
     * 
     * @param osm3s
     *     The osm3s
     */
    public void setOsm3s(Osm3s osm3s) {
        this.osm3s = osm3s;
    }

    /**
     * 
     * @return
     *     The elements
     */
    public List<Element> getElements() {
        return elements;
    }

    /**
     * 
     * @param elements
     *     The elements
     */
    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

}
