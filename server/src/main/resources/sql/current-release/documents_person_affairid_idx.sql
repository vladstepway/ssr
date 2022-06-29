CREATE INDEX documents_person_affairid_idx
    ON ssr.documents ((json_data -> 'Person' -> 'PersonData' ->> 'affairId'));
