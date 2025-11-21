### ChatModel
- 다국어 지원 또는 한국어 지원 잘 되는 모델을 사용하는 것이 좋다.
- 스프링 AI와 잘 맞는 Tool 지원 모델은 GPT 계열, Ollama에 gpt-oss:20b Tool 동작하는것 확인했으나 좀 느리다.
- Too이 필요 없다면 여러가지 선택지가 있다. 예: exaone3.5

### EmbeddingModel
- ChatModel과 EmbeddingModel은 각각 별도로 사용할 수 있다.
- 다국어 지원 잘되는 임베딩 모델로 qwen3-embedding을 많이 사용한다.

### Vector Database
- PGVector --> 1536 차원의 벡터만 인덱싱 가능하다
- MariaDB --> 꽤 큰 차원도 인덱싱 가능하다
