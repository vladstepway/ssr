package ru.croc.ugd.ssr.report;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.reinform.cdp.security.utils.RiAuthenticationUtils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReportTest extends AbstractReportTest {

    private static final String AFFAIR_ID = "91496";

    private static final String PERSON_ID = "657925";

    @Autowired
    private RiAuthenticationUtils riAuthenticationUtils;

    @Autowired
    private PersonDocumentService personDocumentService;

    @Before
    public void init() {
        riAuthenticationUtils.setSecurityContextByServiceuser();
        PersonDocument personDocument = new PersonDocument();
        Person person = new Person();
        PersonType personType = new PersonType();
        personType.setUNOM(new BigInteger("29825"));
        personType.setAffairId(AFFAIR_ID);
        personType.setPersonID(PERSON_ID);
        personType.setFlatID("flatId");

        personType.setOfferLetters(new PersonType.OfferLetters());
        personType.getOfferLetters().getOfferLetter().add(new PersonType.OfferLetters.OfferLetter());
        personType.getOfferLetters().getOfferLetter().get(0).setDate(LocalDate.now());

        personType.setFlatDemo(new PersonType.FlatDemo());
        personType.getFlatDemo().getFlatDemoItem().add(new PersonType.FlatDemo.FlatDemoItem());
        personType.getFlatDemo().getFlatDemoItem().get(0).setDate(LocalDate.now());

        personType.setAgreements(new PersonType.Agreements());
        personType.getAgreements().getAgreement().add(new PersonType.Agreements.Agreement());
        personType.getAgreements().getAgreement().get(0).setDate(LocalDate.now());

        personType.setContracts(new PersonType.Contracts());
        personType.getContracts().getContract().add(new PersonType.Contracts.Contract());
        personType.getContracts().getContract().get(0).setMsgDateTime(LocalDateTime.now());

        personType.setContracts(new PersonType.Contracts());
        personType.getContracts().getContract().add(new PersonType.Contracts.Contract());
        personType.getContracts().getContract().get(1).setContractSignDate(LocalDate.now());

        personType.setNewFlatInfo(new PersonType.NewFlatInfo());
        personType.getNewFlatInfo().getNewFlat().add(new PersonType.NewFlatInfo.NewFlat());
        personType.getNewFlatInfo().getNewFlat().get(0).setMsgDateTime(LocalDateTime.now());

        personType.setKeysIssue(new PersonType.KeysIssue());
        personType.getKeysIssue().setActDate(LocalDate.now());

        personType.setReleaseFlat(new PersonType.ReleaseFlat());
        personType.getReleaseFlat().setActDate(LocalDate.now());



        person.setPersonData(personType);
        personDocument.setDocument(person);

        personDocumentService.createDocument(personDocument, false, "test");

    }

    @Test
    public void testUpdateInfo() {
        System.out.println(personDocumentService.fetchOneByPersonIdAndAffairId(PERSON_ID, AFFAIR_ID));
    }
}
