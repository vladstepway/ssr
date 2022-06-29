package ru.croc.ugd.ssr.service.reporter.apartmentinspection;

import ru.croc.ugd.ssr.service.reporter.ReportFieldType;

public enum ApartmentInspectionFieldType implements ReportFieldType {

    CURRENT_DATE_TEXT,
    APARTMENT_INSPECTION_NUMBER,
    APPLICANT_FIO,
    APPLICANT_PHONE,
    NEW_FLAT_ADDRESS,
    OLD_FLAT_ADDRESS,
    GENERAL_CONTRACTOR_ORGANIZATION,
    DEVELOPER_ORGANIZATION,
    DEFECT_LIST,
    COMMISSION_DECISION;

    @Override
    public String getFieldName() {
        return name();
    }
}
