package ru.croc.ugd.ssr.db.projection;

/**
 * ApartmentDefectProjection.
 */
public interface ApartmentDefectProjection {

    String getId();

    String getApartmentInspectionId();

    String getFlat();

    Integer getFloor();

    String getEntrance();

    String getFlatElement();

    String getDescription();

    Boolean getIsBlocked();

    String getAffairId();

    String getOldEliminationDate();

    String getEliminationDate();

    String getEliminationDateComment();

    Boolean getItemRequired();

    String getItemRequiredComment();

    Boolean getIsEliminated();

    Boolean getIsNotDefect();

    String getNotDefectComment();
}
