CREATE OR REPLACE FUNCTION ssr.jsonpatch_has(source jsonb, search text)
RETURNS boolean AS 
'SELECT source::TEXT like (''%'' || search || ''%'')'
LANGUAGE sql IMMUTABLE STRICT;
