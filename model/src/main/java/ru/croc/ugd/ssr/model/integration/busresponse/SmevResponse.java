package ru.croc.ugd.ssr.model.integration.busresponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@NoArgsConstructor
@AllArgsConstructor
@Data
@XmlRootElement(name = "SmevResponse")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class SmevResponse {

    private String messageId;
    private String serviceNumber;
    private String statusCode;
    private String note;
    private String resultCode;
    private String resultDescription;
    private String ochdFolderGuid;
}
