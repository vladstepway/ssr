package ru.croc.ugd.ssr.db.projection;

import java.time.LocalDateTime;

/**
 * CommissionInspectionProjection.
 */
public interface CommissionInspectionProjection {

    /**
     * getId.
     * @return id
     */
    String getId();

    /**
     * getEno.
     * @return eno
     */
    String getEno();

    /**
     * getFlatNum.
     * @return flat num
     */
    String getFlatNum();

    /**
     * getInspectionDateTime.
     * @return inspection date time
     */
    String getInspectionDateTime();
}
