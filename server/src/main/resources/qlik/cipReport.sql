LIB CONNECT TO 'PostgreSQL_UGD';

[person_resettlement]:
LOAD *;
SQL
WITH person AS (
    SELECT id                                                   as id,
           json_data -> 'Person' -> 'PersonData' ->> 'FIO'      as person_full_name,
           json_data -> 'Person' -> 'PersonData' ->> 'affairId' as affair_id,
           json_data -> 'Person' -> 'PersonData' ->> 'flatNum'  as flat_number,
           json_data -> 'Person' -> 'PersonData' ->> 'UNOM'     as unom,
           string_agg(coalesce(roomNumbers, ''), ', ')          as room_numbers
    FROM ssr.documents
             LEFT JOIN jsonb_array_elements_text(json_data -> 'Person' -> 'PersonData' -> 'roomNum') as roomNumbers
                       ON TRUE
    WHERE doc_type = 'PERSON'
      AND json_data -> 'Person' -> 'PersonData' -> 'resettlementHistory' IS NOT NULL
      AND (json_data -> 'Person' -> 'PersonData' ->> 'isArchive' = 'false'
        OR json_data -> 'Person' -> 'PersonData' ->> 'isArchive' IS NULL)
      AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
    GROUP BY id
),
     realEstate AS (
         SELECT DISTINCT json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM'    as unom,
                         json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Address' as address
         FROM ssr.documents
         WHERE doc_type = 'REAL-ESTATE'
     ),
     offerLetter AS (
         SELECT id                                                    as person_id,
                offerLetter ->> 'letterId'                            as letter_id,
                to_char((offerLetter ->> 'date')::date, 'DD.MM.YYYY') as letter_receive_date
         FROM ssr.documents,
              jsonb_array_elements(
                      json_data -> 'Person' -> 'PersonData' -> 'offerLetters' -> 'offerLetter') as offerLetter
         WHERE doc_type = 'PERSON'
               AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
     ),
     apartmentDemoTotal AS (
         SELECT flatDemo ->> 'letterId'               as letter_id,
                id                                    as person_id,
                count(flatDemo ->> 'demoId')		  as total_apartment_demo
         FROM ssr.documents,
              jsonb_array_elements(
                      json_data -> 'Person' -> 'PersonData' -> 'flatDemo' -> 'flatDemoItem') as flatDemo
         WHERE doc_type = 'PERSON'
               AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
         GROUP BY letter_id, person_id
     ),
     apartmentDemoDates AS (
         SELECT id                                                                  							  as person_id,
                flatDemo ->> 'letterId'                                              							  as letter_id,
                string_agg(to_char((flatDemo ->> 'date')::date, 'DD.MM.YYYY'), ', ' ORDER BY flatDemo ->> 'date') as demo_dates
         FROM ssr.documents,
              jsonb_array_elements(
                      json_data -> 'Person' -> 'PersonData' -> 'flatDemo' -> 'flatDemoItem') as flatDemo
         WHERE doc_type = 'PERSON'
               AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
         GROUP BY person_id, letter_id
     ),
     agreement AS (
         SELECT DISTINCT ON (id, agreements ->> 'letterId') id                                                   as person_id,
                                                            agreements ->> 'letterId'                            as letter_id,
                                                            CASE
                                                                WHEN agreements ->> 'event' in ('1', '3')
                                                                    THEN 'Согласие'
                                                                WHEN agreements ->> 'event' = '2' THEN 'Отказ'
                                                                ELSE ''
                                                                END                                              as decision,
                                                            CASE
                                                                WHEN agreements ->> 'fullDocs' = '1' THEN 'Полный пакет'
                                                                WHEN agreements ->> 'fullDocs' = '2'
                                                                    THEN 'Неполный пакет'
                                                                ELSE ''
                                                                END                                              as documents_package,
                                                            to_char((agreements ->> 'date')::date, 'DD.MM.YYYY') as decision_date
         FROM ssr.documents,
              jsonb_array_elements(
                      json_data -> 'Person' -> 'PersonData' -> 'agreements' -> 'agreement') as agreements
         WHERE doc_type = 'PERSON'
               AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
         ORDER BY id, agreements ->> 'letterId', agreements ->> 'date' DESC
     ),
     lastApprovalAgreement AS (
         SELECT DISTINCT ON (id) id                        as person_id,
                                 agreements ->> 'letterId' as letter_id
         FROM ssr.documents,
              jsonb_array_elements(
                      json_data -> 'Person' -> 'PersonData' -> 'agreements' -> 'agreement') as agreements
         WHERE doc_type = 'PERSON'
           AND agreements ->> 'event' in ('1', '3')
               AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
         ORDER BY id, row_number() over () desc
     ),
     lastOfferLetter AS (
         SELECT DISTINCT ON (id) id                         as person_id,
                                 offerLetter ->> 'letterId' as letter_id
         FROM ssr.documents,
              jsonb_array_elements(
                      json_data -> 'Person' -> 'PersonData' -> 'offerLetters' -> 'offerLetter') as offerLetter
         WHERE doc_type = 'PERSON'
               AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
         ORDER BY id, row_number() over () desc
     ),
     apartmentInspection AS (
         SELECT DISTINCT ON (coalesce(NULLIF(lastApprovalAgreement.letter_id, ''), lastOfferLetter.letter_id),
             coalesce(NULLIF(lastApprovalAgreement.person_id, ''), lastOfferLetter.person_id)) coalesce(
                                                                                                       NULLIF(lastApprovalAgreement.letter_id, ''),
                                                                                                       lastOfferLetter.letter_id) as letter_id,
                                                                                               coalesce(
                                                                                                       NULLIF(lastApprovalAgreement.person_id, ''),
                                                                                                       lastOfferLetter.person_id) as person_id,
                                                                                               json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->>
                                                                                               'address'                          as address_to,
                                                                                               json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->>
                                                                                               'flat'                             as flat_to,
                                                                                               string_agg(to_char(
                                                                                                                  (json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->>
                                                                                                                   'filingDate')::date,
                                                                                                                  'DD.MM.YYYY'),
                                                                                                          ', ')                   as creation_dates,
                                                                                               string_agg(
                                                                                                       COALESCE(to_char(
                                                                                                                        (json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->>
                                                                                                                         'acceptedDefectsDate')::date,
                                                                                                                        'DD.MM.YYYY'),
                                                                                                                '-'),
                                                                                                       ', ')                      as defect_accept_dates
         FROM ssr.documents
                  LEFT JOIN lastApprovalAgreement
                            ON json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->>
                               'personID' = lastApprovalAgreement.person_id
                  LEFT JOIN lastOfferLetter
                            ON json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->>
                               'personID' = lastOfferLetter.person_id
         WHERE doc_type = 'APARTMENT-INSPECTION'
           and (json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'isRemoved' is null
             OR json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'isRemoved' = 'false')
         GROUP BY coalesce(NULLIF(lastApprovalAgreement.letter_id, ''), lastOfferLetter.letter_id), address_to, flat_to,
                  coalesce(NULLIF(lastApprovalAgreement.person_id, ''), lastOfferLetter.person_id)
     ),
     contractOrder AS (
         SELECT DISTINCT ON (id, contract_order ->> 'orderId') id                                                              as person_id,
                                                               lastApprovalAgreement.letter_id                                 as letter_id,
                                                               contract_order ->> 'orderId'                                    as contract_order_id,
                                                               to_char((contract_order ->> 'msgDateTime')::date, 'DD.MM.YYYY') as creation_date
         FROM ssr.documents
                  LEFT JOIN jsonb_array_elements(
                 json_data -> 'Person' -> 'PersonData' -> 'contracts' -> 'contract') as contract_order ON TRUE
                  LEFT JOIN lastApprovalAgreement ON id = lastApprovalAgreement.person_id
         WHERE doc_type = 'PERSON'
           AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
           AND contract_order ->> 'orderId' IS NOT NULL
           AND contract_order ->> 'contractStatus' in ('2', '3')
         ORDER BY id, contract_order_id, contract_order ->> 'msgDateTime' ASC
     ),
     contract AS (
         SELECT DISTINCT ON (id, contract_order ->> 'orderId') id                                                                   as person_id,
                                                               contract_order ->> 'orderId'                                         as contract_order_id,
                                                               to_char((contract_order ->> 'contractSignDate')::date, 'DD.MM.YYYY') as sign_date
         FROM ssr.documents
                  LEFT JOIN jsonb_array_elements(
                 json_data -> 'Person' -> 'PersonData' -> 'contracts' -> 'contract') as contract_order ON TRUE
         WHERE doc_type = 'PERSON'
           AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
           AND contract_order ->> 'orderId' IS NOT NULL
           AND contract_order ->> 'contractStatus' in ('2', '3')
         ORDER BY id, contract_order_id, contract_order ->> 'msgDateTime' DESC
     ),
     lastPreparedContract AS (
         SELECT DISTINCT ON (id, contract_order ->> 'orderId') id                           as person_id,
                                                               contract_order ->> 'orderId' as contract_order_id
         FROM ssr.documents
                  LEFT JOIN jsonb_array_elements(
                 json_data -> 'Person' -> 'PersonData' -> 'contracts' -> 'contract') as contract_order ON TRUE
         WHERE doc_type = 'PERSON'
           AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
           AND contract_order ->> 'orderId' IS NOT NULL
           AND contract_order ->> 'contractStatus' in ('2', '3')
         ORDER BY id, contract_order_id, contract_order ->> 'msgDateTime' DESC
     ),
     key_return AS (
         SELECT id                                     as person_id,
                lastPreparedContract.contract_order_id as contract_order_id,
                to_char((json_data -> 'Person' -> 'PersonData' -> 'releaseFlat' ->> 'actDate')::date,
                        'DD.MM.YYYY')                  as return_date
         FROM ssr.documents
                  LEFT JOIN lastPreparedContract ON id = lastPreparedContract.person_id
         WHERE doc_type = 'PERSON'
               AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
     ),
     key_issue AS (
         SELECT id                                     as person_id,
                lastPreparedContract.contract_order_id as contract_order_id,
                to_char((json_data -> 'Person' -> 'PersonData' -> 'keysIssue' ->> 'actDate')::date,
                        'DD.MM.YYYY')                  as issue_date
         FROM ssr.documents
                  LEFT JOIN lastPreparedContract ON id = lastPreparedContract.person_id
         WHERE doc_type = 'PERSON'
               AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
     ),
     cco AS (
         select json_data -> 'capitalConstructionObject' -> 'capitalConstructionObjectData' ->> 'unom' as cco_unom,
                coalesce(json_data -> 'capitalConstructionObject' -> 'capitalConstructionObjectData' ->
                         'buildingAddress' ->>
                         'addressString',
                         json_data -> 'capitalConstructionObject' -> 'capitalConstructionObjectData' ->
                         'assignedAddress' ->>
                         'addressString', '')                                                          as cco_address
         from ps.documents
         where doc_type = 'CAPITAL-CONSTRUCTION-OBJECT'
           and json_data -> 'capitalConstructionObject' -> 'capitalConstructionObjectData' ->> 'unom' IS NOT NULL
     ),
     new_flat AS (
         SELECT DISTINCT ON (id) id                                                       as person_id,
                                 lastPreparedContract.contract_order_id                   as contract_order_id,
                                 coalesce(newFlats ->> 'ccoAddress', cco.cco_address, '') as cco_address,
                                 newFlats ->> 'ccoFlatNum'                                as cco_flat_number
         FROM ssr.documents
                  LEFT JOIN jsonb_array_elements(
                 json_data -> 'Person' -> 'PersonData' -> 'newFlatInfo' -> 'newFlat') newFlats ON TRUE
                  LEFT JOIN lastPreparedContract ON id = lastPreparedContract.person_id
                  LEFT JOIN cco ON newFlats ->> 'ccoUnom' = cco.cco_unom
         WHERE doc_type = 'PERSON'
               AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
         ORDER BY id, newFlats ->> 'msgDateTime' DESC
     )
