CREATE INDEX documents_person_unom_idx
    ON ssr.documents ((json_data -> 'Person' -> 'PersonData' ->> 'UNOM'));
