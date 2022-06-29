package ru.croc.ugd.ssr.service;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.reinform.cdp.search.model.search_input.QuerySearch;
import ru.reinform.cdp.search.model.search_output.SearchResponse;
import ru.reinform.cdp.search.service.SearchRemoteService;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Сервис по работы с SYS_ADDRESSES.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SystemAddressesService {

    private final SearchRemoteService searchRemoteService;

    /**
     * Получить уном из солр по адресу (SYS_ADDRESSES).
     *
     * @param address адрес
     * @return уном
     */
    public BigInteger getSysUnomByAddress(final String address) {
        try {
            final String cleanAddress = cleanupAddress(address);
            final String query = queryFromAddress(address);

            if (query.length() == 0) {
                return null;
            }

            final QuerySearch querySearch = new QuerySearch();

            querySearch.setPage(0);
            querySearch.setPageSize(100);
            querySearch.setTypes(Collections.singletonList("SYS_ADDRESSES_ADDRESS"));

            log.info("getSysUnomByAddress: query: '*{}*', '{}'", query, cleanAddress);

            querySearch.setQuery(
                "fullAddress:(*" + query + "*) AND (isDeletedAddress:false)"
            );

            final SearchResponse searchResponse = searchRemoteService.searchByQuery(querySearch);
            final List<Map<String, Object>> docs = searchResponse.getDocs();

            log.info("getSysUnomByAddress: result size = {}", ofNullable(docs).map(List::size).orElse(0));

            final List<String> unomList = ofNullable(docs)
                .map(List::stream)
                .orElse(Stream.empty())
                .filter(solrDocument -> filterAddress(solrDocument, cleanAddress))
                .map(solrDocument -> solrDocument.get("unomAddress").toString())
                .distinct()
                .collect(Collectors.toList());

            log.info("getSysUnomByAddress: filtered size = {}", unomList.size());

            if (unomList.size() == 1) {
                return new BigInteger(unomList.get(0));
            }
        } catch (Exception exception) {
            log.error("Unable to getSysUnomByAddress by address {}", address, exception);
        }
        return null;
    }

    private boolean filterAddress(final Map<String, Object> solrDocument, final String address) {
        final String unom = solrDocument.get("unomAddress").toString();
        final String foundAddress = solrDocument.get("fullAddress").toString();
        final String cleanAddress = cleanupAddress(foundAddress);

        final boolean result = nonNull(unom) && Objects.equals(address, cleanAddress);

        log.info("getSysUnomByAddress: filterAddress: '{}', '{}', '{}', {}", unom, foundAddress, cleanAddress, result);

        return result;
    }

    private static String[] tokensFromAddress(final String address) {
        return address
            .toLowerCase()
            .replaceFirst(".*москва", "")
            .replaceFirst("(( кв\\..+)|( квартира.+))", "")
            .replaceAll("[^а-яё0-9 ]", " ")
            .trim()
            .split(" +");
    }

    private static String standardToken(final String token) {
        return token
            .replaceFirst("^бульвар$", "бульв")
            .replaceFirst("^(влад|владение|влд)$", "вл")
            .replaceFirst("^город$", "г")
            .replaceFirst("^дом$", "д")
            .replaceFirst("^квартал$", "кв")
            .replaceFirst("^(корп|корпус)$", "к")
            .replaceFirst("^переулок$", "пер")
            .replaceFirst("^площадь$", "пл")
            .replaceFirst("^проезд$", "пр")
            .replaceFirst("^проспект$", "просп")
            .replaceFirst("^стр$", "с")
            .replaceFirst("^(ул|улица)$", "у")
            .replaceFirst("^шоссе$", "ш");
    }

    private static String queryFromAddress(final String address) {
        return Arrays.stream(tokensFromAddress(address))
            .filter(t -> !t.matches(
                "^((аллея)|(бульв)|(бульвар)|(вл)|(влад)|(владение)|(влд)"
                    + "|(г)|(город)|(д)|(дом)|(к)|(кв)|(квартал)|(корп)|(корпус)|(мкр)|(наб)"
                    + "|(пер)|(переулок)|(пл)|(площадь)|(пр)|(проезд)|(просп)|(проспект)|(район)|(с)|(стр)"
                    + "|(у)|(ул)|(улица)|(ш)|(шоссе))$"))
            .collect(Collectors.joining("* AND *"));
    }

    private static String cleanupAddress(final String address) {
        return Arrays.stream(tokensFromAddress(address))
            .map(SystemAddressesService::standardToken)
            .collect(Collectors.joining(" "));
    }
}
