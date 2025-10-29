# 什么是LangChain4j？

​		官网地址：https://docs.langchain4j.dev/

​		官方简介：利用 LLM 的强大功能增强你的 Java 应用程序

LangChain4j 的目标是简化 LLM 与 Java 应用程序的集成。

方法如下：

1. **统一的 API：** LLM 提供商（例如 OpenAI 或 Google Vertex AI）和嵌入（向量）存储（例如 Pinecone 或 Milvus）使用专有 API。LangChain4j 提供统一的 API，避免了为每个 API 学习和实现特定的 API。为了尝试不同的 LLM 或嵌入存储，您可以轻松地在它们之间切换，而无需重写代码。LangChain4j 目前支持[20 多个常用的 LLM 提供商](https://docs.langchain4j.dev/integrations/language-models/) 和[30 多个嵌入存储](https://docs.langchain4j.dev/integrations/embedding-stores/)。
2. **全面的工具箱：** 自 2023 年初以来，社区一直在构建大量基于 LLM 的应用程序，识别常见的抽象、模式和技术。LangChain4j 已将这些工具精炼成一个随时可用的工具包。我们的工具箱涵盖了从低级提示模板、聊天内存管理和函数调用到高级模式（例如 Agents 和 RAG）的各种工具。对于每个抽象，我们都提供了一个接口以及基于常见技术的多种随时可用的实现。无论您是构建聊天机器人，还是开发具有从数据提取到检索的完整流程的 RAG，LangChain4j 都能提供丰富的选择。
3. **大量示例：** 这些[示例](https://github.com/langchain4j/langchain4j-examples)展示了如何开始创建各种 LLM 驱动的应用程序，提供灵感并使您能够快速开始构建。

LangChain4j 于 2023 年初在 ChatGPT 热潮中启动开发。我们注意到，众多 Python 和 JavaScript LLM 库和框架缺乏 Java 版本，我们必须解决这个问题！虽然我们的名字里有“LangChain”，但该项目融合了 LangChain、Haystack、LlamaIndex 以及更广泛社区的理念和概念，并融入了我们自己的创新元素。

我们积极关注社区发展，旨在快速整合新技术和集成，确保您始终保持最新状态。该库正在积极开发中。虽然部分功能仍在开发中，但核心功能已基本到位，让您可以立即开始构建基于 LLM 的应用程序！

为了更容易集成，LangChain4j 还包括与 [Quarkus](https://docs.langchain4j.dev/tutorials/quarkus-integration)、[Spring Boot](https://docs.langchain4j.dev/tutorials/spring-boot-integration)、[Helidon](https://docs.langchain4j.dev/tutorials/helidon-integration)和[Micronaut 的集成](https://docs.langchain4j.dev/tutorials/micronaut-integration)

​	**优势**：

- **与 LLM 和 Vector Stores 轻松交互**  

所有主要的商业和开源 LLM 和 Vector Stores 都可以通过统一的 API 轻松访问，使您能够构建聊天机器人、助手等。

- **专为 Java 量身定制**  

得益于 Quarkus、Spring Boot 和 Helidon 的集成，LLM 可以顺利集成到您的 Java 应用程序中。LLM 与 Java 之间是双向集成：您可以从 Java 调用 LLM，并允许 LLM 调用您的 Java 代码。

- **代理、工具、RAG**

我们丰富的工具箱为常见的 LLM 操作提供了广泛的工具，从低级提示模板、聊天内存管理和输出解析到代理和 RAG 等高级模式。

# 总结

​		LangChain4j可以让我们使用Java更好的集成LLM大模型，项目融合了 LangChain、Haystack、LlamaIndex 以及更广泛社区的理念和概念，工具箱涵盖了从低级提示模板、聊天内存管理和函数调用到高级模式（例如 Agents 和 RAG）的各种工具。



