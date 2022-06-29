package ru.croc.ugd.ssr.service;

import static java.util.Optional.ofNullable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.reinform.cdp.search.model.search_input.QuerySearch;
import ru.reinform.cdp.search.model.search_output.SearchResponse;
import ru.reinform.cdp.utils.rest.utils.SendRestUtils;

import java.util.List;
import java.util.stream.Stream;


/**
 * Сервис по работы с catalogs search.
 */
@Service
public class CatalogSearchService {

    private final String catalogSearchUrl;
    private final String addressCore;
    private final SendRestUtils sendJsonRequest;

    CatalogSearchService(
        @Value("${catalogs-search.url}") final String catalogSearchUrl,
        @Value("${catalogs-search.address-core:sys_addresses}") final String addressCore,
        final SendRestUtils sendJsonRequest
    ) {
        this.catalogSearchUrl = catalogSearchUrl;
        this.addressCore = addressCore;
        this.sendJsonRequest = sendJsonRequest;
    }

    /**
     * Получить адрес по уном.
     *
     * @param unom уном
     * @return адрес
     */
    public String fetchAddressByUnom(final String unom) {
        return ofNullable(searchAddressByUnom(unom))
            .map(SearchResponse::getDocs)
            .map(List::stream)
            .orElse(Stream.empty())
            .findFirst()
            .map(solrDocument -> solrDocument.get("fullAddress"))
            .map(String.class::cast)
            .orElse(null);
    }

    private SearchResponse searchAddressByUnom(final String unom) {
        final QuerySearch querySearch = new QuerySearch();

        querySearch.setPage(0);
        querySearch.setPageSize(1);
        querySearch.setQuery("unomAddress:(" + unom + ")");

        return searchAddressByQuery(querySearch);
    }

    private SearchResponse searchAddressByQuery(final QuerySearch querySearch) {
        return this.sendJsonRequest.sendJsonRequest(
            catalogSearchUrl + "/v1/solr/query/" + addressCore,
            HttpMethod.POST,
            querySearch,
            SearchResponse.class
        );
    }

}
