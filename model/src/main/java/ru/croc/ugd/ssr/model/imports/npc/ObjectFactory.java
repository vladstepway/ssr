package ru.croc.ugd.ssr.model.imports.npc;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the response11825 package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _RootRegionKvartalStartsTepStartTep_QNAME = new QName("", "start_tep");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: response11825
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Root }
     *
     */
    public Root createRoot() {
        return new Root();
    }

    /**
     * Create an instance of {@link Root.Region }
     *
     */
    public Root.Region createRootRegion() {
        return new Root.Region();
    }

    /**
     * Create an instance of {@link Root.Region.Kvartal }
     *
     */
    public Root.Region.Kvartal createRootRegionKvartal() {
        return new Root.Region.Kvartal();
    }

    /**
     * Create an instance of {@link Root.Region.Kvartal.StartsTep }
     *
     */
    public Root.Region.Kvartal.StartsTep createRootRegionKvartalStartsTep() {
        return new Root.Region.Kvartal.StartsTep();
    }

    /**
     * Create an instance of {@link Root.Region.Kvartal.StartsTep.StartTep }
     *
     */
    public Root.Region.Kvartal.StartsTep.StartTep createRootRegionKvartalStartsTepStartTep() {
        return new Root.Region.Kvartal.StartsTep.StartTep();
    }

    /**
     * Create an instance of {@link Root.Region.Kvartal.StartsTep.StartTep.Buildobjects }
     *
     */
    public Root.Region.Kvartal.StartsTep.StartTep.Buildobjects createRootRegionKvartalStartsTepStartTepBuildobjects() {
        return new Root.Region.Kvartal.StartsTep.StartTep.Buildobjects();
    }

    /**
     * Create an instance of {@link Root.Region.Kvartal.SnosBuildings }
     *
     */
    public Root.Region.Kvartal.SnosBuildings createRootRegionKvartalSnosBuildings() {
        return new Root.Region.Kvartal.SnosBuildings();
    }

    /**
     * Create an instance of {@link Root.Region.Kvartal.SnosBuildings.SnosBuilding }
     *
     */
    public Root.Region.Kvartal.SnosBuildings.SnosBuilding createRootRegionKvartalSnosBuildingsSnosBuilding() {
        return new Root.Region.Kvartal.SnosBuildings.SnosBuilding();
    }

    /**
     * Create an instance of {@link Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats }
     *
     */
    public Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats createRootRegionKvartalSnosBuildingsSnosBuildingSnosFlats() {
        return new Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats();
    }

    /**
     * Create an instance of {@link Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats.SnosFlat }
     *
     */
    public Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats.SnosFlat createRootRegionKvartalSnosBuildingsSnosBuildingSnosFlatsSnosFlat() {
        return new Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats.SnosFlat();
    }

    /**
     * Create an instance of {@link Root.Region.Kvartal.StartsTep.StartTep.Buildobjects.Buildobject }
     *
     */
    public Root.Region.Kvartal.StartsTep.StartTep.Buildobjects.Buildobject createRootRegionKvartalStartsTepStartTepBuildobjectsBuildobject() {
        return new Root.Region.Kvartal.StartsTep.StartTep.Buildobjects.Buildobject();
    }

    /**
     * Create an instance of {@link Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats.SnosFlat.SnosLic }
     *
     */
    public Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats.SnosFlat.SnosLic createRootRegionKvartalSnosBuildingsSnosBuildingSnosFlatsSnosFlatSnosLic() {
        return new Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats.SnosFlat.SnosLic();
    }

    /**
     * Create an instance of {@link Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats.SnosFlat.FlatGraf }
     *
     */
    public Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats.SnosFlat.FlatGraf createRootRegionKvartalSnosBuildingsSnosBuildingSnosFlatsSnosFlatFlatGraf() {
        return new Root.Region.Kvartal.SnosBuildings.SnosBuilding.SnosFlats.SnosFlat.FlatGraf();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Root.Region.Kvartal.StartsTep.StartTep }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "start_tep", scope = Root.Region.Kvartal.StartsTep.class)
    public JAXBElement<Root.Region.Kvartal.StartsTep.StartTep> createRootRegionKvartalStartsTepStartTep(Root.Region.Kvartal.StartsTep.StartTep value) {
        return new JAXBElement<Root.Region.Kvartal.StartsTep.StartTep>(_RootRegionKvartalStartsTepStartTep_QNAME, Root.Region.Kvartal.StartsTep.StartTep.class, Root.Region.Kvartal.StartsTep.class, value);
    }

}
