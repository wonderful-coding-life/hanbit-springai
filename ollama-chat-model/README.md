### ChatModel
- 생각보다 ollama에 tool을 지원하는 모델이 별로 없다. gpt-oss:20b Tool 동작하는것 확인했으나 좀 느리다

### EmbeddingModel
- 대부분의 모델이 임베딩을 지원한다.
- ChatModel과 EmbeddingModel은 각각 별도로 사용할 수 있다.

### Vector Database
- PGVector --> 1536 차원의 벡터만 인덱싱 가능하다
- MariaDB --> 꽤 큰 차원도 인덱싱 가능하다
