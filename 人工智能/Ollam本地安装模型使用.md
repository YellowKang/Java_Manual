# 安装Ollam

Ollam官方仓库地址：https://ollama.com/library

## Mac

```sh
# 下载安装 ollama
brew install ollama

```



# 安装open-webui

```bash
# 创建 docker-compose 文件


# 定义存储的路径 以及OLLAMA服务地址端口
export OPEN_WEBUI_DIR="/Users/bigkang/deploys/open-webui/data"
export OLLAMA_HOST_PORT="192.168.1.111:11434"

cat << EOF > docker-compose.yml
version: '3.8'
services:
  open-webui:
    image: ghcr.io/open-webui/open-webui:main
    ports:
      - "3000:8080"
    environment:
      - OLLAMA_API_BASE_URL=http://$OLLAMA_HOST_PORT
    volumes:
      - $OPEN_WEBUI_DIR:/app/backend/data 
EOF
```



