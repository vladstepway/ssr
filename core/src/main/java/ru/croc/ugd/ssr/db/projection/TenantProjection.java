package ru.croc.ugd.ssr.db.projection;

/**
 * TenantProjection.
 */
public interface TenantProjection {

    /**
     * getId.
     * @return id
     */
    String getId();

    /**
     * getPersonId.
     * @return person id
     */
    String getPersonId();

    /**
     * getFullName.
     * @return full name
     */
    String getFullName();

    /**
     * getLastName.
     * @return last name
     */
    String getLastName();

    /**
     * getFirstName.
     * @return first name
     */
    String getFirstName();

    /**
     * getMiddleName.
     * @return middle name
     */
    String getMiddleName();

    /**
     * getBirthDate.
     * @return birthdate
     */
    String getBirthDate();

    /**
     * getStatusLiving.
     * @return status living
     */
    String getStatusLiving();
}
