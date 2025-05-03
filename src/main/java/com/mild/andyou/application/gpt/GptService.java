package com.mild.andyou.application.gpt;

import com.mild.andyou.application.gpt.dto.GeminiRq;
import com.mild.andyou.application.gpt.dto.GeminiRs;
import com.mild.andyou.application.gpt.dto.RoleType;
import com.mild.andyou.config.filter.UserContext;
import com.mild.andyou.config.filter.UserContextHolder;
import com.mild.andyou.config.properties.GptKeyProperty;
import com.mild.andyou.controller.survey.rqrs.ContentDto;
import com.mild.andyou.controller.survey.rqrs.OptionSaveRq;
import com.mild.andyou.controller.survey.rqrs.SurveySaveRq;
import com.mild.andyou.domain.survey.ContentType;
import com.mild.andyou.domain.survey.Survey;
import com.mild.andyou.domain.survey.SurveyOption;
import com.mild.andyou.domain.survey.SurveyType;
import com.mild.andyou.domain.user.User;
import com.mild.andyou.utils.ObjectMapperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
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

    public SurveySaveRq createSurvey(Long chainOptionId, Survey survey) {
        UserContextHolder.setUserContext(new UserContext(new User(survey.getCreatedBy().getId()), "localhost"));
        String prompt = getCreateSurveyPrompt(chainOptionId, survey);
        GeminiRq rq = new GeminiRq();
        rq.addContents(RoleType.USER, prompt);
        String result = request(rq).replace("```json","").replace("```","");
        SurveySaveRq newSurveyRq = ObjectMapperUtils.getFromJson(result, SurveySaveRq.class);
        newSurveyRq.setChainOptionId(chainOptionId);
        newSurveyRq.setTopic(survey.getTopic());
        newSurveyRq.setType(survey.getType());
        return newSurveyRq;
    }

    private String getCreateSurveyPrompt(Long chainOptionId, Survey survey) {
        StringBuilder sb = new StringBuilder();

        sb.append("# 상황\n");
        sb.append("주어진 '스토리 요약'과 '직전 스토리'를 토대로 앞으로의 내용을 작성해야 함. ");
        sb.append("내용은 소설체 작성, 글의 주인공은 나(글을 읽는 독자)임. ");
        sb.append("내용은 해피앤딩, 세드앤딩, 배드엔딩 중 택1 필수. ");
        sb.append("새로운 시작과 같은 전개는 금지. 결말의 경우 완결섬 필수.\n");
        if(survey.getTotalSummary() != null) {
            sb.append("# 스토리 요약\n");
            sb.append(survey.getTotalSummary()).append("\n");
        }
        sb.append("# 직전 스토리\n");
        sb.append("## 제목\n").append(survey.getTitle()).append("\n");
        sb.append("## 내용\n").append(survey.getDescription()).append("\n");
        sb.append("## 주인공의 행동\n").append(survey.getOption(chainOptionId).get().getText()).append("\n");
        sb.append("# 출력 조건\n");
        sb.append("아래의 형식으로 응답 필수. 이외의 구조는 에러 발생.\n");
        sb.append(ObjectMapperUtils.getToJson(getExampleSurveySaveRq(survey)));
        sb.append("\n");
        sb.append("내용은 최소 500자 분량으로 작성. ");
        sb.append("'*'특수문자는 사용 금지. 예외적으로 가독성을 위해 개행은 '\\n'로 처리.");
        return sb.toString();
    }

    private SurveySaveRq getExampleSurveySaveRq(Survey survey) {
        boolean isFinal = ThreadLocalRandom.current().nextDouble() < 0.8;

        List<OptionSaveRq> options = new ArrayList<>();
        if(isFinal) {
            options.add(new OptionSaveRq(
                    "생성된 선택지(결말의 경우 [끝] 한글자 작성)",
                    new ContentDto(
                            ContentType.NONE,
                            null,
                            null
                    ),
                    null,
                    null
            ));
        }else {
            for(int i=0; i<2; i++) {
                options.add(new OptionSaveRq(
                        "생선된 선택지(클라이맥스)",
                        new ContentDto(
                                ContentType.NONE,
                                null,
                                null
                        ),
                        null,
                        null
                ));
            }
        }
        String v = (isFinal ? "(결말)" : "(클라이맥스)");
        SurveySaveRq surveySaveRq = new SurveySaveRq(
                null,
                null,
                null,
                "생성된 제목",
                "생성된 내용"+v,
                options,
                null,
                "전체 스토리 요약",
                isFinal
        );
        return surveySaveRq;
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
//        final String MODEL = "gemini-2.5-flash-preview-04-17";
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
