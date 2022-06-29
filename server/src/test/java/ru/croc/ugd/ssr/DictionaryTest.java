package ru.croc.ugd.ssr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

@SpringBootTest
public class DictionaryTest {

    private static final String SYSTEM_CODE = "ugd";
    private static final String SUBSYSTEM_CODE = "ssr";
    private static final String BUSINESS_PROCESS_REGISTRY = "BusinessProcessRegistry";
    private static final String MY_TASKS_SETTINGS = "MyTasksSettings";

    private static final List<String> businessProcesses = asList(
            "ugdssr",
            "ugdssr_dashboardRequest",
            "ugdssr_objectResettlementRequest",
            "ugdssr_objectSoonResettlementRequest",
            "ugdssrArm",
            "ugdssrArm_applyRequest",
            "ugdssrArm_issueOfferLetterRequest",
            "ugdssrArm_showApartRequest"
    );

    @Test
    public void checkDictionary() throws IOException {
        PathMatchingResourcePatternResolver s = new PathMatchingResourcePatternResolver(getClass().getClassLoader());
        Resource[] resources = s.getResources("classpath*:dict/common/*/*.json");
        List<Resource> processDicts = new ArrayList<>();
        Stream.of(resources)
                .filter(res -> Objects.requireNonNull(res.getFilename()).toLowerCase().contains(MY_TASKS_SETTINGS.toLowerCase()))
                .findFirst()
                .ifPresent(processDicts::add);
        Stream.of(resources)
                .filter(res -> Objects.requireNonNull(res.getFilename()).toLowerCase().contains(BUSINESS_PROCESS_REGISTRY.toLowerCase()))
                .findFirst()
                .ifPresent(processDicts::add);

        List<Resource> dicts = Stream.of(resources)
                .filter(res -> !Objects.requireNonNull(res.getFilename()).toLowerCase().contains(MY_TASKS_SETTINGS.toLowerCase())
                        && !Objects.requireNonNull(res.getFilename()).toLowerCase().contains(BUSINESS_PROCESS_REGISTRY.toLowerCase())
                )
                .collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();
        Map<String, List<String>> wrongCodes = new HashMap<>();
        for (Resource resource : dicts) {
            Map<?, ?> map = mapper.readValue(resource.getFile(), Map.class);
            ArrayList<Map<?, ?>> elements = (ArrayList<Map<?, ?>>)map.get("elements");
            elements.stream()
                    .flatMap(elems -> ((ArrayList<Map<?, ?>>)elems.get("values")).stream())
                    .filter(val -> val.get("nickAttr").equals("code"))
                    .forEach(element -> {
                        String dictCode = (String) ((Map<?, ?>)map.get("metaDict")).get("nick");
                        String value = (String)element.get("value");
                        if (!value.toLowerCase().startsWith(SYSTEM_CODE + "_" + SUBSYSTEM_CODE)) {
                            if (!wrongCodes.containsKey(dictCode)) {
                                wrongCodes.put(dictCode, new ArrayList<>());
                            }
                            wrongCodes.get(dictCode).add(value);
                        }
                    });
        }

        for (Resource resource : processDicts) {
            Map<?, ?> map = mapper.readValue(resource.getFile(), Map.class);
            ArrayList<Map<?, ?>> elements = (ArrayList<Map<?, ?>>)map.get("elements");
            elements.stream()
                    .flatMap(elems -> ((ArrayList<Map<?, ?>>)elems.get("values")).stream())
                    .filter(val -> val.get("nickAttr").equals("code"))
                    .forEach(element -> {
                        String dictCode = (String) ((Map<?, ?>)map.get("metaDict")).get("nick");
                        String value = (String)element.get("value");
                        if (!businessProcesses.contains(value)) {
                            if (!wrongCodes.containsKey(dictCode)) {
                                wrongCodes.put(dictCode, new ArrayList<>());
                            }
                            wrongCodes.get(dictCode).add(value);
                        }
                    });
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, List<String>> codes : wrongCodes.entrySet()) {
            builder.append("\n");
            builder.append(codes.getKey()).append("\n");
            builder.append(
                    codes.getValue()
                            .stream()
                            .map(str -> "\t"+str)
                            .collect(Collectors.joining("\n"))
            );
        }

        Assert.assertEquals(
                builder.toString(),
                wrongCodes.size(),
                0
        );
    }

}