SELECT person.id                                                           as person_id,
       person.person_full_name                                             as person_full_name,
       person.affair_id                                                    as affair_id,
       realEstate.address                                                  as address,
       person.room_numbers                                                 as room_numbers,
       coalesce(person.flat_number, '')                                    as flat_number,
       offerLetter.letter_id                                               as letter_id,
       coalesce(offerLetter.letter_receive_date, '')                       as letter_received_date,
       apartmentDemoTotal.total_apartment_demo                             as total_apartment_demo,
       coalesce(apartmentDemoDates.demo_dates, '')                         as demo_dates,
       coalesce(agreement.decision, '')                                    as decision,
       coalesce(agreement.documents_package, '')                           as documents_package,
       coalesce(agreement.decision_date, '')                               as decision_date,
       coalesce(new_flat.cco_address, apartmentInspection.address_to, '')  as address_to,
       coalesce(new_flat.cco_flat_number, apartmentInspection.flat_to, '') as flat_to,
       coalesce(apartmentInspection.creation_dates, '')                    as defect_act_creation_dates,
       coalesce(apartmentInspection.defect_accept_dates, '')               as defect_act_accept_dates,
       coalesce(contractOrder.creation_date, '')                           as contract_creation_date,
       coalesce(contract.sign_date, '')                                    as contract_sign_date,
       coalesce(key_return.return_date, '')                                as key_return_date,
       coalesce(key_issue.issue_date, '')                                  as key_issue_date
