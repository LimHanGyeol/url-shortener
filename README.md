# URL Shortener (URL 단축기)
URL Shortener는 긴 URL을 짧은 링크로 변환하고, 짧은 링크를 원래의 긴 URL로 리다이렉트하는 기능을 제공합니다.

사용 기술
- Kotlin, Gradle, SpringBoot, JUnit5, Mockk, MySQL, Redis, JPA, QueryDLS

## 주요 기능
1. URL 단축: 주어진 원본 URL을 짧은 링크로 변환합니다.
2. URL 리다이렉트: 단축된 URL을 원본 URL로 리다이렉트합니다.

## 설계 및 아키텍처
### 1. URL 단축
#### Sequence Diagram
```mermaid
sequenceDiagram
actor user as Client
participant shortener as Shortener
participant redis as Redis
participant rdbms as RDBMS

user ->> shortener: 원본 URL 전송
alt Cache에 (Key: 원본 URL, Value: ShortURL)이 있을 경우
    shortener->>redis: Key: 원본 URL 조회
    redis-->>shortener: Value: ShortURL 반환
    shortener-->>user: Cache된 ShortURL 응답

else Cache에 (Key: 원본 URL, Value: ShortURL)이 없을 경우
    alt DB에 원본 URL이 있을 경우
        shortener->>rdbms: 원본 URL 조회
        rdbms-->>shortener: 저장된 ShortURL Entity 반환

    else DB에 원본 URL이 없을 경우
        shortener->>rdbms: ShortURL 생성 후 DB 저장
        rdbms-->>shortener: 생성된 ShortURL Entity 반환
    end
    shortener->>redis: DB에서 생성/조회한 ShortURL Cache 저장
    shortener-->>user: ShortURL 응답
end
```
- URL 단축 길이 8자 이내로 생성 
- 동일한 URL 단축에 대한 요청은 동일한 ShortURL로 응답
- URL 단축 시 Cache 이용

### 2. URL 리다이렉트
#### Sequence Diagram
```mermaid
sequenceDiagram
actor user as Client
participant shortener as Shortener
participant redis as Redis
participant rdbms as RDBMS

user->>shortener: ShortURL 입력
alt Cache에 (Key: ShortUrl, Value: 원본 URL)이 있을 경우
    shortener->>redis: Key: ShortURL 조회
    redis-->>shortener: Value: 원본 URL 반환
    shortener-->>user: Cache된 원본 URL 301 Redirect

else Cache에 (Key: ShortUrl, Value: 원본 URL)이 없을 경우
    alt DB에 ShortURL이 있을 경우
        shortener->>rdbms: ShortURL 조회
        rdbms-->>shortener: 저장된 ShortURL Entity 반환
        shortener->>redis: 조회한 Entity의 원본 URL Cache 저장
        shortener-->>user: DB 조회한 원본 URL 301 Redirect

    else DB에 ShortURL이 없을 경우
        shortener->>user: 404 NotFoundException 응답
    end
end
```
- 단축 URL을 입력받으면 원본 URL로 리다이렉트
- URL 리다이렉트 시 Cache 이용
