CREATE INDEX documents_person_snils_ssoid_idx
    ON ssr.documents ((json_data -> 'Person' -> 'PersonData' ->> 'SNILS'),
                  (json_data -> 'Person' -> 'PersonData' ->> 'SsoID'));
