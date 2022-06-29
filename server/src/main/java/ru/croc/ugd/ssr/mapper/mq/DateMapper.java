package ru.croc.ugd.ssr.mapper.mq;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.util.XmlUtils;

import java.time.ZonedDateTime;
import javax.xml.datatype.XMLGregorianCalendar;

@Service
@AllArgsConstructor
@Slf4j
public class DateMapper {

    private final XmlUtils xmlUtils;

    public XMLGregorianCalendar map(ZonedDateTime value) {
        return xmlUtils.getXmlDate(value);
    }
}
