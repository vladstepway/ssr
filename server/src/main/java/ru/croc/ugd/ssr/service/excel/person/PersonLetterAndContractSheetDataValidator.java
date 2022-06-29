package ru.croc.ugd.ssr.service.excel.person;

import static ru.croc.ugd.ssr.service.excel.person.model.PersonLetterAndContractSheetConstants.ADMINISTRATIVE_DOCUMENT_DATE_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.excel.person.model.PersonLetterAndContractSheetConstants.AFFAIR_ID_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.excel.person.model.PersonLetterAndContractSheetConstants.LETTER_DATE_COLUMN_INDEX;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.excel.model.ExtractedCellValue;
import ru.croc.ugd.ssr.service.validator.MappedValueValidator;
import ru.croc.ugd.ssr.service.validator.impl.excel.DateValidator;
import ru.croc.ugd.ssr.service.validator.impl.excel.MandatoryValidator;

import javax.annotation.PostConstruct;

@Service
public class PersonLetterAndContractSheetDataValidator<T extends ExtractedCellValue> extends MappedValueValidator<T> {

    @PostConstruct
    public void init() {
        this.registerValidatorInMap(AFFAIR_ID_COLUMN_INDEX.getColumnIndex(), new MandatoryValidator());
        this.registerValidatorInMap(LETTER_DATE_COLUMN_INDEX.getColumnIndex(), new DateValidator());
        this.registerValidatorInMap(ADMINISTRATIVE_DOCUMENT_DATE_COLUMN_INDEX.getColumnIndex(), new DateValidator());
    }

    @Override
    public Object getKeyForValidator(final ExtractedCellValue extractedCellValue) {
        return extractedCellValue.getColIndex();
    }
}
