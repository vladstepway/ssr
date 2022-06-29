LIB CONNECT TO 'PostgreSQL_UGD';

NULLASVALUE *;
SET NullValue ='';

[r_defect_report]:
LOAD coalesce(Date(creation_date),'')                      as "Дата получения обращения"
   , area_code
   , address                                               as "Адрес отселяемого дома"
   , flatnumber                                            as "№ кв"
   , room_numbers                                          as "№ комнаты (при наличии)"
   , floor                                                 as "Этаж"
   , ba                                                    as "Строительный адрес зас. дома"
   , aa                                                    as "Присвоенный адрес зас. дома"
   , last_name                                             as "Фамилия"
   , first_name                                            as "Имя"
   , middle_name                                           as "Отчество"
   , resettlement_type                                     as "Вид переселения"
   , general_contractors                                   as "Компания-подрядчик"
   , developers                                            as "Застройщик"
   , Num(delaydatecount)                                   as "Количество переносов срока устранения"
   , mid(coalesce(delaydates,''),12)                       as "Даты переносов"
   , flat                                                  as "№ кв."
   , coalesce(Date(delaydate),'')                          as "Дата завершения работ (планируемая)"
   , coalesce(Date(defectseliminatednotificationdate), '') as "Дата завершения работ (фактическая)"
   , coalesce(Date(accepted_date), '')                     as "Дата проставления согласия граждан"
   , Num(total_defects)                                    as "Общее количество дефектов"
   , Num(cnt)                                              as "Количество"
   , Num(cnt_complited)                                    as "Выполнено"
   , coalesce(Date(fill_date, 'DD.MM.YYYY hh:mm'),'')         as "Дата заведения"
   , coalesce(Date(start_work_date, 'DD.MM.YYYY hh:mm'),'')   as "Дата взятия в работу"
   , coalesce(Date(fill_accept_date, 'DD.MM.YYYY hh:mm'),'')  as "Дата внесения согласия"
   , "Подъезд и входная дверь"
   , "Прихожая"
   , "Ванная"
   , "Совмещенные ванная/туалет"
   , "Туалет"
   , "Кухня"
   , "Комната 1"
   , "Комната 2"
   , "Комната 3"
   , "Комната 4"
   , "Лоджия или балкон"
   , "Окна"
   , "Подоконные доски"
   , "Прочие дефекты"
   , has_consent as "Согласие гражданина";
