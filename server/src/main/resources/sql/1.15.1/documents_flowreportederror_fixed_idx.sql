CREATE INDEX documents_flowreportederror_fixed_idx
    ON ssr.documents ((COALESCE(json_data -> 'flowReportedError' -> 'flowReportedErrorData' ->> 'fixed', 'false')));
