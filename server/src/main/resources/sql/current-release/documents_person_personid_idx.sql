CREATE INDEX documents_person_personid_idx
    ON ssr.documents ((json_data -> 'Person' -> 'PersonData' ->> 'personID'));