SQL
  with v_person as (select person.json_data -> 'Person' ->> 'documentID'                 as person_id
                         , person.json_data -> 'Person' -> 'PersonData' ->> 'FIO'        as full_name
                         , person.json_data -> 'Person' -> 'PersonData' ->> 'FirstName'  as first_name
                         , person.json_data -> 'Person' -> 'PersonData' ->> 'LastName'   as last_name
                         , person.json_data -> 'Person' -> 'PersonData' ->> 'MiddleName' as middle_name
                         , person.json_data -> 'Person' -> 'PersonData' ->> 'affairId'   as affair_id
                         , person.json_data -> 'Person' -> 'PersonData' ->> 'flatID'     as flat_id
                         , string_agg(coalesce(roomnumbers, ''), ', ')                   as room_numbers
                      from ssr.documents person
                           left join jsonb_array_elements_text(json_data -> 'Person' -> 'PersonData' -> 'roomNum') as roomnumbers
                           on true
                     where person.doc_type = 'PERSON'
                     group by person_id, person.json_data)
     , v_real_estate as (select documents.json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Address'               as address
                              , flats ->> 'flatID'                                                                  as flatid
                              , flats ->> 'floor'                                                                   as floor
                              , flats ->> 'FlatNumber'                                                              as flatnumber
                              , documents.json_data -> 'RealEstate' -> 'RealEstateData' -> 'MunOkrug_P5' ->> 'name' as district
                              , documents.json_data -> 'RealEstate' -> 'RealEstateData' -> 'ADM_AREA' ->> 'name'    as area
                              , documents.json_data -> 'RealEstate' -> 'RealEstateData' -> 'DISTRICT' ->> 'name'    as real_estate_district
                           from ssr.documents as documents
                              , lateral jsonb_array_elements(documents.json_data -> 'RealEstate' -> 'RealEstateData' -> 'Flats' -> 'Flat') flats
                          where documents.doc_type = 'REAL-ESTATE')
     , v_defects_by_flat_element as (select doc_id                                                                                                                         as doc_id
                                          , personid                                                                                                                       as person_id
                                          , sum(total_defects)                                                                                                             as total_defects
                                          , max(case when innert.flatelement = 'Подъезд и входная дверь' then innert.description else '' end)                              as "Подъезд и входная дверь"
                                          , max(case when innert.flatelement = 'Прихожая' or innert.flatelement = 'Прихожая/Корридор' then innert.description else '' end) as "Прихожая"
                                          , max(case when innert.flatelement = 'Ванная' then innert.description else '' end)                                               as "Ванная"
                                          , max(case when innert.flatelement = 'Совмещенные ванная/туалет' then innert.description else '' end)                            as "Совмещенные ванная/туалет"
                                          , max(case when innert.flatelement = 'Туалет' then innert.description else '' end)                                               as "Туалет"
                                          , max(case when innert.flatelement = 'Кухня' then innert.description else '' end)                                                as "Кухня"
                                          , max(case when innert.flatelement = 'Комната 1' then innert.description else '' end)                                            as "Комната 1"
                                          , max(case when innert.flatelement = 'Комната 2' then innert.description else '' end)                                            as "Комната 2"
                                          , max(case when innert.flatelement = 'Комната 3' then innert.description else '' end)                                            as "Комната 3"
                                          , max(case when innert.flatelement = 'Комната 4' then innert.description else '' end)                                            as "Комната 4"
                                          , max(case when innert.flatelement = 'Лоджия или балкон' or innert.flatelement = 'Лоджия' then innert.description else '' end)   as "Лоджия или балкон"
                                          , max(case when innert.flatelement = 'Окна' then innert.description else '' end)                                                 as "Окна"
                                          , max(case when innert.flatelement = 'Подоконные доски' then innert.description else '' end)                                     as "Подоконные доски"
                                          , string_agg(case
                                                         when ((innert.flatelement <> 'Подъезд и входная дверь' and innert.flatelement <> 'Прихожая' and innert.flatelement <> 'Ванная' and
                                                                innert.flatelement <> 'Совмещенные ванная/туалет' and innert.flatelement <> 'Туалет' and innert.flatelement <> 'Кухня' and
                                                                innert.flatelement <> 'Комната 1' and innert.flatelement <> 'Комната 2' and innert.flatelement <> 'Комната 3' and
                                                                innert.flatelement <> 'Комната 4' and innert.flatelement <> 'Лоджия или балкон' and innert.flatelement <> 'Окна' and
                                                                innert.flatelement <> 'Подоконные доски' and innert.flatelement <> 'Лоджия' and innert.flatelement <> 'Прихожая/Корридор') or
                                                               innert.flatelement is null) and coalesce(innert.description, '') <> '' then innert.description
                                                         else ''
                                                       end, ' ')                                                                                                           as "Прочие дефекты"
                                       from (select id                                                                                       as doc_id
                                                  , agreement -> 'ApartmentDefectData' ->> 'flatElement'                                     as flatelement
                                                  , documents.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'personID' as personid
                                                  , documents.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'unom'     as unom
                                                  , string_agg(agreement -> 'ApartmentDefectData' ->> 'description', '; ')                   as description
                                                  , count(*)                                                                                 as total_defects
                                               from ssr.documents
                                                  , jsonb_array_elements(json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' -> 'apartmentDefects') as agreement
                                              where doc_type = 'APARTMENT-INSPECTION'
                                                and (documents.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'pending' is null or
                                                     documents.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'pending' != 'true')
                                                and (documents.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'isRemoved' is null or
                                                     documents.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'isRemoved' != 'true')
                                                and documents.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' -> 'delayReason' is not null
                                              group by id, flatelement, personid, unom) innert
                                            --WHERE unom is not null
                                      group by doc_id, personid)
     , v_app_insp as (select doc.id                                                                                                               as doc_id
                           , doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'filingDate'                                 as creation_date
                           , doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'acceptedDefectsDate'                        as accepted_date
                           , coalesce(cco.area_code, cip.area_code)                                                                               as area_code
                           , doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'address'                                    as address_to
                           , coalesce(cco.cco_buildingaddress, doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'address') as cco_buildingaddress
                           , cco.cco_assignedaddress
                           , doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'flat'                                       as flat
                           , doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'cipId'                                      as cip_id
                           , doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'personID'                                   as personid
                           , doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'resettlementType'                           as resettlement_type
                           , app_ins_gc.general_contractors                                                                                       as general_contractors
                           , app_ins_dev.developers                                                                                               as developers
                           , doc_dd.delaydate                                                                                                     as delaydate
                           , doc_dd.delaydates
                           , doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'defectsEliminatedNotificationDate'          as defectseliminatednotificationdate
                           , doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'letterId'                                   as letter_id
                           , doc_dd.delaydatecount
                           , h.fill_date                                                                                                          as fill_date
                           , h.start_work_date
                           , h.fill_accept_date
                           , case
                               when doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'hasConsent' is null then ''
                               when doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'hasConsent' = 'true' then 'Согласен'
                               else 'Не согласен'
                             end                                                                                                                  as has_consent
                        from ssr.documents doc
                             left join (select distinct on (doc.id) doc.id
                                                                  , app_ins_gen ->> 'orgFullName' as general_contractors
                                          from ssr.documents doc
                                             , lateral jsonb_array_elements(doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' -> 'generalContractors') app_ins_gen
                                         where doc.doc_type = 'APARTMENT-INSPECTION'
                                           and (doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'pending' is null or
                                                doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'pending' != 'true')
                                           and (doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'isRemoved' is null or
                                                doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'isRemoved' != 'true')
                                           and doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' -> 'delayReason' is not null) as app_ins_gc
                             on doc.id = app_ins_gc.id
                             left join (select distinct on (doc.id) doc.id
                                                                  , app_ins_dev ->> 'orgFullName' as developers
                                          from ssr.documents doc
                                             , lateral jsonb_array_elements(doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' -> 'developers') app_ins_dev
                                         where doc.doc_type = 'APARTMENT-INSPECTION'
                                           and (doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'pending' is null or
                                                doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'pending' != 'true')
                                           and (doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'isRemoved' is null or
                                                doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'isRemoved' != 'true')
                                           and doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' -> 'delayReason' is not null) as app_ins_dev
                             on doc.id = app_ins_dev.id
                             left join (select cip_doc.id                                                       as cip_id
                                             , cip_doc.json_data -> 'Cip' -> 'cipData' ->> 'Address'            as address
                                             , cip_doc.json_data -> 'Cip' -> 'cipData' -> 'District' ->> 'code' as area_code
                                          from ssr.documents cip_doc
                                         where cip_doc.doc_type = 'CIP') as cip
                             on doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'cipId' = cip.cip_id
                             left join (select doc.id
                                             , (array_agg(delayreasons::json ->> 'delayDate'))[1]                                  as delaydate
                                             , count(delayreasons::json ->> 'delayDate') - 1                                       as delaydatecount
                                             , string_agg(to_char((delayreasons::json ->> 'delayDate')::date, 'dd.mm.yyyy'), '; ') as delaydates
                                          from ssr.documents doc
                                             , lateral jsonb_array_elements(doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' -> 'delayReason') as delayreasons
                                         where doc.doc_type = 'APARTMENT-INSPECTION'
                                           and (doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'pending' is null or
                                                doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'pending' != 'true')
                                           and (doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'isRemoved' is null or
                                                doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'isRemoved' != 'true')
                                           and doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' -> 'delayReason' is not null
                                         group by doc.id
                                         order by doc.id) as doc_dd
                             on doc.id = doc_dd.id
                             left join (select json_data -> 'capitalConstructionObject' -> 'capitalConstructionObjectData' ->> 'unom'                                             as cco_unom
                                             , coalesce(json_data -> 'capitalConstructionObject' -> 'capitalConstructionObjectData' ->> 'buildingAddressString',
                                                        json_data -> 'capitalConstructionObject' -> 'capitalConstructionObjectData' -> 'buildingAddress' ->> 'addressString', '') as cco_buildingaddress
                                             , coalesce(json_data -> 'capitalConstructionObject' -> 'capitalConstructionObjectData' ->> 'assignedAddressString',
                                                        json_data -> 'capitalConstructionObject' -> 'capitalConstructionObjectData' -> 'assignedAddress' ->> 'addressString', '') as cco_assignedaddress
                                             , areas                                                                                                                              as area_code
                                          from ps.documents
                                               left join jsonb_array_element_text(json_data -> 'capitalConstructionObject' -> 'capitalConstructionObjectData' -> 'districts', 0) as areas
                                               on true
                                         where doc_type = 'CAPITAL-CONSTRUCTION-OBJECT'
                                           and json_data -> 'capitalConstructionObject' -> 'capitalConstructionObjectData' ->> 'unom' is not null) cco
                             on cco.cco_unom = doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'unom'
                             left join (select id_doc
                                             , min(date_edit) as fill_date
                                             , min(case
                                                     when exists(select *
                                                                   from jsonb_array_elements(json_patch) c
                                                                  where c ->> 'path' = '/ApartmentInspection/ApartmentInspectionData/delayReason') then date_edit
                                                   end)       as start_work_date
                                             , min(case
                                                     when exists(select *
                                                                   from jsonb_array_elements(json_patch) c
                                                                  where c ->> 'path' = '/ApartmentInspection/ApartmentInspectionData/acceptedDefectsDate') then date_edit
                                                   end)       as fill_accept_date
                                          from ssr.documents_log
                                         group by id_doc) as h
                             on h.id_doc = doc.id
                       where doc.doc_type = 'APARTMENT-INSPECTION'
                         and (doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'pending' is null or
                              doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'pending' != 'true')
                         and (doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'isRemoved' is null or
                              doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'isRemoved' != 'true')
                         and doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' -> 'delayReason' is not null
                       group by doc.id, doc_dd.delaydatecount, doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'filingDate'
                              , doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'acceptedDefectsDate', coalesce(cco.area_code, cip.area_code)
                              , doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'address', cco.cco_buildingaddress, cco.cco_assignedaddress
                              , doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'flat', doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'cipId'
                              , doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'personID'
                              , doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'resettlementType', app_ins_gc.general_contractors, app_ins_dev.developers, doc_dd.delaydate
                              , doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'defectsEliminatedNotificationDate'
                              , doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'letterId', doc.json_data -> 'ApartmentInspection' -> 'ApartmentInspectionData' ->> 'hasConsent'
                              , h.fill_date, h.start_work_date, h.fill_accept_date, doc_dd.delaydates)
select ai.creation_date::date                                                                                                      as creation_date
     , ai.area_code
     , rs.address                                                                                                                  as address
     , rs.flatnumber                                                                                                               as flatnumber
     , prs.room_numbers                                                                                                            as room_numbers
     , rs.floor                                                                                                                    as floor
     , coalesce(prs.last_name, split_part(prs.full_name, ' ', 1))                                                                  as last_name
     , coalesce(prs.first_name, split_part(prs.full_name, ' ', 2))                                                                 as first_name
     , coalesce(prs.middle_name, split_part(prs.full_name, ' ', 3))                                                                as middle_name
     , ai.resettlement_type                                                                                                        as resettlement_type
     , coalesce(ai.cco_buildingaddress, ai.address_to, '')                                                                         as ba
     , coalesce(ai.cco_assignedaddress, '')                                                                                        as aa
     , coalesce(ai.general_contractors, '')                                                                                        as general_contractors
     , coalesce(ai.developers, '')                                                                                                 as developers
     , ai.flat                                                                                                                     as flat
     , ai.delaydate::date                                                                                                          as delaydate
     , ai.defectseliminatednotificationdate::date                                                                                  as defectseliminatednotificationdate
     , ai.accepted_date::date                                                                                                      as accepted_date
     , defects.total_defects                                                                                                       as total_defects
     , 1                                                                                                                           as cnt
     , case when ai.defectseliminatednotificationdate is not null and ai.defectseliminatednotificationdate <> '' then 1 else 0 end as cnt_complited
     , ai.delaydatecount
     , ai.delaydates
     , defects."Подъезд и входная дверь"
     , defects."Прихожая"
     , defects."Ванная"
     , defects."Совмещенные ванная/туалет"
     , defects."Туалет"
     , defects."Кухня"
     , defects."Комната 1"
     , defects."Комната 2"
     , defects."Комната 3"
     , defects."Комната 4"
     , defects."Лоджия или балкон"
     , defects."Окна"
     , defects."Подоконные доски"
     , defects."Прочие дефекты"
     , ai.fill_date
     , ai.start_work_date
     , ai.fill_accept_date
     , ai.has_consent
  from v_app_insp ai
       join v_person prs
       on prs.person_id = ai.personid
       left join v_real_estate rs
       on prs.flat_id = rs.flatid
       left join v_defects_by_flat_element defects
       on ai.doc_id = defects.doc_id;
