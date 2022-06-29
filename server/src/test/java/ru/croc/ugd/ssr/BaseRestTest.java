package ru.croc.ugd.ssr;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;

@Slf4j
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TestConfig.class })
@WebAppConfiguration
@RequiredArgsConstructor
@ActiveProfiles({ "test", "ugddev", "dev" })
public abstract class BaseRestTest {

    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    public static RequestPostProcessor ivanovBasic() {
        return httpBasic("ivanov_ii", "YfiGfhjkm1");
    }

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
    }

    @SneakyThrows
    protected <T> T mapToObject(MvcResult result, Class<T> clazz) {
        String json = result.getResponse().getContentAsString();
        return mapToObject(json, clazz);
    }

    @SneakyThrows
    protected <T> T mapToObject(String json, Class<T> clazz) {
        return mapper.readValue(json, clazz);
    }

    @SneakyThrows
    protected MvcResult sendPostRequest(String path, String request, ResultMatcher expectedStatus) {
        return mockMvc.perform(post(path).with(ivanovBasic()).contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(expectedStatus)
                .andReturn();
    }

    protected MvcResult sendPostRequest(String path, String request) {
        return sendPostRequest(path, request, anyStatus());
    }

    @SneakyThrows
    protected MvcResult sendPatchRequest(String path, String request, ResultMatcher expectedStatus) {
        return mockMvc.perform(patch(path).with(ivanovBasic()).contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(expectedStatus)
                .andReturn();
    }

    protected MvcResult sendPatchRequest(String path, String request) {
        return sendPatchRequest(path, request, anyStatus());
    }

    @SneakyThrows
    protected MvcResult sendDeleteRequest(String path, ResultMatcher expectedStatus) {
        return mockMvc.perform(delete(path).with(ivanovBasic())).andExpect(expectedStatus).andReturn();
    }

    protected MvcResult sendDeleteRequest(String path) {
        return sendDeleteRequest(path, anyStatus());
    }

    @SneakyThrows
    protected MvcResult sendGetRequest(String path, ResultMatcher expectedStatus) {
        return mockMvc.perform(get(path).with(ivanovBasic())).andExpect(expectedStatus).andReturn();
    }

    protected MvcResult sendGetRequest(String path) {
        return sendGetRequest(path, anyStatus());
    }

    @SneakyThrows
    public JsonNode readTestDataAsJsonNode(String path) {
        String jsonString = readTestData(path);
        return mapper.readTree(jsonString);
    }

    @SneakyThrows
    public String readTestData(String path) {
        URL resource = getClass().getClassLoader().getResource(path);
        File file = new File(resource.getFile());

        return new String(Files.readAllBytes(file.toPath()), Charset.forName("UTF-8"));
    }

    @SneakyThrows
    public void print(MvcResult result) {
        System.out.println("Result:");
        System.out.println("Status: " + result.getResponse().getStatus());
        System.out.println("Error message: " + result.getResponse().getErrorMessage());
        System.out.println("Content type: " + result.getResponse().getContentType());
        String unformattedContent = result.getResponse().getContentAsString();
        try {
            JsonNode jsonNode = mapper.readTree(unformattedContent);
            String formattedContent = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
            System.out.println("Response: " + formattedContent);
        } catch (Exception e) {
            System.out.println("Response: " + unformattedContent);
        }
    }

    @SneakyThrows
    public JsonNode mapToJsonNode(MvcResult result) {
        return mapper.readTree(result.getResponse().getContentAsString());
    }

    private static ResultMatcher anyStatus() {
        return (result) -> {
        };
    }
}
