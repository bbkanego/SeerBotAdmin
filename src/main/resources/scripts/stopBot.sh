#!/bin/bash
echo "======================================Terminate the BOT now======================================"
echo "Terminating bot running at port: $1"
kill -9 $(lsof -ti tcp:$1)
echo "======================================Terminate the BOT complete======================================"