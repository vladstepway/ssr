package ru.croc.ugd.ssr.service;

import static java.util.Objects.nonNull;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.reinform.cdp.search.model.search_input.QuerySearch;
import ru.reinform.cdp.search.model.search_output.SearchResponse;
import ru.reinform.cdp.search.service.SearchRemoteService;

import java.util.Collections;

/**
 * Сервис по работе с ОН из солр.
 */
@Service
@RequiredArgsConstructor
public class RealEstateSolrService {

    private final SearchRemoteService searchRemoteService;

    /**
     * Получить ОН по адресу (подстроке).
     *
     * @param addrSubstr адрес (подстрока)
     * @param top        кол-во элементов на стринице
     * @return SearchResponse
     */
    public SearchResponse getRealEstatesFromSolrByAddrSubstr(String addrSubstr, Integer top) {
        QuerySearch querySearch = new QuerySearch();

        querySearch.setPage(0);
        querySearch.setPageSize(top);
        querySearch.setSort("ugd_ssr_real_estate_address asc");
        querySearch.setTypes(Collections.singletonList("UGD_SSR_REAL-ESTATE"));
        if (nonNull(addrSubstr)) {
            StringBuilder filter = new StringBuilder();
            for (String str : addrSubstr.split(" ")) {
                filter.append("*").append(str).append("*").append(" AND ");
            }
            String filterQuery = filter.substring(0, filter.length() - 5);
            querySearch.setQuery(
                "ugd_ssr_real_estate_address:(" + filterQuery + ")"
                    + "OR ugd_ssr_real_estate_unom:(" + filterQuery + ")"
            );
        }

        return searchRemoteService.searchByQuery(querySearch);
    }

}
