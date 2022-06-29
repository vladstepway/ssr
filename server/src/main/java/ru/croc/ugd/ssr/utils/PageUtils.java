package ru.croc.ugd.ssr.utils;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PageUtils {

    public static Sort retrieveSort(final String sort) {
        if (StringUtils.isBlank(sort)) {
            return Sort.unsorted();
        } else {
            final List<Sort.Order> orders = Arrays.stream(sort.split(";"))
                .map(sortData -> {
                    final String[] sortItem = sortData.split(",");
                    return "asc".equalsIgnoreCase(sortItem[1])
                        ? Sort.Order.asc(sortItem[0])
                        : Sort.Order.desc(sortItem[0]);
                })
                .collect(Collectors.toList());
            return Sort.by(orders);
        }
    }
}