FROM person
         LEFT JOIN realEstate ON person.unom = realEstate.unom
         JOIN offerLetter ON person.id = offerLetter.person_id
         LEFT JOIN apartmentDemoDates ON offerLetter.letter_id = apartmentDemoDates.letter_id AND
                                         offerLetter.person_id = apartmentDemoDates.person_id
         LEFT JOIN apartmentDemoTotal ON offerLetter.letter_id = apartmentDemoTotal.letter_id AND
                                         offerLetter.person_id = apartmentDemoTotal.person_id
         LEFT JOIN agreement
                   ON offerLetter.letter_id = agreement.letter_id AND offerLetter.person_id = agreement.person_id
         LEFT JOIN apartmentInspection ON offerLetter.letter_id = apartmentInspection.letter_id AND
                                          offerLetter.person_id = apartmentInspection.person_id
         LEFT JOIN contractOrder ON offerLetter.letter_id = contractOrder.letter_id AND
                                    offerLetter.person_id = contractOrder.person_id
         LEFT JOIN contract ON contractOrder.contract_order_id = contract.contract_order_id AND
                               contractOrder.person_id = contract.person_id
         LEFT JOIN key_return ON contractOrder.contract_order_id = key_return.contract_order_id AND
                                 contractOrder.person_id = key_return.person_id
         LEFT JOIN key_issue ON contractOrder.contract_order_id = key_issue.contract_order_id AND
                                contractOrder.person_id = key_issue.person_id
         LEFT JOIN new_flat ON contractOrder.contract_order_id = new_flat.contract_order_id AND
                               contractOrder.person_id = new_flat.person_id;

