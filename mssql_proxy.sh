#!/bin/bash

set -euo pipefail

enableProxy() {
    echo -e -n "\nsocat proxy is running on $1. Do not terminate until you have finished your job."
    socat tcp4-listen:1433,fork,reuseaddr,bind=$1 tcp4:localhost:1433
}

CONFIRM_PROXY_MSSQL="n"
echo -e -n "Do you want to enable MSSQL proxy on FirewallD?: "
read -n 1 CONFIRM_PROXY_MSSQL

if [[ "${CONFIRM_PROXY_MSSQL}" =~ ^[Yy]$ ]]; then
    enableProxy "$1"
else
    echo -e -n "\nNo action was made."
    exit 0
fi
