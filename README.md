# Spring AI [![build status](https://github.com/hibuz/spring-ai-skeleton/actions/workflows/gradle.yml/badge.svg)](https://github.com/hibuz/spring-ai-skeleton/actions/workflows/gradle.yml)

The Spring AI skeleton project provides a Spring-friendly API and abstractions for developing AI applications.

Its goal is to apply to the AI domain Spring ecosystem design principles such as portability and modular design and promote using POJOs as the building blocks of an application to the AI domain.

![spring-ai-integration-diagram-3](https://docs.spring.io/spring-ai/reference/_images/spring-ai-integration-diagram-3.svg)

> At its core, Spring AI addresses the fundamental challenge of AI integration: Connecting your enterprise __Data__ and __APIs__ with the __AI Models__.

For further information go to our [Spring AI Reference Documentation](https://docs.spring.io/spring-ai/reference/).

The project draws inspiration from notable Python projects, such as [LangChain](https://docs.langchain.com/docs/) and [LlamaIndex](https://gpt-index.readthedocs.io/en/latest/getting_started/concepts.html), but Spring AI is not a direct port of those projects. The project was founded with the belief that the next wave of Generative AI applications will not be only for Python developers but will be ubiquitous across many programming languages.

This is a high level feature overview.
You can find more details in the [Reference Documentation](https://docs.spring.io/spring-ai/reference/)

* Support for all major [AI Model providers](https://docs.spring.io/spring-ai/reference/api/index.html) such as Anthropic, OpenAI, Microsoft, Amazon, Google, and Ollama. Supported model types include:
    - [Chat Completion](https://docs.spring.io/spring-ai/reference/api/chatmodel.html)
    - [Embedding](https://docs.spring.io/spring-ai/reference/api/embeddings.html)
    - [Text to Image](https://docs.spring.io/spring-ai/reference/api/imageclient.html)
    - [Audio Transcription](https://docs.spring.io/spring-ai/reference/api/audio/transcriptions.html)
    - [Text to Speech](https://docs.spring.io/spring-ai/reference/api/audio/speech.html)
    - [Moderation](https://docs.spring.io/spring-ai/reference/api/index.html#api/moderation)
* Portable API support across AI providers for both synchronous and streaming API options are supported. Access to [model-specific features](https://docs.spring.io/spring-ai/reference/api/chatmodel.html#_chat_options) is also available.
* [Structured Outputs](https://docs.spring.io/spring-ai/reference/api/structured-output-converter.html) - Mapping of AI Model output to POJOs.
* Support for all major [Vector Database providers](https://docs.spring.io/spring-ai/reference/api/vectordbs.html) such as *Apache Cassandra, Azure Vector Search, Chroma, Milvus, MongoDB Atlas, Neo4j, Oracle, PostgreSQL/PGVector, PineCone, Qdrant, Redis, and Weaviate*.
* Portable API across Vector Store providers, including a novel SQL-like [metadata filter API](https://docs.spring.io/spring-ai/reference/api/vectordbs.html#metadata-filters).
* [Tools/Function Calling](https://docs.spring.io/spring-ai/reference/api/functions.html) - permits the model to request the execution of client-side tools and functions, thereby accessing necessary real-time information as required.
* [Observability](https://docs.spring.io/spring-ai/reference/observability/index.html) - Provides insights into AI-related operations.
* Document injection [ETL framework](https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html) for Data Engineering.
* [AI Model Evaluation](https://docs.spring.io/spring-ai/reference/api/testing.html) - Utilities to help evaluate generated content and protect against hallucinated response.
* [ChatClient API](https://docs.spring.io/spring-ai/reference/api/chatclient.html) - Fluent API for communicating with AI Chat Models, idiomatically similar to the WebClient and RestClient APIs.
* [Advisors API](https://docs.spring.io/spring-ai/reference/api/advisors.html) - Encapsulates recurring Generative AI patterns, transforms data sent to and from Language Models (LLMs), and provides portability across various models and use cases.
* Support for [Chat Conversation Memory](https://docs.spring.io/spring-ai/reference/api/chatclient.html#_chat_memory) and [Retrieval Augmented Generation (RAG)](https://docs.spring.io/spring-ai/reference/api/chatclient.html#_retrieval_augmented_generation).
* Spring Boot Auto Configuration and Starters for all AI Models and Vector Stores - use the [start.spring.io](https://start.spring.io/) to select the Model or Vector-store of choice.


## Getting Started
![OpenAI API Compatibility](https://docs.spring.io/spring-ai/reference/_images/spring-ai-ollama-over-openai.jpg)
Please refer to the [Getting Started Guide](https://docs.spring.io/spring-ai/reference/getting-started.html) for instruction on adding your dependencies.

## Prerequisites - Ollama

- Install Ollama on your local machine : https://ollama.com
- Run `ollama run qwen2.5:1.5b`

or

Docker run (add `--rm` option to run temporary)
```
docker run -it -p 11434:11434 --name ollama ollama/ollama

docker run -it -p 5432:5432 -e POSTGRES_PASSWORD=pass --name postgres pgvector/pgvector:pg18
```

## Quickstart

To run and chat with Llama via spring boot app:

```
// simple run on port 8080
./gradlew bootRun

// run with dev profile and local ollama server
./gradlew bootRun --args='--spring.profiles.active=dev --spring.ai.ollama.base-url=http://localhost:11434/ --spring.datasource.url=jdbc:postgresql://localhost:5432/postgres'
```

## Building for production

### Packaging as jar

To build the final jar and optimize the example application for production, run:

```
./gradlew clean bootJar
```

To ensure everything worked, run:

```
// run in devcontainer
java -jar -Dspring.profiles.active=prod build/libs/*.jar
```

### Packaging as docker

To build the final jar and optimize the example application for production, run:

```
./gradlew docker

docker run --rm -it -p 8888:8080 --name spring-tmp ghcr.io/hibuz/spring-ai
```

To ensure everything worked, run:

```
docker compose up
```

### PGvector

```
docker exec -it postgres psql -U postgres

postgres=# SELECT * FROM pg_extension;
  oid  |  extname  | extowner | extnamespace | extrelocatable | extversion | extconfig | extcondition 
-------+-----------+----------+--------------+----------------+------------+-----------+--------------
 13579 | plpgsql   |       10 |           11 | f              | 1.0        |           | 
 16388 | vector    |       10 |         2200 | t              | 0.8.1      |           | 
 16716 | hstore    |       10 |         2200 | t              | 1.8        |           | 
 16844 | uuid-ossp |       10 |         2200 | t              | 1.1        |           | 
(4 rows)

postgres=# \d vector_store
                     Table "public.vector_store"
  Column   |    Type     | Collation | Nullable |      Default       
-----------+-------------+-----------+----------+--------------------
 id        | uuid        |           | not null | uuid_generate_v4()
 content   | text        |           |          | 
 metadata  | json        |           |          | 
 embedding | vector(768) |           |          | 
Indexes:
    "vector_store_pkey" PRIMARY KEY, btree (id)
    "spring_ai_vector_index" hnsw (embedding vector_cosine_ops)

postgres=# select id, metadata from vector_store;
                  id                  |      metadata      
--------------------------------------+--------------------
 4bc11242-8281-4319-a8e1-1b9dea84306b | {"meta1": "meta1"}
 dfe4063b-c2d6-41dd-a293-901d5b29c24e | {"meta": "meta2"}
 8f1fb602-38cd-4b6b-a71c-d9d004badf91 | {"meta": "meta3"}
(3 rows)

postgres=# exit;
```

## Project Links

* [Swagger](http://localhost:8080/ai/swagger-ui/index.html)
* [Issues](https://github.com/spring-projects/spring-ai/issues)
<!-- * [Discussions](https://github.com/spring-projects/spring-ai/discussions) - Go here if you have a question, suggestion, or feedback! -->


## Educational Resources
### Spring AI blogs:
- [Leverage the Power of 45k, free, Hugging Face Models with Spring AI and Ollama](https://spring.io/blog/2024/10/22/leverage-the-power-of-45k-free-hugging-face-models-with-spring-ai-and-ollama)
- [Supercharging Your AI Applications with Spring AI Advisors](https://spring.io/blog/2024/10/02/supercharging-your-ai-applications-with-spring-ai-advisors)
- [Spring AI with NVIDIA LLM API](https://spring.io/blog/2024/08/20/spring-ai-with-nvidia-llm-api)
- [Spring AI Embraces OpenAI's Structured Outputs: Enhancing JSON Response Reliability](https://spring.io/blog/2024/08/09/spring-ai-embraces-openais-structured-outputs-enhancing-json-response)
- [Spring AI with Groq - a blazingly fast AI inference engine](https://spring.io/blog/2024/07/31/spring-ai-with-groq-a-blazingly-fast-ai-inference-engine)
- [Spring AI with Ollama Tool Support](https://spring.io/blog/2024/07/26/spring-ai-with-ollama-tool-support)
- [Spring AI - Structured Output](https://spring.io/blog/2024/05/09/spring-ai-structured-output)
- [Spring AI - Multimodality - Orbis Sensualium Pictus](https://spring.io/blog/2024/04/19/spring-ai-multimodality-orbis-sensualium-pictus)
- [Function Calling in Java and Spring AI using the latest Mistral AI API](https://spring.io/blog/2024/03/06/function-calling-in-java-and-spring-ai-using-the-latest-mistral-ai-api)
- [AI Meets Spring Petclinic: Implementing an AI Assistant with Spring AI (Part I)](https://spring.io/blog/2024/09/26/ai-meets-spring-petclinic-implementing-an-ai-assistant-with-spring-ai-part-i)
- [AI Meets Spring Petclinic: Implementing an AI Assistant with Spring AI (Part II)](https://spring.io/blog/2024/09/27/ai-meets-spring-petclinic-implementing-an-ai-assistant-with-spring-ai-part)

### Code Examples

- [Flight Booking Assistant](https://github.com/tzolov/playground-flight-booking) - Spring AI powered expert system demo
- [Spring AI Chat Bot CLI](https://github.com/tzolov/spring-ai-cli-chatbot) - chatbot with Retrieval-Augmented Generation (RAG) and conversational memory capabilities
- [Spring AI Samples (community driven) Thomas Vitale](https://github.com/ThomasVitale/llm-apps-java-spring-ai) - Samples showing how to build Java applications powered by Generative AI and Large Language Models (LLMs)
- [spring-ai-examples - (community driven) Craig Walls ](https://github.com/habuma/spring-ai-examples)

### Workshops

- [Spring AI Zero to Hero Workshop](https://github.com/asaikali/spring-ai-zero-to-hero) - Example applications showing how to use Spring AI to build Generative AI projects.
- (outdated) [Workshop material for Azure OpenAI](https://github.com/Azure-Samples/spring-ai-azure-workshop) - contains step-by-step examples from 'hello world' to 'retrieval augmented generation'

### Talks and Videos

Some selected videos.  Search YouTube! for more.

- Spring AI: Seamlessly Integrating AI into Your Enterprise Java Applications (2024 - Devoxx.be)
  <br>[![Spring AI: Seamlessly Integrating AI into Your Enterprise Java Applications](https://img.youtube.com/vi/kfRyY0wsZHM/default.jpg)](https://youtu.be/kfRyY0wsZHM?si=qzIshk0GJqVTyrNm)

- Spring AI Is All You Need (2024 - GOTO Amsterdam)
  <br>[![Watch Spring Tips video](https://img.youtube.com/vi/vuhMti8B5H0/default.jpg)](https://youtu.be/vuhMti8B5H0?si=qhRVLh4-EaUhm9oe)

- Introducing Spring AI (2024 - Spring.IO)
  <br>[![Introducing Spring AI](https://img.youtube.com/vi/umKbaXsiCOY/default.jpg)](https://youtu.be/umKbaXsiCOY?si=FbqCtLIOgbihm6b6)

- Spring AI at Spring.IO Keynotes (2024 - Spring.IO)
  <br>[![Watch](https://img.youtube.com/vi/XUz4LKZx83g/default.jpg)](https://youtu.be/XUz4LKZx83g?t=2940)

- Spring Tips: Spring AI
  <br>[![Watch Spring Tips video](https://img.youtube.com/vi/aNKDoiOUo9M/default.jpg)](https://www.youtube.com/watch?v=aNKDoiOUo9M)
* Overview of Spring AI @ Devoxx 2023
  <br>[![Watch the Devoxx 2023 video](https://img.youtube.com/vi/7OY9fKVxAFQ/default.jpg)](https://www.youtube.com/watch?v=7OY9fKVxAFQ)
* Introducing Spring AI - Add Generative AI to your Spring Applications
  <br>[![Watch the video](https://img.youtube.com/vi/1g_wuincUdU/default.jpg)](https://www.youtube.com/watch?v=1g_wuincUdU)

- Spring AI Introduction: Building AI Applications in Java with Spring
  <br>[![Watch the video](https://img.youtube.com/vi/yyvjT0v3lpY/default.jpg)](https://www.youtube.com/watch?v=yyvjT0v3lpY&ab_channel=DanVega)
