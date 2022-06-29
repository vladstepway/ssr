package ru.croc.ugd.ssr.db.projection;

public interface DisabledPersonDetailsProjection {
    boolean getUsingWheelchair();

    String getPersonDocumentId();

    String getCreatedAt();
}
