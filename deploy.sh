#!/bin/bash
# 服务器上更新代码并重建 Docker 服务
# 用法: ./deploy.sh          # 更新全部
#       ./deploy.sh backend  # 仅重建后端

set -e
cd "$(dirname "$0")"

echo ">>> git pull"
git pull --ff-only

if [ -n "$1" ]; then
  echo ">>> docker compose up -d --build $1"
  docker compose up -d --build "$1"
else
  echo ">>> docker compose up -d --build"
  docker compose up -d --build
fi

echo ">>> 服务状态"
docker compose ps

echo ">>> 健康检查"
sleep 3
curl -sf http://127.0.0.1:${APP_PORT:-8080}/api/health && echo "" || echo "health check failed"

echo ">>> 完成"
