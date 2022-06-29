
package ru.croc.ugd.ssr.model.integration.asur.snilsbyadditional;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the mypackage package. 
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

    private final static QName _ResidencePermitRF_QNAME = new QName("urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", "ResidencePermitRF");
    private final static QName _ReleaseCertificate_QNAME = new QName("urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", "ReleaseCertificate");
    private final static QName _BirthCertificate_QNAME = new QName("urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", "BirthCertificate");
    private final static QName _Form9Certificate_QNAME = new QName("urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", "Form9Certificate");
    private final static QName _IdentityDocument_QNAME = new QName("urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", "IdentityDocument");
    private final static QName _PrimaryIdentityDocument_QNAME = new QName("urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", "PrimaryIdentityDocument");
    private final static QName _TemporaryIdentityCardRF_QNAME = new QName("urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", "TemporaryIdentityCardRF");
    private final static QName _PassportRF_QNAME = new QName("urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", "PassportRF");
    private final static QName _AttachmentRef_QNAME = new QName("urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", "AttachmentRef");
    private final static QName _MilitaryPassport_QNAME = new QName("urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", "MilitaryPassport");
    private final static QName _SailorPassport_QNAME = new QName("urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", "SailorPassport");
    private final static QName _DrivingLicenseRF_QNAME = new QName("urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", "DrivingLicenseRF");
    private final static QName _PassportLossCertificate_QNAME = new QName("urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", "PassportLossCertificate");
    private final static QName _ForeignPassport_QNAME = new QName("urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", "ForeignPassport");
    private final static QName _InternationalPassportRF_QNAME = new QName("urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", "InternationalPassportRF");
    private final static QName _SovietPassport_QNAME = new QName("urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", "SovietPassport");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: mypackage
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SnilsByAdditionalDataResponse }
     * 
     */
    public SnilsByAdditionalDataResponse createSnilsByAdditionalDataResponse() {
        return new SnilsByAdditionalDataResponse();
    }

    /**
     * Create an instance of {@link BirthPlaceType }
     * 
     */
    public BirthPlaceType createBirthPlaceType() {
        return new BirthPlaceType();
    }

    /**
     * Create an instance of {@link PassportRFType }
     * 
     */
    public PassportRFType createPassportRFType() {
        return new PassportRFType();
    }

    /**
     * Create an instance of {@link NotRestrictedDocumentType }
     * 
     */
    public NotRestrictedDocumentType createNotRestrictedDocumentType() {
        return new NotRestrictedDocumentType();
    }

    /**
     * Create an instance of {@link InternationalPassportRFType }
     * 
     */
    public InternationalPassportRFType createInternationalPassportRFType() {
        return new InternationalPassportRFType();
    }

    /**
     * Create an instance of {@link MilitaryPassportDocumentType }
     * 
     */
    public MilitaryPassportDocumentType createMilitaryPassportDocumentType() {
        return new MilitaryPassportDocumentType();
    }

    /**
     * Create an instance of {@link SovietPassportType }
     * 
     */
    public SovietPassportType createSovietPassportType() {
        return new SovietPassportType();
    }

    /**
     * Create an instance of {@link DrivingLicenseRFType }
     * 
     */
    public DrivingLicenseRFType createDrivingLicenseRFType() {
        return new DrivingLicenseRFType();
    }

    /**
     * Create an instance of {@link IdentificationDocumentType }
     * 
     */
    public IdentificationDocumentType createIdentificationDocumentType() {
        return new IdentificationDocumentType();
    }

    /**
     * Create an instance of {@link PrimaryIdentityDocumentType }
     * 
     */
    public PrimaryIdentityDocumentType createPrimaryIdentityDocumentType() {
        return new PrimaryIdentityDocumentType();
    }

    /**
     * Create an instance of {@link AnyIdentityDocumentType }
     * 
     */
    public AnyIdentityDocumentType createAnyIdentityDocumentType() {
        return new AnyIdentityDocumentType();
    }

    /**
     * Create an instance of {@link AttachmentRefType }
     * 
     */
    public AttachmentRefType createAttachmentRefType() {
        return new AttachmentRefType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InternationalPassportRFType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", name = "ResidencePermitRF")
    public JAXBElement<InternationalPassportRFType> createResidencePermitRF(InternationalPassportRFType value) {
        return new JAXBElement<InternationalPassportRFType>(_ResidencePermitRF_QNAME, InternationalPassportRFType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotRestrictedDocumentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", name = "ReleaseCertificate")
    public JAXBElement<NotRestrictedDocumentType> createReleaseCertificate(NotRestrictedDocumentType value) {
        return new JAXBElement<NotRestrictedDocumentType>(_ReleaseCertificate_QNAME, NotRestrictedDocumentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SovietPassportType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", name = "BirthCertificate")
    public JAXBElement<SovietPassportType> createBirthCertificate(SovietPassportType value) {
        return new JAXBElement<SovietPassportType>(_BirthCertificate_QNAME, SovietPassportType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotRestrictedDocumentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", name = "Form9Certificate")
    public JAXBElement<NotRestrictedDocumentType> createForm9Certificate(NotRestrictedDocumentType value) {
        return new JAXBElement<NotRestrictedDocumentType>(_Form9Certificate_QNAME, NotRestrictedDocumentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AnyIdentityDocumentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", name = "IdentityDocument")
    public JAXBElement<AnyIdentityDocumentType> createIdentityDocument(AnyIdentityDocumentType value) {
        return new JAXBElement<AnyIdentityDocumentType>(_IdentityDocument_QNAME, AnyIdentityDocumentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PrimaryIdentityDocumentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", name = "PrimaryIdentityDocument")
    public JAXBElement<PrimaryIdentityDocumentType> createPrimaryIdentityDocument(PrimaryIdentityDocumentType value) {
        return new JAXBElement<PrimaryIdentityDocumentType>(_PrimaryIdentityDocument_QNAME, PrimaryIdentityDocumentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotRestrictedDocumentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", name = "TemporaryIdentityCardRF")
    public JAXBElement<NotRestrictedDocumentType> createTemporaryIdentityCardRF(NotRestrictedDocumentType value) {
        return new JAXBElement<NotRestrictedDocumentType>(_TemporaryIdentityCardRF_QNAME, NotRestrictedDocumentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PassportRFType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", name = "PassportRF")
    public JAXBElement<PassportRFType> createPassportRF(PassportRFType value) {
        return new JAXBElement<PassportRFType>(_PassportRF_QNAME, PassportRFType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AttachmentRefType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", name = "AttachmentRef")
    public JAXBElement<AttachmentRefType> createAttachmentRef(AttachmentRefType value) {
        return new JAXBElement<AttachmentRefType>(_AttachmentRef_QNAME, AttachmentRefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MilitaryPassportDocumentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", name = "MilitaryPassport")
    public JAXBElement<MilitaryPassportDocumentType> createMilitaryPassport(MilitaryPassportDocumentType value) {
        return new JAXBElement<MilitaryPassportDocumentType>(_MilitaryPassport_QNAME, MilitaryPassportDocumentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MilitaryPassportDocumentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", name = "SailorPassport")
    public JAXBElement<MilitaryPassportDocumentType> createSailorPassport(MilitaryPassportDocumentType value) {
        return new JAXBElement<MilitaryPassportDocumentType>(_SailorPassport_QNAME, MilitaryPassportDocumentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DrivingLicenseRFType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", name = "DrivingLicenseRF")
    public JAXBElement<DrivingLicenseRFType> createDrivingLicenseRF(DrivingLicenseRFType value) {
        return new JAXBElement<DrivingLicenseRFType>(_DrivingLicenseRF_QNAME, DrivingLicenseRFType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotRestrictedDocumentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", name = "PassportLossCertificate")
    public JAXBElement<NotRestrictedDocumentType> createPassportLossCertificate(NotRestrictedDocumentType value) {
        return new JAXBElement<NotRestrictedDocumentType>(_PassportLossCertificate_QNAME, NotRestrictedDocumentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotRestrictedDocumentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", name = "ForeignPassport")
    public JAXBElement<NotRestrictedDocumentType> createForeignPassport(NotRestrictedDocumentType value) {
        return new JAXBElement<NotRestrictedDocumentType>(_ForeignPassport_QNAME, NotRestrictedDocumentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InternationalPassportRFType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", name = "InternationalPassportRF")
    public JAXBElement<InternationalPassportRFType> createInternationalPassportRF(InternationalPassportRFType value) {
        return new JAXBElement<InternationalPassportRFType>(_InternationalPassportRF_QNAME, InternationalPassportRFType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SovietPassportType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1", name = "SovietPassport")
    public JAXBElement<SovietPassportType> createSovietPassport(SovietPassportType value) {
        return new JAXBElement<SovietPassportType>(_SovietPassport_QNAME, SovietPassportType.class, null, value);
    }

}
