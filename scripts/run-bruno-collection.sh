#!/usr/bin/env bash

set -euo pipefail

readonly project_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

cd "$project_root/bruno/mdd-api"
"$project_root/node_modules/.bin/bru" run --env Manual
