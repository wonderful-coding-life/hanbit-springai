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