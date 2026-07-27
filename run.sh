#!/bin/bash
# ==========================================
# Book Social Network - Dev Runner
# Loads .env file and starts Spring Boot
# ==========================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$SCRIPT_DIR/.env"

if [ -f "$ENV_FILE" ]; then
    echo "Loading environment from $ENV_FILE"
    set -a
    source "$ENV_FILE"
    set +a
else
    echo "WARNING: .env file not found at $ENV_FILE"
    echo "Copy .env.example to .env and fill in your values"
    exit 1
fi

echo "Starting Book Social Network API..."
cd "$SCRIPT_DIR/book-network"
./mvnw spring-boot:run
