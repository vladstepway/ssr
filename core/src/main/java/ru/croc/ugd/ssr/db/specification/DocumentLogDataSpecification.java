package ru.croc.ugd.ssr.db.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.reinform.cdp.db.model.DocumentLogData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Спецификация для логов.
 */
public class DocumentLogDataSpecification implements Specification<DocumentLogData> {

    private List<SearchCriteria> list;

    /**
     * Конструктор.
     */
    public DocumentLogDataSpecification() {
        list = new ArrayList<>();
    }

    /**
     * Добавляет критерий для дальнейшего формирования предикатов.
     *
     * @param criteria критерий
     */
    public void add(SearchCriteria criteria) {
        list.add(criteria);
    }

    /**
     * Формирует предикат для получения логов.
     *
     * @param root     root
     * @param query    query
     * @param builder  builder
     * @return предикат
     */
    @Override
    public Predicate toPredicate(Root<DocumentLogData> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        //create a new predicate list
        List<Predicate> predicates = new ArrayList<>();

        //add add criteria to predicates
        for (SearchCriteria criteria : list) {
            if (criteria.getOperation().equals(SearchOperation.DATE_TIME_GREATER_THAN_EQUAL)) {
                predicates.add(builder.greaterThanOrEqualTo(
                        root.get(criteria.getKey()), (LocalDateTime)criteria.getValue()));
            } else if (criteria.getOperation().equals(SearchOperation.DATE_TIME_LESS_THAN_EQUAL)) {
                predicates.add(builder.lessThanOrEqualTo(
                        root.get(criteria.getKey()), (LocalDateTime)criteria.getValue()));
            } else if (criteria.getOperation().equals(SearchOperation.EQUAL)) {
                predicates.add(builder.equal(
                        root.get(criteria.getKey()), criteria.getValue()));
            } else if (criteria.getOperation().equals(SearchOperation.JSONPATCH_HAS)) {
                String path = (String) criteria.getValue();
                List<String> split = new ArrayList<>(Arrays.asList(path.split("/")));
                split.remove("-");
                split.remove("*");

                List<Predicate> jsonPatchHasPathPredicates = new ArrayList<>();
                for (String s : split) {
                    // предикат, что в jsonPatch есть путь
                    Predicate jsonpatchHasPredicate = builder.isTrue(
                            builder.function(
                                    "jsonpatch_has",
                                    Boolean.class,
                                    root.get(criteria.getKey()),
                                    builder.literal(s)
                            )
                    );
                    jsonPatchHasPathPredicates.add(jsonpatchHasPredicate);
                }
                Predicate jsonpatchHasPredicate = builder.and(jsonPatchHasPathPredicates.toArray(new Predicate[0]));

                // предикат, что jsonPatch is null
                Predicate isNullJsonPatchPredicate = builder.isNull(root.get(criteria.getKey()));

                List<Predicate> jsonnewHasPathPredicates = new ArrayList<>();
                for (String s : split) {
                    // предикат, что в jsonNew содержит в себе путь
                    Predicate jsonnewHasPredicate = builder.isTrue(
                            builder.function(
                                    "jsonpatch_has",
                                    Boolean.class,
                                    root.get("jsonNew"),
                                    builder.literal(s)
                            )
                    );
                    jsonnewHasPathPredicates.add(jsonnewHasPredicate);
                }
                Predicate jsonnewHasPredicate = builder.and(jsonnewHasPathPredicates.toArray(new Predicate[0]));

                // добавляем финальный предикат, что jsonPatch содержит в себе путь
                // ИЛИ jsonPatch is null, а jsonNew содержит в себе путь (для вновь созданных записей)
                predicates.add(
                        builder.or(jsonpatchHasPredicate, builder.and(isNullJsonPatchPredicate, jsonnewHasPredicate))
                );
            } else if (criteria.getOperation().equals(SearchOperation.JSONPATCH_HAS_ARRAY)) {
                if (criteria.getValue() instanceof List) {
                    List<String> paths = (List) criteria.getValue();

                    List<String> croppedPaths = paths.stream()
                            .map(path -> {
                                String[] split = path.split("/");
                                String end = split[split.length - 1];
                                if (!end.equals("-") && !end.equals("*")) {
                                    return end;
                                }
                                return split[split.length - 2];
                            }).collect(Collectors.toList());

                    // предикаты на проверку того, что jsonPatch содержит в себе пути
                    List<Predicate> orPatchPredicates = croppedPaths.stream().map(path -> builder.isTrue(
                            builder.function(
                                    "jsonpatch_has",
                                    Boolean.class,
                                    root.get(criteria.getKey()),
                                    builder.literal(path)
                            )
                    )).collect(Collectors.toList());

                    // предикаты на проверку того, что jsonNew содержит в себе путь (не целиком, а только окончание)
                    List<Predicate> orJsonNewPredicates = croppedPaths.stream().map(path -> {
                        return builder.isTrue(
                                builder.function(
                                        "jsonpatch_has",
                                        Boolean.class,
                                        root.get("jsonNew"),
                                        builder.literal(path)
                                )
                        );
                    }).collect(Collectors.toList());

                    // предикат, что jsonPatch is null
                    Predicate isNullJsonPatchPredicate = builder.isNull(root.get(criteria.getKey()));

                    // предикат, что jsonNew содержит в себе один из путей и jsonPatch is null
                    Predicate jsonNewPredicate = builder.and(
                            builder.or(
                                    orJsonNewPredicates.toArray(new Predicate[0])
                            ),
                            isNullJsonPatchPredicate
                    );
                    orPatchPredicates.add(jsonNewPredicate);

                    // добавляем финальный предикат, что jsonPatch содержит в себе один из путей
                    // ИЛИ jsonPatch is null, а jsonNew содержит в себе один из путей (для вновь созданных записей)
                    predicates.add(builder.or(orPatchPredicates.toArray(new Predicate[0])));
                }
            } else if (criteria.getOperation().equals(SearchOperation.IN_STRINGS)) {
                List<String> logins = (List) criteria.getValue();
                if (!logins.isEmpty()) {
                    predicates.add(builder.in(root.get(criteria.getKey())).value(logins));
                } else {
                    predicates.add(builder.disjunction());
                }
            }
        }

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
