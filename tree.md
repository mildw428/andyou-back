.
├── build
│   ├── classes
│   │   └── java
│   │       ├── main
│   │       │   └── com
│   │       │       └── mild
│   │       │           └── andyou
│   │       │               ├── AndyouApplication.class
│   │       │               ├── config
│   │       │               │   └── DataSourceConfig.class
│   │       │               └── domain
│   │       │                   ├── comment
│   │       │                   │   ├── Comment.class
│   │       │                   │   └── Comment$CommentBuilder.class
│   │       │                   ├── survey
│   │       │                   │   ├── Survey.class
│   │       │                   │   ├── Survey$SurveyBuilder.class
│   │       │                   │   ├── SurveyOption.class
│   │       │                   │   ├── SurveyOption$SurveyOptionBuilder.class
│   │       │                   │   ├── SurveyResponse.class
│   │       │                   │   └── SurveyResponse$SurveyResponseBuilder.class
│   │       │                   └── user
│   │       │                       ├── User.class
│   │       │                       ├── User$SocialType.class
│   │       │                       └── User$UserBuilder.class
│   │       └── test
│   ├── generated
│   │   └── sources
│   │       ├── annotationProcessor
│   │       │   └── java
│   │       │       └── main
│   │       └── headers
│   │           └── java
│   │               └── main
│   ├── reports
│   │   └── problems
│   │       └── problems-report.html
│   ├── reports 2
│   │   └── problems
│   │       └── problems-report.html
│   ├── resources
│   │   ├── main
│   │   │   ├── application-dv.yaml
│   │   │   └── application.yaml
│   │   └── test
│   └── tmp
│       └── compileJava
│           └── previous-compilation-data.bin
├── build.gradle
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── HELP.md
├── PRD.md
├── settings.gradle
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── mild
│   │   │           └── andyou
│   │   │               ├── AndyouApplication.java
│   │   │               ├── application
│   │   │               │   └── SurveyService.java
│   │   │               ├── config
│   │   │               │   └── DataSourceConfig.java
│   │   │               ├── controller
│   │   │               │   └── rqrs
│   │   │               │       ├── CommentRs.java
│   │   │               │       ├── OptionRs.java
│   │   │               │       └── SurveyRs.java
│   │   │               ├── domain
│   │   │               │   ├── comment
│   │   │               │   │   ├── Comment.java
│   │   │               │   │   └── CommentRepository.java
│   │   │               │   ├── survey
│   │   │               │   │   ├── Survey.java
│   │   │               │   │   ├── SurveyOption.java
│   │   │               │   │   ├── SurveyRepository.java
│   │   │               │   │   └── SurveyResponse.java
│   │   │               │   └── user
│   │   │               │       ├── User.java
│   │   │               │       └── UserRepository.java
│   │   │               └── model
│   │   │                   ├── Comment.java
│   │   │                   ├── Option.java
│   │   │                   └── Survey.java
│   │   └── resources
│   │       ├── application-dv.yaml
│   │       └── application.yaml
│   └── test
│       └── java
│           └── com
│               └── mild
│                   └── andyou
│                       └── AndyouApplicationTests.java
└── tree.md