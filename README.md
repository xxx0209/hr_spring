## 🧪 테스트 실행 방법

### 1. 테스트 환경
- **언어**: Java 17  
- **프레임워크**: Spring Boot  
- **빌드 도구**: Maven  
- **IDE**: IntelliJ IDEA  

### 2. 테스트 실행 방법

#### ✅ 터미널에서 실행
```bash
# 전체 테스트 실행
./mvnw test
```
> `mvnw`는 Maven Wrapper로, 프로젝트에 포함된 Maven 설정을 그대로 사용할 수 있습니다.  
> 만약 `mvnw`가 없다면 아래 명령어를 사용하세요:
```bash
mvn test
```

#### ✅ IntelliJ IDEA에서 실행
1. `src/test/java` 폴더에서 원하는 테스트 클래스 우클릭 → **Run '클래스명'**
2. 전체 테스트 실행: `src/test/java` 폴더 우클릭 → **Run 'All Tests'**
3. 테스트 결과는 하단의 **Run** 또는 **Test Results** 탭에서 확인 가능

### 3. 테스트 관련 정보
- 테스트 프레임워크: **JUnit 5**
- Mock 라이브러리: **Mockito** (필요 시 사용)
- 테스트 클래스는 `src/test/java` 경로에 위치