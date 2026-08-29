#!/usr/bin/env bash

set -euo pipefail

readonly project_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
readonly compose_file="compose.bruno.yaml"
readonly health_url="http://localhost:9002/actuator/health"
readonly health_attempts=60

cleanup() {
  docker compose -f "$compose_file" down --volumes --remove-orphans
}

wait_for_backend() {
  for ((attempt = 1; attempt <= health_attempts; attempt++)); do
    if curl --fail --silent --show-error "$health_url" > /dev/null; then
      return
    fi

    sleep 1
  done

  docker compose -f "$compose_file" logs back
  return 1
}

trap cleanup EXIT

cd "$project_root"

docker compose -f "$compose_file" up --build --detach
wait_for_backend

(
  cd bruno/mdd-api
  "$project_root/node_modules/.bin/bru" run --env Isolated
)
