LIB CONNECT TO 'PostgreSQL_UGD';

NULLASVALUE *;
SET NullValue ='';

[Адрес]:
LOAD unom
   , cip_area                               as "Округ ЦИП"
   , cip_district                           as "Район ЦИП"
   , cip_address                            as "Адрес ЦИП"
   , real_estate_address                    as "Адрес отселяемого дома"
   , employee_count
   , flat_count
   , flat_with_sso_count
   , coalesce(Date(resettlement_begin), '') as "О начале переселения"
   , coalesce(Date(offer_letter), '')       as "Письма с предложениями"
   , coalesce(Date(sign_agreement), '')     as "О необходимости подписать согласие/отказ"
   , coalesce(Date(signed_agreement), '')   as "О согласиях/отказах"
   , coalesce(Date(defect_removed), '')     as "О завершении работ по дефектам"
   , coalesce(Date(sign_contract), '')      as "Проект договора"
   , coalesce(Date(signed_contract), '')    as "О подписании договора";
SQL
  with msg as (select json_data -> 'Person' -> 'PersonData' ->> 'UNOM'                                                                                                              as unom
                    , id                                                                                                                                                            as person_id
                    , json_data -> 'Person' -> 'PersonData' ->> 'flatID'                                                                                                            as flat_id
                    , coalesce(json_data -> 'Person' -> 'PersonData' ->> 'affairId', id)                                                                                            as affair_id
                    , coalesce(messages ->> 'eventDateTime', messages ->> 'date')::date                                                                                             as event_dt
                    , case lower(replace(messages ->> 'businessType', ' ', ''))
                        when 'уведомлениегражданоначалепереселения' then 1
                        when 'уведомлениегражданописьмеспредложением' then 2
                        when 'уведомлениеонеобходимостиподписатьзаявлениенасогласие/отказпослепросмотраквартиры' then 3
                        when 'уведомлениегражданоподанномсогласии' then 4
                        when 'уведомлениегражданоподанномотказе' then 4
                        when 'уведомлениегражданобустранениидефектов' then 5
                        when 'уведомлениеоготовностипроектадоговорасприглашениемнаподписаниедоговора' then 6
                        when 'уведомлениеоподписанномдоговоре' then 7
                      end                                                                                                                                                           as business_type
                    , case lower(replace(messages ->> 'event', ' ', '')) when 'уведомлениенаправленовелк' then 1 when 'доставленоадресату' then 2 when 'врученоадресату' then 3 end as event
                 from ssr.documents
                    , jsonb_array_elements(json_data -> 'Person' -> 'PersonData' -> 'sendedMessages' -> 'Message') as messages
                where doc_type = 'PERSON'
                  and json_data -> 'Person' -> 'PersonData' ->> 'UNOM' is not null
                  and json_data -> 'Person' -> 'PersonData' ->> 'flatID' is not null
                  and coalesce(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false')
     , msg_dates as (select unom
                          , min(case when business_type = 1 then event_dt end) as resettlement_begin
                          , min(case when business_type = 2 then event_dt end) as offer_letter
                          , min(case when business_type = 3 then event_dt end) as sign_agreement
                          , min(case when business_type = 4 then event_dt end) as signed_agreement
                          , min(case when business_type = 5 then event_dt end) as defect_removed
                          , min(case when business_type = 6 then event_dt end) as sign_contract
                          , min(case when business_type = 7 then event_dt end) as signed_contract
                       from msg
                      group by unom)
     , unom_cip as (select json_data -> 'Person' -> 'PersonData' ->> 'UNOM' as unom
                         , min(letters ->> 'idCIP')                         as cip_id
                      from ssr.documents
                         , jsonb_array_elements(json_data -> 'Person' -> 'PersonData' -> 'offerLetters' -> 'offerLetter') as letters
                     where doc_type = 'PERSON'
                       and json_data -> 'Person' -> 'PersonData' ->> 'UNOM' is not null
                       and json_data -> 'Person' -> 'PersonData' ->> 'flatID' is not null
                       and coalesce(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
                       and letters ->> 'idCIP' is not null
                     group by json_data -> 'Person' -> 'PersonData' ->> 'UNOM')
     , cip as (select id                                                       as cip_id
                    , json_data -> 'Cip' -> 'cipData' -> 'District' ->> 'name' as cip_district
                    , json_data -> 'Cip' -> 'cipData' -> 'Area' ->> 'name'     as cip_area
                    , json_data -> 'Cip' -> 'cipData' ->> 'Address'            as cip_address
                 from ssr.documents
                where doc_type = 'CIP')
     , emp as (select id       as cip_id
                    , count(1) as cip_employee_count
                 from ssr.documents
                    , jsonb_array_elements(json_data -> 'Cip' -> 'cipData' -> 'Employees' -> 'Employee') as employees
                where doc_type = 'CIP'
                group by id)
     , real_estate as (select json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM'                    as unom
                            , min(json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Address')            as real_estate_address
                            , min(json_data -> 'RealEstate' -> 'RealEstateData' -> 'DISTRICT' ->> 'name') as real_estate_area
                         from ssr.documents
                        where doc_type = 'REAL-ESTATE'
                          and json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' is not null
                        group by unom)
     , flats as (select unom
                      , count(1)    as flat_count
                      , sum(is_sso) as flat_with_sso_count
                   from (select rooms.unom
                              , max(sso_id) as is_sso
                           from (select json_data -> 'Person' -> 'PersonData' ->> 'UNOM'                                          as unom
                                      , case when json_data -> 'Person' -> 'PersonData' ->> 'SsoID' is not null then 1 else 0 end as sso_id
                                      , coalesce(json_data -> 'Person' -> 'PersonData' ->> 'affairId', id)                        as affair_id
                                      , json_data -> 'Person' -> 'PersonData' ->> 'flatID'                                        as flat_id
                                   from ssr.documents
                                  where doc_type = 'PERSON'
                                    and json_data -> 'Person' -> 'PersonData' ->> 'UNOM' is not null
                                    and json_data -> 'Person' -> 'PersonData' ->> 'flatID' is not null
                                    and coalesce(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
                                  union all
                                 select json_data -> 'Person' -> 'PersonData' ->> 'UNOM'                   as unom
                                      , 1                                                                  as sso_id
                                      , coalesce(json_data -> 'Person' -> 'PersonData' ->> 'affairId', id) as affair_id
                                      , json_data -> 'Person' -> 'PersonData' ->> 'flatID'                 as flat_id
                                   from ssr.documents
                                      , jsonb_array_elements(json_data -> 'Person' -> 'PersonData' -> 'sendedMessages' -> 'Message') as messages
                                  where doc_type = 'PERSON'
                                    and json_data -> 'Person' -> 'PersonData' ->> 'UNOM' is not null
                                    and json_data -> 'Person' -> 'PersonData' ->> 'flatID' is not null
                                    and coalesce(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false') rooms
                          group by rooms.unom, flat_id, affair_id) t
                  group by unom)
select msg_dates.unom
     , coalesce(cip.cip_area, real_estate.real_estate_area) as cip_area
     , cip.cip_district
     , cip.cip_address
     , real_estate.real_estate_address
     , coalesce(emp.cip_employee_count, 0)                  as employee_count
     , coalesce(flats.flat_count, 0)                        as flat_count
     , coalesce(flats.flat_with_sso_count, 0)               as flat_with_sso_count
     , msg_dates.resettlement_begin
     , msg_dates.offer_letter
     , msg_dates.sign_agreement
     , msg_dates.signed_agreement
     , msg_dates.defect_removed
     , msg_dates.sign_contract
     , msg_dates.signed_contract
  from msg_dates
       join real_estate
       on real_estate.unom = msg_dates.unom
       join flats
       on flats.unom = msg_dates.unom
       left join unom_cip
       on unom_cip.unom = msg_dates.unom
       left join cip
       on cip.cip_id = unom_cip.cip_id
       left join emp
       on emp.cip_id = cip.cip_id;

[Уведомления]:
LOAD *;
SQL
  with msg as (select json_data -> 'Person' -> 'PersonData' ->> 'UNOM'                                                                                                              as unom
                    , id                                                                                                                                                            as person_id
                    , coalesce(json_data -> 'Person' -> 'PersonData' ->> 'affairId', id)                                                                                            as affair_id
                    , json_data -> 'Person' -> 'PersonData' ->> 'flatID'                                                                                                            as flat_id
                    , coalesce(messages ->> 'eventDateTime', messages ->> 'date')::date                                                                                             as event_dt
                    , case lower(replace(messages ->> 'businessType', ' ', ''))
                        when 'уведомлениегражданоначалепереселения' then 1
                        when 'уведомлениегражданописьмеспредложением' then 2
                        when 'уведомлениеонеобходимостиподписатьзаявлениенасогласие/отказпослепросмотраквартиры' then 3
                        when 'уведомлениегражданоподанномсогласии' then 4
                        when 'уведомлениегражданоподанномотказе' then 4
                        when 'уведомлениегражданобустранениидефектов' then 5
                        when 'уведомлениеоготовностипроектадоговорасприглашениемнаподписаниедоговора' then 6
                        when 'уведомлениеоподписанномдоговоре' then 7
                      end                                                                                                                                                           as business_type
                    , case lower(replace(messages ->> 'event', ' ', '')) when 'уведомлениенаправленовелк' then 1 when 'доставленоадресату' then 2 when 'врученоадресату' then 3 end as event
                 from ssr.documents
                    , jsonb_array_elements(json_data -> 'Person' -> 'PersonData' -> 'sendedMessages' -> 'Message') as messages
                where doc_type = 'PERSON'
                  and json_data -> 'Person' -> 'PersonData' ->> 'UNOM' is not null
                  and json_data -> 'Person' -> 'PersonData' ->> 'flatID' is not null
                  and coalesce(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
                  and coalesce(messages ->> 'eventDateTime', messages ->> 'date') is not null)
     , msg_g as (select unom
                      , person_id
                      , affair_id
                      , flat_id
                      , business_type
                      , event
                      , event_dt
                   from (select unom
                              , person_id
                              , affair_id
                              , flat_id
                              , business_type
                              , event
                              , min(event_dt) over (partition by unom, affair_id, flat_id, business_type) as event_dt
                           from (select unom
                                      , person_id
                                      , affair_id
                                      , flat_id
                                      , business_type
                                      , event
                                      , event_dt
                                   from msg
                                  where event in (1, 2, 3)
                                  union all
                                 select unom
                                      , person_id
                                      , affair_id
                                      , flat_id
                                      , business_type
                                      , 1
                                      , event_dt
                                   from msg
                                  where event in (2, 3)
                                  union all
                                 select unom
                                      , person_id
                                      , affair_id
                                      , flat_id
                                      , business_type
                                      , 2
                                      , event_dt
                                   from msg
                                  where event = 3) t) t1
                  group by unom, person_id, affair_id, flat_id, business_type, event, event_dt)
     , msg_t as (select unom
                      , event
                      , event_dt
                      , row_number() over (order by event_dt)              as rn
                      , person_id
                      , affair_id
                      , flat_id
                      , max(case when business_type = 1 then 1 else 0 end) as resettlement_begin
                      , max(case when business_type = 2 then 1 else 0 end) as offer_letter
                      , max(case when business_type = 3 then 1 else 0 end) as sign_agreement
                      , max(case when business_type = 4 then 1 else 0 end) as signed_agreement
                      , max(case when business_type = 5 then 1 else 0 end) as defect_removed
                      , max(case when business_type = 6 then 1 else 0 end) as sign_contract
                      , max(case when business_type = 7 then 1 else 0 end) as signed_contract
                   from msg_g
                  group by unom, event, event_dt, person_id, affair_id, flat_id)
     , msg_d as (select unom
                      , event
                      , event_dt
                      , rn
                      , person_id
                      , affair_id
                      , flat_id
                      , resettlement_begin
                      , offer_letter
                      , sign_agreement
                      , signed_agreement
                      , defect_removed
                      , sign_contract
                      , signed_contract
                      , min(rn) over (partition by unom, event, resettlement_begin, person_id) as resettlement_begin_person
                      , min(rn) over (partition by unom, event, resettlement_begin, affair_id) as resettlement_begin_affair
                      , min(rn) over (partition by unom, event, resettlement_begin, flat_id)   as resettlement_begin_flat
                      , min(rn) over (partition by unom, event, offer_letter, person_id)       as offer_letter_person
                      , min(rn) over (partition by unom, event, offer_letter, affair_id)       as offer_letter_affair
                      , min(rn) over (partition by unom, event, offer_letter, flat_id)         as offer_letter_flat
                      , min(rn) over (partition by unom, event, sign_agreement, person_id)     as sign_agreement_person
                      , min(rn) over (partition by unom, event, sign_agreement, affair_id)     as sign_agreement_affair
                      , min(rn) over (partition by unom, event, sign_agreement, flat_id)       as sign_agreement_flat
                      , min(rn) over (partition by unom, event, signed_agreement, person_id)   as signed_agreement_person
                      , min(rn) over (partition by unom, event, signed_agreement, affair_id)   as signed_agreement_affair
                      , min(rn) over (partition by unom, event, signed_agreement, flat_id)     as signed_agreement_flat
                      , min(rn) over (partition by unom, event, defect_removed, person_id)     as defect_removed_person
                      , min(rn) over (partition by unom, event, defect_removed, affair_id)     as defect_removed_affair
                      , min(rn) over (partition by unom, event, defect_removed, flat_id)       as defect_removed_flat
                      , min(rn) over (partition by unom, event, sign_contract, person_id)      as sign_contract_person
                      , min(rn) over (partition by unom, event, sign_contract, affair_id)      as sign_contract_affair
                      , min(rn) over (partition by unom, event, sign_contract, flat_id)        as sign_contract_flat
                      , min(rn) over (partition by unom, event, signed_contract, person_id)    as signed_contract_person
                      , min(rn) over (partition by unom, event, signed_contract, affair_id)    as signed_contract_affair
                      , min(rn) over (partition by unom, event, signed_contract, flat_id)      as signed_contract_flat
                   from msg_t)
     , msg_a as (select unom
                      , event
                      , event_dt
                      , sum(case when resettlement_begin_person = rn then resettlement_begin else 0 end) as resettlement_begin_person
                      , sum(case when resettlement_begin_affair = rn then resettlement_begin else 0 end) as resettlement_begin_affair
                      , sum(case when resettlement_begin_flat = rn then resettlement_begin else 0 end)   as resettlement_begin_flat
                      , sum(case when offer_letter_person = rn then offer_letter else 0 end)             as offer_letter_person
                      , sum(case when offer_letter_affair = rn then offer_letter else 0 end)             as offer_letter_affair
                      , sum(case when offer_letter_flat = rn then offer_letter else 0 end)               as offer_letter_flat
                      , sum(case when sign_agreement_person = rn then sign_agreement else 0 end)         as sign_agreement_person
                      , sum(case when sign_agreement_affair = rn then sign_agreement else 0 end)         as sign_agreement_affair
                      , sum(case when sign_agreement_flat = rn then sign_agreement else 0 end)           as sign_agreement_flat
                      , sum(case when signed_agreement_person = rn then signed_agreement else 0 end)     as signed_agreement_person
                      , sum(case when signed_agreement_affair = rn then signed_agreement else 0 end)     as signed_agreement_affair
                      , sum(case when signed_agreement_flat = rn then signed_agreement else 0 end)       as signed_agreement_flat
                      , sum(case when defect_removed_person = rn then defect_removed else 0 end)         as defect_removed_person
                      , sum(case when defect_removed_affair = rn then defect_removed else 0 end)         as defect_removed_affair
                      , sum(case when defect_removed_flat = rn then defect_removed else 0 end)           as defect_removed_flat
                      , sum(case when sign_contract_person = rn then sign_contract else 0 end)           as sign_contract_person
                      , sum(case when sign_contract_affair = rn then sign_contract else 0 end)           as sign_contract_affair
                      , sum(case when sign_contract_flat = rn then sign_contract else 0 end)             as sign_contract_flat
                      , sum(case when signed_contract_person = rn then signed_contract else 0 end)       as signed_contract_person
                      , sum(case when signed_contract_affair = rn then signed_contract else 0 end)       as signed_contract_affair
                      , sum(case when signed_contract_flat = rn then signed_contract else 0 end)         as signed_contract_flat
                   from msg_d
                  group by unom, event, event_dt)
     , msg_s as (select *
                      ,
      resettlement_begin_person + offer_letter_person + sign_agreement_person + signed_agreement_person + defect_removed_person + sign_contract_person + signed_contract_person                   as total_person
                      , resettlement_begin_affair + offer_letter_affair + sign_agreement_affair + signed_agreement_affair + defect_removed_affair + sign_contract_affair +
                        signed_contract_affair                                                                                                                                                    as total_affair
                      , resettlement_begin_flat + offer_letter_flat + sign_agreement_flat + signed_agreement_flat + defect_removed_flat + sign_contract_flat +
                        signed_contract_flat                                                                                                                                                      as total_flat
                   from msg_a
                  where resettlement_begin_person + offer_letter_person + sign_agreement_person + signed_agreement_person + defect_removed_person + sign_contract_person + signed_contract_person +
                        resettlement_begin_affair + offer_letter_affair + sign_agreement_affair + signed_agreement_affair + defect_removed_affair + sign_contract_affair + signed_contract_affair +
                        resettlement_begin_flat + offer_letter_flat + sign_agreement_flat + signed_agreement_flat + defect_removed_flat + sign_contract_flat + signed_contract_flat > 0)
     , keys as (select unom
                     , event_dt
                     , generate_series(1, 3) as event
                  from (select distinct unom
                                      , event_dt
                          from msg_s) t)
     , msg_ex as (select keys.unom
                       , keys.event
                       , keys.event_dt
                       , coalesce(resettlement_begin_person, 0) as resettlement_begin_person
                       , coalesce(resettlement_begin_affair, 0) as resettlement_begin_affair
                       , coalesce(resettlement_begin_flat, 0)   as resettlement_begin_flat
                       , coalesce(offer_letter_person, 0)       as offer_letter_person
                       , coalesce(offer_letter_affair, 0)       as offer_letter_affair
                       , coalesce(offer_letter_flat, 0)         as offer_letter_flat
                       , coalesce(sign_agreement_person, 0)     as sign_agreement_person
                       , coalesce(sign_agreement_affair, 0)     as sign_agreement_affair
                       , coalesce(sign_agreement_flat, 0)       as sign_agreement_flat
                       , coalesce(signed_agreement_person, 0)   as signed_agreement_person
                       , coalesce(signed_agreement_affair, 0)   as signed_agreement_affair
                       , coalesce(signed_agreement_flat, 0)     as signed_agreement_flat
                       , coalesce(defect_removed_person, 0)     as defect_removed_person
                       , coalesce(defect_removed_affair, 0)     as defect_removed_affair
                       , coalesce(defect_removed_flat, 0)       as defect_removed_flat
                       , coalesce(sign_contract_person, 0)      as sign_contract_person
                       , coalesce(sign_contract_affair, 0)      as sign_contract_affair
                       , coalesce(sign_contract_flat, 0)        as sign_contract_flat
                       , coalesce(signed_contract_person, 0)    as signed_contract_person
                       , coalesce(signed_contract_affair, 0)    as signed_contract_affair
                       , coalesce(signed_contract_flat, 0)      as signed_contract_flat
                       , coalesce(total_person, 0)              as total_person
                       , coalesce(total_affair, 0)              as total_affair
                       , coalesce(total_flat, 0)                as total_flat
                    from keys
                         left join msg_s
                         on (msg_s.unom = keys.unom and msg_s.event = keys.event and msg_s.event_dt = keys.event_dt))
select unom
     , event_dt                                                                                                                    as "Дата оповещения"
     , event                                                                                                                       as "Статус"
     , resettlement_begin_person - lead(resettlement_begin_person, 1, 0::bigint) over (partition by unom, event_dt order by event) as resettlement_begin_person
     , resettlement_begin_affair - lead(resettlement_begin_affair, 1, 0::bigint) over (partition by unom, event_dt order by event) as resettlement_begin_affair
     , resettlement_begin_flat - lead(resettlement_begin_flat, 1, 0::bigint) over (partition by unom, event_dt order by event)     as resettlement_begin_flat
     , offer_letter_person - lead(offer_letter_person, 1, 0::bigint) over (partition by unom, event_dt order by event)             as offer_letter_person
     , offer_letter_affair - lead(offer_letter_affair, 1, 0::bigint) over (partition by unom, event_dt order by event)             as offer_letter_affair
     , offer_letter_flat - lead(offer_letter_flat, 1, 0::bigint) over (partition by unom, event_dt order by event)                 as offer_letter_flat
     , sign_agreement_person - lead(sign_agreement_person, 1, 0::bigint) over (partition by unom, event_dt order by event)         as sign_agreement_person
     , sign_agreement_affair - lead(sign_agreement_affair, 1, 0::bigint) over (partition by unom, event_dt order by event)         as sign_agreement_affair
     , sign_agreement_flat - lead(sign_agreement_flat, 1, 0::bigint) over (partition by unom, event_dt order by event)             as sign_agreement_flat
     , signed_agreement_person - lead(signed_agreement_person, 1, 0::bigint) over (partition by unom, event_dt order by event)     as signed_agreement_person
     , signed_agreement_affair - lead(signed_agreement_affair, 1, 0::bigint) over (partition by unom, event_dt order by event)     as signed_agreement_affair
     , signed_agreement_flat - lead(signed_agreement_flat, 1, 0::bigint) over (partition by unom, event_dt order by event)         as signed_agreement_flat
     , defect_removed_person - lead(defect_removed_person, 1, 0::bigint) over (partition by unom, event_dt order by event)         as defect_removed_person
     , defect_removed_affair - lead(defect_removed_affair, 1, 0::bigint) over (partition by unom, event_dt order by event)         as defect_removed_affair
     , defect_removed_flat - lead(defect_removed_flat, 1, 0::bigint) over (partition by unom, event_dt order by event)             as defect_removed_flat
     , sign_contract_person - lead(sign_contract_person, 1, 0::bigint) over (partition by unom, event_dt order by event)           as sign_contract_person
     , sign_contract_affair - lead(sign_contract_affair, 1, 0::bigint) over (partition by unom, event_dt order by event)           as sign_contract_affair
     , sign_contract_flat - lead(sign_contract_flat, 1, 0::bigint) over (partition by unom, event_dt order by event)               as sign_contract_flat
     , signed_contract_person - lead(signed_contract_person, 1, 0::bigint) over (partition by unom, event_dt order by event)       as signed_contract_person
     , signed_contract_affair - lead(signed_contract_affair, 1, 0::bigint) over (partition by unom, event_dt order by event)       as signed_contract_affair
     , signed_contract_flat - lead(signed_contract_flat, 1, 0::bigint) over (partition by unom, event_dt order by event)           as signed_contract_flat
     , total_person - lead(total_person, 1, 0::bigint) over (partition by unom, event_dt order by event)                           as total_person
     , total_affair - lead(total_affair, 1, 0::bigint) over (partition by unom, event_dt order by event)                           as total_affair
     , total_flat - lead(total_flat, 1, 0::bigint) over (partition by unom, event_dt order by event)                               as total_flat
     , resettlement_begin_person                                                                                                   as resettlement_begin_person_s
     , resettlement_begin_affair                                                                                                   as resettlement_begin_affair_s
     , resettlement_begin_flat                                                                                                     as resettlement_begin_flat_s
     , offer_letter_person                                                                                                         as offer_letter_person_s
     , offer_letter_affair                                                                                                         as offer_letter_affair_s
     , offer_letter_flat                                                                                                           as offer_letter_flat_s
     , sign_agreement_person                                                                                                       as sign_agreement_person_s
     , sign_agreement_affair                                                                                                       as sign_agreement_affair_s
     , sign_agreement_flat                                                                                                         as sign_agreement_flat_s
     , signed_agreement_person                                                                                                     as signed_agreement_person_s
     , signed_agreement_affair                                                                                                     as signed_agreement_affair_s
     , signed_agreement_flat                                                                                                       as signed_agreement_flat_s
     , defect_removed_person                                                                                                       as defect_removed_person_s
     , defect_removed_affair                                                                                                       as defect_removed_affair_s
     , defect_removed_flat                                                                                                         as defect_removed_flat_s
     , sign_contract_person                                                                                                        as sign_contract_person_s
     , sign_contract_affair                                                                                                        as sign_contract_affair_s
     , sign_contract_flat                                                                                                          as sign_contract_flat_s
     , signed_contract_person                                                                                                      as signed_contract_person_s
     , signed_contract_affair                                                                                                      as signed_contract_affair_s
     , signed_contract_flat                                                                                                        as signed_contract_flat_s
     , total_person                                                                                                                as total_person_s
     , total_affair                                                                                                                as total_affair_s
     , total_flat                                                                                                                  as total_flat_s
     , max(resettlement_begin_person) over (partition by unom, event_dt)                                                           as resettlement_begin_person_t
     , max(resettlement_begin_affair) over (partition by unom, event_dt)                                                           as resettlement_begin_affair_t
     , max(resettlement_begin_flat) over (partition by unom, event_dt)                                                             as resettlement_begin_flat_t
     , max(offer_letter_person) over (partition by unom, event_dt)                                                                 as offer_letter_person_t
     , max(offer_letter_affair) over (partition by unom, event_dt)                                                                 as offer_letter_affair_t
     , max(offer_letter_flat) over (partition by unom, event_dt)                                                                   as offer_letter_flat_t
     , max(sign_agreement_person) over (partition by unom, event_dt)                                                               as sign_agreement_person_t
     , max(sign_agreement_affair) over (partition by unom, event_dt)                                                               as sign_agreement_affair_t
     , max(sign_agreement_flat) over (partition by unom, event_dt)                                                                 as sign_agreement_flat_t
     , max(signed_agreement_person) over (partition by unom, event_dt)                                                             as signed_agreement_person_t
     , max(signed_agreement_affair) over (partition by unom, event_dt)                                                             as signed_agreement_affair_t
     , max(signed_agreement_flat) over (partition by unom, event_dt)                                                               as signed_agreement_flat_t
     , max(defect_removed_person) over (partition by unom, event_dt)                                                               as defect_removed_person_t
     , max(defect_removed_affair) over (partition by unom, event_dt)                                                               as defect_removed_affair_t
     , max(defect_removed_flat) over (partition by unom, event_dt)                                                                 as defect_removed_flat_t
     , max(sign_contract_person) over (partition by unom, event_dt)                                                                as sign_contract_person_t
     , max(sign_contract_affair) over (partition by unom, event_dt)                                                                as sign_contract_affair_t
     , max(sign_contract_flat) over (partition by unom, event_dt)                                                                  as sign_contract_flat_t
     , max(signed_contract_person) over (partition by unom, event_dt)                                                              as signed_contract_person_t
     , max(signed_contract_affair) over (partition by unom, event_dt)                                                              as signed_contract_affair_t
     , max(signed_contract_flat) over (partition by unom, event_dt)                                                                as signed_contract_flat_t
     , max(total_person) over (partition by unom, event_dt)                                                                        as total_person_t
     , max(total_affair) over (partition by unom, event_dt)                                                                        as total_affair_t
     , max(total_flat) over (partition by unom, event_dt)                                                                          as total_flat_t
  from msg_ex;
