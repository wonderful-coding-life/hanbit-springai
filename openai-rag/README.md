### 예제 순서
- 임베딩 실습 : OpenAiEmbeddingModelTests.java
- 벡터 데이터베이스 연동 실습 : OpenAiVectorStoreTests.java
- ChatClient + Advisor(RagAdvisor + LogAdvisor + ChatMemoryAdvisor) 연동 실습 : RagController, ChatClientConfig, RagAdvisor, LogAdvisor

### 벡터 데이터베이스에서 유사도 검색하기
- PGVector에서 유사도 검색하는 SQL문 예제
```sql
SELECT content
FROM vector_store
ORDER BY embedding <-> '[0.021563465, ..., -0.0030589562]'
LIMIT 3;
```
- MariaDB 11.7 이상에서 유사도 검색하는 SQL문 예제
```sql
SELECT content
FROM vector_store
ORDER BY VEC_DISTANCE_EUCLIDEAN(embedding,
        VEC_FromText('[0.021563465, ..., -0.0030589562]'))
LIMIT 3;
```