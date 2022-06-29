package ru.croc.ugd.ssr.service.feedback;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.document.SurveyDocumentService;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultSurveyService implements SurveyService {

    private final SurveyDocumentService surveyDocumentService;
}
