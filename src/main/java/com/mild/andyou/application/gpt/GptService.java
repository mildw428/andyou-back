package com.mild.andyou.application.gpt;

import com.mild.andyou.application.gpt.dto.GeminiContent;
import com.mild.andyou.application.gpt.dto.GeminiRq;
import com.mild.andyou.application.gpt.dto.GeminiRs;
import com.mild.andyou.application.gpt.dto.RoleType;
import com.mild.andyou.config.properties.GptKeyProperty;
import com.mild.andyou.domain.survey.Survey;
import com.mild.andyou.domain.survey.SurveyOption;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class GptService {

    private final GptKeyProperty gptKeyProperty;
    private final WebClient webClient = WebClient.create();

    public String createOpinion(Survey survey) {
        StringBuilder sb = new StringBuilder();
        sb.append("다음은 투표 주제와 선택지이다.\n");

        sb.append("주제 :\n");
        sb.append(survey.getTitle()).append("\n");
        sb.append(survey.getDescription()).append("\n");
        sb.append("선택지 :\n");
        List<SurveyOption> options = survey.getOptions();
        for(int i=0; i<options.size(); i++) {
            sb.append(i+1).append(". ");
            sb.append(options.get(i).getText()).append("\n");
        }

        sb.append("100자 이내로 답해야 함.");
        sb.append("markdown을 지원하지 않음.");
        sb.append("음슴체 사용.");
        sb.append("위 선택지 중에서 가장 설득력 있는 하나를 고르고, ");
        sb.append("다른 선택지들과 비교해서 왜 이 선택지를 선택해야하는지 논리적으로 설명해줘.");
        sb.append("답변양식은 [{선택지 번호}] {문구}로 답변해야함.");

        String prompt = sb.toString();
        GeminiRq rq = new GeminiRq();
        rq.addContents(RoleType.USER, prompt);
        return request(rq);
    }

    public String request(GeminiRq rq) {
        GeminiRs rs = webClient.post()
                .uri(getGeminiUri())
                .headers(httpHeaders -> httpHeaders.addAll(getHttpHeaders()))
                .body(Mono.just(rq), GeminiRq.class)
                .retrieve()
                .bodyToMono(GeminiRs.class)
                .block();

        return rs.getCandidates().get(0).getContent().getParts().get(0).getText();
    }

    private static HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return headers;
    }
    private URI getGeminiUri() {
        final String MODEL = "gemini-2.0-flash";
        String url = "generativelanguage.googleapis.com";
        String path = "/v1beta/models/"+MODEL+":generateContent";

        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(url)
                .path(path)
                .queryParam("key", gptKeyProperty.getApiKey())
                .build().toUri();
    }

}
