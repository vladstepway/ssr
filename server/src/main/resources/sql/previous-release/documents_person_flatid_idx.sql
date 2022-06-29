CREATE INDEX documents_person_flatid_idx
    ON ssr.documents ((json_data -> 'Person' -> 'PersonData' ->> 'flatID'));
