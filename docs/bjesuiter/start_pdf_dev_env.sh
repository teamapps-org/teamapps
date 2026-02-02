#!/usr/bin/env bash
set -euo pipefail

PORT=9000
XDG_DATA_HOME=/tmp/bgproc-data
export XDG_DATA_HOME
mkdir -p "${XDG_DATA_HOME}/bgproc/logs"

bgproc stop -n teamapps-frontend >/dev/null 2>&1 || true

if lsof -iTCP:${PORT} -sTCP:LISTEN >/dev/null 2>&1; then
	echo "Port ${PORT} is already in use. Stopping existing listener."
	lsof -tiTCP:${PORT} -sTCP:LISTEN | xargs -r kill
fi

bgproc start -f -n teamapps-frontend -- /bin/zsh -lc "cd /Users/bjesuiter/Develop/teamapps-org/teamapps/teamapps-client && ./start-dev-server.sh 8082 ${PORT}"

for i in {1..60}; do
	if lsof -iTCP:${PORT} -sTCP:LISTEN >/dev/null 2>&1; then
		echo "Dev server is listening on port ${PORT}."
		exit 0
	fi
	sleep 1
done

echo "Timed out waiting for dev server to listen on port ${PORT}."
exit 1
