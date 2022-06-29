package ru.croc.ugd.ssr.service.excel.person.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonLetterAndContractDto {
    private String affairId;
    private String letterId;
    private LocalDate letterDate;
    private String letterChedFileId;
    private LocalDate administrativeDocumentDate;
    private String administrativeDocumentChedFileId;
    private String orderId;
    private String rtfContractToSignChedFileId;
    private String pdfContractChedFileId;
}
