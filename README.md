# MicroDustApp
미세먼지 앱 만들기

# 사용 기술
- Kotlin
- MVP
- Koin
- Coroutine
- Flow
- Retrofit
- Room
- ViewPager
- ViewBinding
- Glide

# 💡 Topic
- 어플리케이션에서 설정한 지역의 대기오염 정보를 화면에 표시하는 앱을 만들어 보았다.

# 📝 Summary

- 사실 코로나 데이터를 제공하는 앱이 마땅히 없어서 코로나 앱을 만들어보려 했으나, 관련 정보를 제공하는 API가 부족해 미세먼지 앱으로 방향을 틀었다.
- 대기오염 수치에 따른 표현 방법은 [한국환경공단](https://www.airkorea.or.kr/web/khaiInfo?pMENU_NO=129) 포털을 참조하였다.

# ⭐️ Key Function

- 미세먼지 수치에 따라 앱의 배경색, 아이콘이 변경된다.
- 즐겨 찾는 지역을 추가할 수 있다.

# ⚙️ Architecture
- `MVP`

# 🤔 Learned
- `MVP` 패턴을 학습하고 실제로 앱에 적용해 보기 [블로그 링크](https://tkdgns8234.tistory.com/170)
- `Coroutine` 사용법을 숙지하고 실제 앱에 적용해 보기 [블로그 링크](https://tkdgns8234.tistory.com/178)
- `Flow` 를 이용해 `Room` 변경 내용을 Trace 하도록 구현하기
- **다양한 해상도별 UI 대응하기**
    - dimens 디렉터리를 통해 해상도별 UI 대응해 보기
    - 런타임에 디바이스 크기 계산해서 UI 그리기
- open API 사용하기
    - 카카오 API, 공공 데이터 포털 API (에어코리아)