[real_estates]:
LOAD *;
SQL
WITH flatTotal AS (
    SELECT json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM'        as real_estate_unom,
           count(1)                                                        as total_flat_number,
           count(case flat ->> 'flat_type' when 'Коммунальная' then 1 end) as total_communal_flat_number
    FROM ssr.documents,
         jsonb_array_elements(
                 json_data -> 'RealEstate' -> 'RealEstateData' -> 'Flats' -> 'Flat') as flat
    WHERE doc_type = 'REAL-ESTATE'
    GROUP BY real_estate_unom
),
     realEstateTotal AS (
         SELECT json_data -> 'Person' -> 'PersonData' ->> 'UNOM'                                      as real_estate_unom,
                count(1)                                                                              as total_person,
                count(case json_data -> 'Person' -> 'PersonData' -> 'resettlementHistory' IS NOT NULL
                          when true
                              then 1 end)                                                             as total_person_resettlement,
                count(distinct (case json_data -> 'Person' -> 'PersonData' -> 'resettlementHistory' IS NOT NULL
                                    when true
                                        then json_data -> 'Person' -> 'PersonData' ->> 'flatID' end)) as total_flats_ressettlement
         FROM ssr.documents
         WHERE doc_type = 'PERSON'
               AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
         GROUP BY real_estate_unom
     ),
     realEstate AS (
         SELECT json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM'    as real_estate_unom,
                json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Address' as unom_address_from,
                COALESCE(flatTotal.total_flat_number, 0)                    as total_flat,
                COALESCE(flatTotal.total_communal_flat_number, 0)           as total_communal_flat,
                COALESCE(realEstateTotal.total_person, 0)                   as total_person,
                COALESCE(realEstateTotal.total_person_resettlement, 0)      as total_person_resettlement,
                COALESCE(realEstateTotal.total_flats_ressettlement, 0)      as total_flats_ressettlement
         FROM ssr.documents
                  LEFT JOIN flatTotal
                            ON json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' = flatTotal.real_estate_unom
                  LEFT JOIN realEstateTotal
                            ON json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' =
                               realEstateTotal.real_estate_unom
         WHERE doc_type = 'REAL-ESTATE'
           AND COALESCE(json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Address','') <> ''
           AND json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' IS NOT NULL
           AND realEstateTotal.total_flats_ressettlement > 0
     )
SELECT *
from realEstate;


[unom_offer_letter]:
select DISTINCT json_data -> 'Person' -> 'PersonData' ->> 'UNOM'      as real_estate_unom,
                offerLetter ->> 'letterId'                            as unom_offer_letter_id,
                to_char((offerLetter ->> 'date')::date, 'DD.MM.YYYY') as unom_letter_receive_date
from ssr.documents,
     jsonb_array_elements(
             json_data -> 'Person' -> 'PersonData' -> 'offerLetters' -> 'offerLetter') as offerLetter
where doc_type = 'PERSON'
               AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false';

[unom_appartment_demo]:
select DISTINCT json_data -> 'Person' -> 'PersonData' ->> 'UNOM'   as real_estate_unom,
                flatDemo ->> 'demoId'                              as unom_demo_id,
                to_char((flatDemo ->> 'date')::date, 'DD.MM.YYYY') as unom_demo_date
from ssr.documents,
     jsonb_array_elements(
             json_data -> 'Person' -> 'PersonData' -> 'flatDemo' -> 'flatDemoItem') as flatDemo
where doc_type = 'PERSON'
               AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false';

[unom_appartment_inspection]:
select doc.json_data -> 'Person' -> 'PersonData' ->> 'UNOM'   as real_estate_unom,
       defectAct.defect_act_id                                as unom_defect_act_id,
       to_char((defectAct.creation_date)::date, 'DD.MM.YYYY') as unom_defect_act_creation_date,
       to_char((defectAct.accept_date)::date, 'DD.MM.YYYY')   as unom_defect_act_accept_date
from ssr.documents doc
         JOIN (select id                    as defect_act_id,
                      json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->>
                      'personID'            as person_id,
                      json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->>
                      'filingDate'          as creation_date,
                      json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->>
                      'acceptedDefectsDate' as accept_date
               from ssr.documents
               where doc_type = 'APARTMENT-INSPECTION'
                 and (json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'isRemoved' is null
                   OR json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'isRemoved' = 'false')
) defectAct
              ON doc.id = defectAct.person_id;

[unom_contracts]:
select DISTINCT ON (json_data -> 'Person' -> 'PersonData' ->> 'UNOM',
                    contract ->> 'contractNum',
                    json_data -> 'Person' -> 'PersonData' ->> 'affairId')
                                                                                                  json_data -> 'Person' -> 'PersonData' ->> 'UNOM'          as real_estate_unom,
                                                                                                  contract ->> 'contractNum'                                as unom_contract_number,
                                                                                                  to_char((contract ->> 'msgDateTime')::date, 'DD.MM.YYYY') as unom_contract_receive_date
from ssr.documents,
     jsonb_array_elements(
             json_data -> 'Person' -> 'PersonData' -> 'contracts' -> 'contract') as contract
where doc_type = 'PERSON'
  and contract ->> 'contractStatus' in ('2', '3')
  AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
ORDER BY real_estate_unom, unom_contract_number, json_data -> 'Person' -> 'PersonData' ->> 'affairId', unom_contract_receive_date;

[unom_signed_contracts]:
select DISTINCT ON (json_data -> 'Person' -> 'PersonData' ->> 'UNOM', contract ->> 'contractNum', json_data -> 'Person' -> 'PersonData' ->> 'affairId') json_data -> 'Person' -> 'PersonData' ->> 'UNOM'               as real_estate_unom,
                                                                                                  contract ->> 'contractNum'                                     as unom_signed_contract_number,
                                                                                                  to_char((contract ->> 'contractSignDate')::date, 'DD.MM.YYYY') as unom_contract_sign_date
from ssr.documents,
     jsonb_array_elements(
             json_data -> 'Person' -> 'PersonData' -> 'contracts' -> 'contract') as contract
where doc_type = 'PERSON'
  and contract ->> 'contractStatus' in ('2', '3')
  AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
ORDER BY real_estate_unom, unom_signed_contract_number, json_data -> 'Person' -> 'PersonData' ->> 'affairId', unom_contract_sign_date DESC;

[unom_key_issue]:
select DISTINCT ON (json_data -> 'Person' -> 'PersonData' ->> 'UNOM', json_data -> 'Person' -> 'PersonData' -> 'keysIssue' ->> 'actDate', json_data -> 'Person' -> 'PersonData' ->> 'affairId') json_data -> 'Person' -> 'PersonData' ->> 'UNOM'                  as real_estate_unom,
                                                                                                                                         json_data -> 'Person' -> 'PersonData' -> 'keysIssue' ->> 'actNum' as unom_key_issue_act_number,
                                                                                                                                         to_char(
                                                                                                                                                 (json_data -> 'Person' -> 'PersonData' -> 'keysIssue' ->> 'actDate')::date,
                                                                                                                                                 'DD.MM.YYYY')                                             as unom_key_issue_date
from ssr.documents
where doc_type = 'PERSON'
  AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
ORDER BY json_data -> 'Person' -> 'PersonData' ->> 'UNOM', json_data -> 'Person' -> 'PersonData' -> 'keysIssue' ->> 'actDate', json_data -> 'Person' -> 'PersonData' ->> 'affairId';

[unom_key_return]:
select DISTINCT ON (json_data -> 'Person' -> 'PersonData' ->> 'UNOM', json_data -> 'Person' -> 'PersonData' -> 'releaseFlat' ->> 'actDate', json_data -> 'Person' -> 'PersonData' ->> 'affairId') json_data -> 'Person' -> 'PersonData' ->> 'UNOM'                    as real_estate_unom,
                                                                                                                                           json_data -> 'Person' -> 'PersonData' -> 'releaseFlat' ->> 'actNum' as unom_key_return_act_number,
                                                                                                                                           to_char(
                                                                                                                                                   (json_data -> 'Person' -> 'PersonData' -> 'releaseFlat' ->> 'actDate')::date,
                                                                                                                                                   'DD.MM.YYYY')                                               as unom_key_return_date
from ssr.documents
where doc_type = 'PERSON'
               AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
ORDER BY json_data -> 'Person' -> 'PersonData' ->> 'UNOM', json_data -> 'Person' -> 'PersonData' -> 'releaseFlat' ->> 'actDate', json_data -> 'Person' -> 'PersonData' ->> 'affairId';

[unom_agreements]:
select DISTINCT ON (agreement ->> 'letterId') id                                                  as agreement_person_id,
                json_data -> 'Person' -> 'PersonData' ->> 'UNOM'    as real_estate_unom,
                agreement ->> 'letterId'                            as agreement_letter_id,
                agreement ->> 'dataId'                              as agreement_id,
                to_char((agreement ->> 'date')::date, 'DD.MM.YYYY') as agreement_date
from ssr.documents,
     jsonb_array_elements(
             json_data -> 'Person' -> 'PersonData' -> 'agreements' -> 'agreement') as agreement
where doc_type = 'PERSON'
  AND agreement ->> 'event' in ('1', '3')
  AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
ORDER BY agreement ->> 'letterId', (agreement ->> 'date')::date DESC;

[unom_rejections]:
select DISTINCT ON (rejection ->> 'letterId') id                                                  as rejection_person_id,
                json_data -> 'Person' -> 'PersonData' ->> 'UNOM'    as real_estate_unom,
                rejection ->> 'letterId'                            as rejection_letter_id,
                rejection ->> 'dataId'                              as rejection_id,
                to_char((rejection ->> 'date')::date, 'DD.MM.YYYY') as rejection_date
from ssr.documents,
     jsonb_array_elements(
             json_data -> 'Person' -> 'PersonData' -> 'agreements' -> 'agreement') as rejection
where doc_type = 'PERSON'
  AND rejection ->> 'event' = '2'
  AND COALESCE(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
ORDER BY rejection ->> 'letterId', (rejection ->> 'date')::date DESC;