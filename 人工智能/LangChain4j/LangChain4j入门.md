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



# 核心概念

## 聊天（Chat）

以下是官方文档中的解释

目前 LLM 提供两种 API 类型：

- `LanguageModel`他们的 API 非常简单——接受一个`String`输入并返回一个`String`输出。现在，这个 API 正逐渐被聊天 API（第二种 API 类型）取代。
- `ChatModel`这些模型接受多个`ChatMessage`对象作为输入，并返回单个对象`AiMessage`作为输出。 `ChatMessage`对象通常包含文本，但一些逻辑层模型也支持其他模态（例如，图像、音频等）。此类聊天模型的示例包括 OpenAI`gpt-4o-mini`和 Google 的聊天模型`gemini-1.5-pro`。

`LanguageModel`LangChain4j 将不再扩展对 s 的支持，因此在所有新功能中，我们将使用`ChatModel`API。

`ChatModel`这是用于与 LangChain4j 中的 LLM 交互的底层 API，它提供了最强大的功能和最大的灵活性。此外，还有一个高级 API（[AI 服务](https://docs.langchain4j.dev/tutorials/ai-services)），我们将在介绍完基础知识后再进行讲解。

除了`ChatModel`和 之外`LanguageModel`，LangChain4j 还支持以下类型的模型：

- `EmbeddingModel`- 此模型可以将文本翻译成`Embedding`.
- `ImageModel`- 该模型可以生成和编辑`Image`。
- `ModerationModel`- 该模型可以检查文本是否包含有害内容。
- `ScoringModel`该模型可以根据查询对多段文本进行评分（或排名），本质上是确定每段文本与查询的相关性。这对于[RAG（红](https://docs.langchain4j.dev/tutorials/rag)黄绿蓝绿）算法非常有用。这些内容将在后面详细介绍。

基础类是`ChatModel`API。主要支持的功能如下

​		如您所见，有一个简单的`chat`方法，它接受一个`String`输入并返回一个`String`输出，类似于`LanguageModel`。这只是一个便捷方法，以便您可以快速轻松地进行尝试，而无需将包装`String`在中`UserMessage`。

​		这些方法版本`chat`接受一个或多个`ChatMessage`s 作为输入。 `ChatMessage`是一个表示聊天消息的基本接口。下一节将提供有关聊天消息的更多详细信息。

```
public interface ChatModel {

    String chat(String userMessage);
    
    ChatResponse chat(ChatMessage... messages);

    ChatResponse chat(List<ChatMessage> messages);
}
```

​		`ChatMessage`

目前聊天消息共有四种类型，每种类型对应消息的四种“来源”：

- UserMessage

  这是一条来自用户的消息。用户可以是应用程序的最终用户（人），也可以是应用程序本身。消息内容可以包含：

  - `contents()`：消息的内容。根据 LLM 支持的模态，它可以只包含单个文本（`String)`，或[其他模态](https://docs.langchain4j.dev/tutorials/chat-and-language-models#multimodality)）。
  - `name()`用户名。并非所有模型提供商都支持此功能。
  - `attributes()`：附加属性：这些属性不会发送到模型，而是存储在[`ChatMemory`](https://docs.langchain4j.dev/tutorials/chat-memory)。

- AiMessage

  这是人工智能针对已发送消息生成的回复。它可以包含：

  - `text()`文本内容
  - `thinking()`思考/推理内容
  - `toolExecutionRequests()`：执行工具的请求。我们将[在另一节中](https://docs.langchain4j.dev/tutorials/tools)探讨工具。
  - `attributes()`：其他属性，通常是提供商特有的。

- `ToolExecutionResultMessage`这是结果`ToolExecutionRequest`。

- `SystemMessage`这是系统发送的消息。通常，作为开发人员，您应该定义此消息的内容。一般来说，您会在此处编写指令，说明 LLM 在此对话中的角色、行为方式、回复风格等等。LLM 经过训练，会更加关注`SystemMessage`此类消息，因此请务必谨慎，最好不要让最终用户随意定义或向消息中添加任何内容`SystemMessage`。通常，此消息位于对话的开头。

- `CustomMessage`这是一条自定义消息，可以包含任意属性。此消息类型只能由 `ChatModel`支持它的实现使用（目前仅支持 Ollama）。

既然我们已经了解了所有类型`ChatMessage`，让我们看看如何在对话中将它们结合起来。

`UserMessage`在最简单的场景中，我们可以向该方法提供一个实例`chat`。这类似于该`chat`方法的第一个版本，它接受一个`String`作为输入。主要区别在于，它现在返回的不是一个`String`，而是一个`ChatResponse`。除了之外`AiMessage`，`ChatResponse`还包含`ChatResponseMetadata`。 `ChatResponseMetadata`包含`TokenUsage`，其中包含关于输入（您提供给 generate 方法的所有）包含的标记数量`ChatMessages`、作为输出生成的标记数量（在`AiMessage`）以及总标记数量（输入 + 输出）的统计信息。您需要这些信息来计算对 LLM 的给定调用的成本。然后，`ChatResponseMetadata`还包含`FinishReason`，这是一个枚举，其中包含生成停止的各种原因。通常，`FinishReason.STOP`如果 LLM 决定自行停止生成，则该值为。

创建 的方法有很多种`UserMessage`，具体取决于内容。最简单的方法是`new UserMessage("Hi")`或`UserMessage.from("Hi")`。

### 简单调用

​		引入Maven依赖

```
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai</artifactId>
    <version>1.8.0</version>
</dependency>
```

​		我们尝试使用open-ai兼容的方式直接调用Ollama服务

```
    public static void main(String[] args) {

        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl("http://localhost:11434/v1") // Ollama端点需要带上v1
                .apiKey("")                             // Ollama 本地不需要 key
                .modelName("qwen2.5vl:32b")            // 本地模型名
                .build();

        String answer = model.chat("你是什么模型");
        System.out.println(answer);
    }
```

​		使用UserMessage来定义用户回答

```
        UserMessage userMessage = new UserMessage("你是什么模型,你能干什么");
        ChatResponse chatResponse = model.chat(userMessage);
        System.out.println(chatResponse.aiMessage().text());
        
        
得到如下回答：
我是一个大型语言模型，由阿里巴巴集团旗下的通义实验室自主研发。我的中文名叫通义千问，英文名叫Qwen。我是在阿里云的基础模型体系上进行训练的，能够理解和生成多种语言的文字内容。以下是我的主要功能和特点：

### **我能干什么**
1. **多语言支持**  
   - 我能够处理包括中文、英文、法语、西班牙语、葡萄牙语、俄语、阿拉伯语、越南语、泰语、印尼语等在内的多种语言，支持跨语言的翻译、理解和生成。
```

​		然后我们再尝试使用SystemMessage+UserMessage模拟对话功能，给大模型定义一个系统角色

```
        SystemMessage systemMessage = new SystemMessage("你是BigKang大模型你就叫做BigKang大模型，你可以处理Java的各种问题");
        UserMessage userMessage = new UserMessage("你是什么模型");

        ChatResponse chatResponse = model.chat(systemMessage, userMessage);
        System.out.println(chatResponse.aiMessage().text());
        

得到如下回答（可能每次回答不一样）：
我是BigKang大模型，专长于Java技术领域的问答服务。无论是Java基础语法、框架使用、性能优化、接口设计，还是代码调试、架构设计，甚至是Java相关的编程挑战、项目实战等问题，我都能为你提供帮助和支持。

如果你遇到Java编程中的任何问题，随时可以向我提问！我会尽力为你解答，并给出清晰、详细的解答。希望能在Java技术领域为你提供帮助！😊 🚀

请问，你现在有具体的Java相关问题需要解答吗？请随时告诉我！
```

