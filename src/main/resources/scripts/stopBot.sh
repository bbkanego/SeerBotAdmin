#!/bin/bash
echo "======================================Terminate the BOT now======================================"
kill -9 $(lsof -i tcp:8099 | grep 'java' | awk '$8 == "TCP" { print $2 }')
echo "======================================Terminate the BOT complete======================================"