LIB CONNECT TO 'PostgreSQL_UGD';

NULLASVALUE *;
SET NullValue ='';

[data]:
LOAD unom
   , ao as "АО"
   , address
   , coalesce(Date(resettlement_begin_dt), '') as resettlement_begin_dt
   , flats_cnt
   , resettlement_flats_cnt
   , free_flats_cnt
   , agreement_cnt
   , contract_cnt
   , reject_cnt
   , release_flat_cnt
   , shipping_done_cnt
   , buy_cnt
   , compensation_flats_cnt
   , compensation_agree_cnt
   , compensation_reject_cnt;
SQL
  with flats as (select unom
                      , count(flat_id)                                                                       as flats_cnt
                      , sum(case when status_living = 5 or no_flat = 1 or own_federal = 1 then 1 else 0 end) as free_flats_cnt
                   from (select json_data -> 'Person' -> 'PersonData' ->> 'UNOM'                                                     as unom
                              , json_data -> 'Person' -> 'PersonData' ->> 'flatID'                                                   as flat_id
                              , min(coalesce(json_data -> 'Person' -> 'PersonData' ->> 'statusLiving', '0')::integer)                as status_living
                              , min(coalesce(json_data -> 'Person' -> 'PersonData' -> 'addFlatInfo' ->> 'noFlat', '0')::integer)     as no_flat
                              , min(coalesce(json_data -> 'Person' -> 'PersonData' -> 'addFlatInfo' ->> 'ownFederal', '0')::integer) as own_federal
                           from ssr.documents
                          where doc_type = 'PERSON'
                            and coalesce(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
                            and json_data -> 'Person' -> 'PersonData' ->> 'UNOM' is not null
                            and json_data -> 'Person' -> 'PersonData' ->> 'flatID' is not null
                          group by json_data -> 'Person' -> 'PersonData' ->> 'UNOM', json_data -> 'Person' -> 'PersonData' ->> 'flatID') t
                  group by unom)
     , real_estate as (select json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM'                    as unom
                            , min(json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Address')            as real_estate_address
                            , min(json_data -> 'RealEstate' -> 'RealEstateData' -> 'DISTRICT' ->> 'name') as real_estate_district
                         from ssr.documents
                        where doc_type = 'REAL-ESTATE'
                          and json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' is not null
                        group by unom)
     , unom_cip as (select json_data -> 'Person' -> 'PersonData' ->> 'UNOM' as unom
                         , min(letters ->> 'idCIP')                         as cip_id
                      from ssr.documents
                         , jsonb_array_elements(json_data -> 'Person' -> 'PersonData' -> 'offerLetters' -> 'offerLetter') as letters
                     where doc_type = 'PERSON'
                       and coalesce(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
                       and json_data -> 'Person' -> 'PersonData' ->> 'UNOM' is not null
                       and json_data -> 'Person' -> 'PersonData' ->> 'flatID' is not null
                       and letters ->> 'idCIP' is not null
                     group by json_data -> 'Person' -> 'PersonData' ->> 'UNOM')
     , cip as (select id                                                   as cip_id
                    , json_data -> 'Cip' -> 'cipData' -> 'Area' ->> 'name' as cip_area
                 from ssr.documents
                where doc_type = 'CIP')
     , messages as (select json_data -> 'Person' -> 'PersonData' ->> 'UNOM'                     as unom
                         , min(coalesce(message ->> 'eventDateTime', message ->> 'date')::date) as dt
                      from ssr.documents
                         , jsonb_array_elements(json_data -> 'Person' -> 'PersonData' -> 'sendedMessages' -> 'Message') as message
                     where doc_type = 'PERSON'
                       and coalesce(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
                       and json_data -> 'Person' -> 'PersonData' ->> 'UNOM' is not null
                       and json_data -> 'Person' -> 'PersonData' ->> 'flatID' is not null
                       and coalesce(message ->> 'eventDateTime', message ->> 'date') is not null
                     group by json_data -> 'Person' -> 'PersonData' ->> 'UNOM')
     , agreements as (select unom
                           , count(1) as cnt
                           , min(dt)  as dt
                        from (select json_data -> 'Person' -> 'PersonData' ->> 'UNOM'   as unom
                                   , json_data -> 'Person' -> 'PersonData' ->> 'flatID' as flat_id
                                   , min((agreement ->> 'date')::date)                  as dt
                                from ssr.documents
                                   , jsonb_array_elements(json_data -> 'Person' -> 'PersonData' -> 'agreements' -> 'agreement') as agreement
                               where doc_type = 'PERSON'
                                 and agreement ->> 'event' in ('1', '3')
                                 and coalesce(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
                                 and json_data -> 'Person' -> 'PersonData' ->> 'UNOM' is not null
                                 and json_data -> 'Person' -> 'PersonData' ->> 'flatID' is not null
                               group by json_data -> 'Person' -> 'PersonData' ->> 'UNOM', json_data -> 'Person' -> 'PersonData' ->> 'flatID') t
                       group by unom)
     , rejections as (select unom
                           , count(1) as cnt
                           , min(dt)  as dt
                        from (select json_data -> 'Person' -> 'PersonData' ->> 'UNOM'   as unom
                                   , json_data -> 'Person' -> 'PersonData' ->> 'flatID' as flat_id
                                   , min((rejection ->> 'date')::date)                  as dt
                                from ssr.documents
                                   , jsonb_array_elements(json_data -> 'Person' -> 'PersonData' -> 'agreements' -> 'agreement') as rejection
                               where doc_type = 'PERSON'
                                 and rejection ->> 'event' = '2'
                                 and coalesce(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
                                 and json_data -> 'Person' -> 'PersonData' ->> 'UNOM' is not null
                                 and json_data -> 'Person' -> 'PersonData' ->> 'flatID' is not null
                               group by json_data -> 'Person' -> 'PersonData' ->> 'UNOM', json_data -> 'Person' -> 'PersonData' ->> 'flatID') t
                       group by unom)
     , contracts as (select unom
                          , count(1) as cnt
                          , min(dt)  as dt
                       from (select json_data -> 'Person' -> 'PersonData' ->> 'UNOM'   as unom
                                  , json_data -> 'Person' -> 'PersonData' ->> 'flatID' as flat_id
                                  , min((contract ->> 'msgDateTime')::date)            as dt
                               from ssr.documents
                                  , jsonb_array_elements(json_data -> 'Person' -> 'PersonData' -> 'contracts' -> 'contract') as contract
                              where doc_type = 'PERSON'
                                and contract ->> 'contractStatus' in ('2', '3')
                                and coalesce(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
                                and json_data -> 'Person' -> 'PersonData' ->> 'UNOM' is not null
                                and json_data -> 'Person' -> 'PersonData' ->> 'flatID' is not null
                              group by json_data -> 'Person' -> 'PersonData' ->> 'UNOM', json_data -> 'Person' -> 'PersonData' ->> 'flatID') t
                      group by unom)
     , keys as (select unom
                     , count(1) as cnt
                  from (select json_data -> 'Person' -> 'PersonData' ->> 'UNOM'   as unom
                             , json_data -> 'Person' -> 'PersonData' ->> 'flatID' as flat_id
                          from ssr.documents
                         where doc_type = 'PERSON'
                           and coalesce(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
                           and json_data -> 'Person' -> 'PersonData' ->> 'UNOM' is not null
                           and json_data -> 'Person' -> 'PersonData' ->> 'flatID' is not null
                           and json_data -> 'Person' -> 'PersonData' -> 'releaseFlat' ->> 'actDate' is not null
                         group by json_data -> 'Person' -> 'PersonData' ->> 'UNOM', json_data -> 'Person' -> 'PersonData' ->> 'flatID') t
                 group by unom)
     , shipping as (select unom
                         , count(1) as cnt
                      from (select p.unom
                                 , p.flat_id
                              from (select json_data -> 'Person' -> 'PersonData' ->> 'UNOM'   as unom
                                         , json_data -> 'Person' -> 'PersonData' ->> 'flatID' as flat_id
                                         , id
                                      from ssr.documents
                                     where doc_type = 'PERSON'
                                       and coalesce(json_data -> 'Person' -> 'PersonData' ->> 'isArchive', 'false') = 'false'
                                       and json_data -> 'Person' -> 'PersonData' ->> 'UNOM' is not null
                                       and json_data -> 'Person' -> 'PersonData' ->> 'flatID' is not null) p
                                   join (select json_data -> 'shippingApplication' -> 'shippingApplicationData' -> 'applicant' ->> 'personUid' as person_id
                                              , json_data -> 'shippingApplication' -> 'shippingApplicationData' ->> 'status'                   as status
                                              , (json_data -> 'shippingApplication' -> 'shippingApplicationData' ->> 'shippingDateEnd')::date  as dt
                                           from ssr.documents
                                          where doc_type = 'SHIPPING-APPLICATION'
                                            and json_data -> 'shippingApplication' -> 'shippingApplicationData' -> 'applicant' ->> 'personUid' is not null
                                            and json_data -> 'shippingApplication' -> 'shippingApplicationData' ->> 'status' = 'Услуга оказана') s
                                   on s.person_id = p.id
                             group by p.unom, p.flat_id) t
                     group by unom)
     , resettlement_begin as (select unom
                                   , min(dt) as resettlement_begin_dt
                                from (select unom
                                           , dt
                                        from messages
                                       union all
                                      select unom
                                           , dt
                                        from agreements
                                       union all
                                      select unom
                                           , dt
                                        from rejections
                                       union all
                                      select unom
                                           , dt
                                        from contracts) t
                               group by unom)
select flats.unom                                                                               as unom
     , coalesce(cip.cip_area, real_estate.real_estate_district)                                 as ao
     , real_estate.real_estate_address                                                          as address
     , resettlement_begin.resettlement_begin_dt                                                 as resettlement_begin_dt
     , coalesce(flats.flats_cnt, 0)                                                             as flats_cnt
     , coalesce(flats.flats_cnt, 0) - coalesce(flats.free_flats_cnt, 0) - coalesce(keys.cnt, 0) as resettlement_flats_cnt
     , coalesce(flats.free_flats_cnt, 0) + coalesce(keys.cnt, 0)                                as free_flats_cnt
     , coalesce(agreements.cnt, 0)                                                              as agreement_cnt
     , coalesce(contracts.cnt, 0)                                                               as contract_cnt
     , coalesce(rejections.cnt, 0)                                                              as reject_cnt
     , coalesce(keys.cnt, 0)                                                                    as release_flat_cnt
     , coalesce(shipping.cnt, 0)                                                                as shipping_done_cnt
     , 0                                                                                        as buy_cnt
     , 0                                                                                        as compensation_flats_cnt
     , 0                                                                                        as compensation_agree_cnt
     , 0                                                                                        as compensation_reject_cnt
  from flats
       join real_estate
       on real_estate.unom = flats.unom
       join resettlement_begin
       on resettlement_begin.unom = flats.unom
       left join unom_cip
       on unom_cip.unom = flats.unom
       left join cip
       on cip.cip_id = unom_cip.cip_id
       left join agreements
       on agreements.unom = flats.unom
       left join rejections
       on rejections.unom = flats.unom
       left join contracts
       on contracts.unom = flats.unom
       left join keys
       on keys.unom = flats.unom
       left join shipping
       on shipping.unom = flats.unom
 where resettlement_begin.resettlement_begin_dt is not null;
