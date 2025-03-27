# TDD 1주차 기본과제 제출

## 구현 완료 항목

- 포인트 조회, 포인트 충전 기능 구현 ['a4e45d0'](https://github.com/khs2973/hhplus-tdd/commit/a4e45d0bffd91c774c0dcb508dc896ca56e67e06)
- 사용 내역 조회 및 포인트 사용 기능 구현 ['c3c3ba1'](https://github.com/khs2973/hhplus-tdd/commit/c3c3ba145b54ac56dc76bfb35fce053b63678d6b)
- 4가지 기능에 대한 단위 테스트 구현 ['8509efd'](https://github.com/khs2973/hhplus-tdd/commit/8509efd7f72ba6bb14c90eb77349a02d7016d6db)
- 포인트 사용, 충전 성공 케이스 구현 ['76f1ef7'](https://github.com/khs2973/hhplus-tdd/commit/76f1ef77488c7bafa4c06a882cdccec246264707)

---

### **리뷰 포인트(질문)**

1. 통합 테스트는 단일 API 호출이 아닌 충전 -> 사용 -> 내역 조회까지 이어지는 흐름으로 테스트 하는게 좋은가요?

2. 테스트마다 값들의 상태를 다르게 세팅을 해야했는데 중복이 많아서 @BeforeEach로 공통 설정을 해도 되는지 궁금합니다.
   - 공통 설정으로 BeforeEach로 빼는게 더 좋은건지 아니면 각 테스트 내부에서 명시적으로 세팅을 하는게 좋은걸까요?
---

### **이번주 KPT 회고**

### Keep
- 기능 구현 전에 테스트를 먼저 작성할것
- 예외 케이스들을 테스트로 먼저 생각할것

### Problem
- 테스트 코드를 작성하는게 처음이여서 어색하다보니 기능을 먼저 구현하고 테스트 코드를 작성
   - 테스트 코드를 많이 연습하며 실력을 늘릴것

### Try
- 테스트 코드를 더 이해하고 다양한 정책이나 예외 케이스를 검증해볼것


