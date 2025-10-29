

# 简单集成Ollama实现聊天

## 启动Ollama模型

```bash
ollama run qwen2.5:14b
```

## 引入依赖

引入springboot3.5 以及 spring-boot-starter-webflux，lombok，spring-ai-starter-model-ollama，

```xml
  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.0</version>
    <relativePath/>
  </parent>

	<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
	</dependencyManagement>

	<dependencies>
	   <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			 <groupId>org.springframework.ai</groupId>
			 <artifactId>spring-ai-starter-model-ollama</artifactId>
		</dependency>
	</dependencies>
```

## 编写配置文件

```yaml
spring:
  application:
    name: test-spring-ai
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        model: qwen2.5:14b
      init:
        pull-model-strategy: never
        timeout: 5m
        max-retries: 1
```

## 编写Controller

```java

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    private final OllamaChatModel ollamaChatModel;


    @GetMapping(value = "/ai", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ChatResponse> generation(String userInput) {
        Prompt prompt = new Prompt(new UserMessage(userInput));
        return this.ollamaChatModel.stream(prompt);
    }

}

```

## 编写前端页面

​	放入src/main/resources/static/index.html

```html
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8" />
    <title>AI 流式聊天演示</title>
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/atom-one-dark.min.css" />
    <style>
        /* 深色科技感主题，优化代码块配色 */
        body {
            font-family: "Microsoft YaHei", sans-serif;
            background: linear-gradient(135deg, #0f2027, #203a43, #2c5364);
            margin: 0;
            padding: 20px;
            color: #eee;
            user-select: text;
        }

        h1 {
            text-align: center;
            color: #00ffd5;
            font-weight: 700;
            text-shadow: 0 0 10px #00ffd5aa;
            margin-bottom: 20px;
        }

        #chat {
            border: 1px solid #00ffd5aa;
            border-radius: 15px;
            padding: 20px 25px;
            height: 600px;
            max-width: 900px;
            margin: 0 auto 20px auto;
            overflow-y: auto;
            background: #111d26;
            box-shadow: 0 0 30px #00ffd5aa;
            scroll-behavior: smooth;
        }

        .message {
            border-radius: 20px;
            margin: 12px 0;
            padding: 15px 18px;
            max-width: 85%;
            white-space: pre-wrap; /* 允许换行且保留空格 */
            word-wrap: break-word;
            overflow-wrap: break-word;
            box-shadow: 0 0 8px rgba(0,0,0,0.3);
            position: relative;
            transition: background-color 0.3s ease;
            font-size: 16px;
            line-height: 1.5;
        }

        .user {
            background: linear-gradient(135deg, #1de9b6, #00bfa5);
            float: right;
            clear: both;
            text-align: right;
            color: #002e26;
            box-shadow: 0 0 15px #00bfa5cc;
        }

        .ai {
            background: linear-gradient(135deg, #3a5a98, #25416e);
            float: left;
            clear: both;
            color: #cce3ff;
            box-shadow: 0 0 15px #3a5a98cc;
        }

        pre {
            background: #223344 !important;
            padding: 15px;
            overflow: auto;
            border-radius: 12px;
            position: relative;
            font-size: 14px;
            color: #a3d4ff !important;
            box-shadow: inset 0 0 10px #112233;
            white-space: pre !important; /* 关键修改：使用pre替代pre-wrap */
            word-break: normal; /* 恢复正常单词断行 */
            tab-size: 4; /* 设置制表符宽度为4个空格 */
        }


        code {
            font-family: 'Fira Code', Consolas, monospace;
            font-size: 14px;
            color: #a3d4ff !important;
            white-space: pre; /* 确保code标签也保留空格 */
        }

        #controls {
            max-width: 900px;
            margin: 0 auto;
            display: flex;
            justify-content: center;
            gap: 12px;
        }

        #input {
            flex: 1;
            padding: 14px 18px;
            font-size: 18px;
            border-radius: 10px;
            border: none;
            outline: none;
            background: #203a43;
            color: #e0f7fa;
            box-shadow: inset 0 0 8px #00ffd5aa;
            transition: box-shadow 0.3s ease;
        }

        #input:focus {
            box-shadow: 0 0 12px #00ffd5ff;
        }

        #sendBtn {
            padding: 14px 30px;
            font-size: 18px;
            border: none;
            border-radius: 12px;
            background: linear-gradient(135deg, #00bfa5, #1de9b6);
            color: #00332f;
            cursor: pointer;
            font-weight: 700;
            box-shadow: 0 0 20px #00bfa5cc;
            transition: background 0.3s ease, box-shadow 0.3s ease;
        }

        #sendBtn:hover {
            background: linear-gradient(135deg, #1de9b6, #00bfa5);
            box-shadow: 0 0 30px #00ffd5ff;
        }

        .copy-btn {
            position: absolute;
            top: 10px;
            right: 10px;
            background: #00bfa5dd;
            border: none;
            padding: 6px 12px;
            font-size: 12px;
            font-weight: 600;
            color: #00332f;
            border-radius: 8px;
            cursor: pointer;
            user-select: none;
            transition: background-color 0.25s ease;
            z-index: 10;
        }

        .copy-btn:hover {
            background: #00ffd5ee;
        }

        /* AI消息块右上角复制整段内容按钮 */
        .ai .copy-all-btn {
            top: 8px;
            right: 8px;
            font-size: 13px;
            padding: 4px 10px;
            background: #25416ecc;
            color: #cce3ff;
            border-radius: 12px;
            box-shadow: 0 0 10px #3a5a98cc;
        }
        .ai .copy-all-btn:hover {
            background: #3a5a98ee;
            color: #fff;
        }
    </style>
</head>
<body>
<h1>AI 流式聊天演示</h1>

<div id="chat"></div>

<div id="controls">
    <input id="input" type="text" placeholder="请输入内容" autocomplete="off" autofocus />
    <button id="sendBtn" aria-label="发送消息">发送</button>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/marked/12.0.1/marked.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js"></script>
<script>
    marked.setOptions({
        highlight: function (code, lang) {
            const validLang = hljs.getLanguage(lang) ? lang : 'plaintext';
            return hljs.highlight(code, { language: validLang }).value;
        },
        breaks: true, // 启用换行符转换
        gfm: true, // 启用GitHub风格Markdown
        preserveWhitespace: true // 保留空格（重要设置）
    });

    const chatBox = document.getElementById('chat');
    const input = document.getElementById('input');
    const sendBtn = document.getElementById('sendBtn');
    let eventSource = null;

    function smoothScrollToBottom() {
        chatBox.scrollTo({
            top: chatBox.scrollHeight,
            behavior: 'smooth'
        });
    }

    // 复制文本通用函数
    async function copyText(text, btn) {
        try {
            await navigator.clipboard.writeText(text);
            const original = btn.textContent;
            btn.textContent = '已复制';
            setTimeout(() => btn.textContent = original, 1500);
        } catch (err) {
            alert('复制失败，请手动复制');
        }
    }

    function appendMessage(content, className, isMarkdown = false) {
        const div = document.createElement('div');
        div.className = 'message ' + className;

        // 优化：先处理代码块中的空格保留
        if (isMarkdown) {
            // 预处理：将连续空格转换为HTML实体
            const preprocessedContent = content.replace(/\s{2,}/g, (match) => {
                return match.replace(/ /g, ' &nbsp;');
            });
            div.innerHTML = marked.parse(preprocessedContent);
        } else {
            div.textContent = content;
        }

        chatBox.appendChild(div);
        smoothScrollToBottom();

        if (className === 'ai') {
            const copyAllBtn = document.createElement('button');
            copyAllBtn.textContent = '复制全部';
            copyAllBtn.className = 'copy-btn copy-all-btn';
            copyAllBtn.title = '复制整段响应内容';
            copyAllBtn.onclick = () => {
                copyText(div.innerText, copyAllBtn);
            };
            div.appendChild(copyAllBtn);
        }

        // 给每个pre代码块添加复制按钮，绑定到pre元素，复制pre内所有代码文本
        div.querySelectorAll('pre').forEach(pre => {
            const btn = document.createElement('button');
            btn.textContent = '复制';
            btn.className = 'copy-btn';
            btn.title = '复制代码块内容';
            btn.onclick = () => {
                copyText(pre.textContent, btn);
            };
            pre.style.position = 'relative';
            pre.appendChild(btn);
        });

        return div;
    }

    function startStream(userInput) {
        if (eventSource) {
            eventSource.close();
        }

        appendMessage(userInput, 'user');

        const url = `/api/user/ai?userInput=${encodeURIComponent(userInput)}`;
        eventSource = new EventSource(url);

        let fullText = '';
        const aiDiv = appendMessage('', 'ai', true);

        eventSource.onmessage = (event) => {
            try {
                const data = JSON.parse(event.data);
                // 你想要展示的文本位置，举例取result.output.text
                const newText = data?.result?.output?.text || '';

                fullText += newText;

                aiDiv.innerHTML = marked.parse(fullText);
                hljs.highlightAll();
                smoothScrollToBottom();

                console.log("解析后文本:", newText);

                // 判断 finishReason 是否为 "stop"
                if (data?.result?.metadata?.finishReason === "stop") {
                    eventSource.close();
                    console.log('检测到 finishReason=stop，关闭流');
                }
            } catch (e) {
                console.error('解析 JSON 出错:', e, '原始数据:', event.data);
            }

        };

        eventSource.onerror = (err) => {
            console.error('EventSource 错误或关闭', err);
            eventSource.close();
        };
    }

    sendBtn.addEventListener('click', () => {
        const text = input.value.trim();
        if (text) {
            startStream(text);
            input.value = '';
            input.focus();
        }
    });

    input.addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendBtn.click();
        }
    });
</script>
</body>
</html>
```

## 访问页面

 直接访问   http://localhost:8080/index.html   即可