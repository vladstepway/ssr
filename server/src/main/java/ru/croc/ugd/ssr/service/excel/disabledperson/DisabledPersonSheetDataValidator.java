package ru.croc.ugd.ssr.service.excel.disabledperson;

import static ru.croc.ugd.ssr.service.excel.disabledperson.model.DisabledPersonSheetConstants.ADDRESS_FROM_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.excel.disabledperson.model.DisabledPersonSheetConstants.BIRTH_DATE_COLUMN_INDEX;
import static ru.croc.ugd.ssr.service.excel.disabledperson.model.DisabledPersonSheetConstants.UNOM_COLUMN_INDEX;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.excel.model.ExtractedCellValue;
import ru.croc.ugd.ssr.service.validator.MappedValueValidator;
import ru.croc.ugd.ssr.service.validator.impl.excel.DateValidator;
import ru.croc.ugd.ssr.service.validator.impl.excel.MandatoryValidator;

import javax.annotation.PostConstruct;

@Service
public class DisabledPersonSheetDataValidator<T extends ExtractedCellValue> extends MappedValueValidator<T> {

    @PostConstruct
    public void init() {
        this.registerValidatorInMap(UNOM_COLUMN_INDEX.getColumnIndex(), new MandatoryValidator());
        this.registerValidatorInMap(ADDRESS_FROM_COLUMN_INDEX.getColumnIndex(), new MandatoryValidator());
        this.registerValidatorInMap(BIRTH_DATE_COLUMN_INDEX.getColumnIndex(), new DateValidator());
    }

    @Override
    public Object getKeyForValidator(final ExtractedCellValue extractedCellValue) {
        return extractedCellValue.getColIndex();
    }
}
