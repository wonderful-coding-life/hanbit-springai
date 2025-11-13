- 유사도 검색하는 SQL문 예제
```sql
SELECT content
FROM vector_store
ORDER BY embedding <=> '[0.021563465, ..., -0.0030589562]'
LIMIT 3;
```