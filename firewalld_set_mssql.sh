#!/bin/bash

set -euo pipefail

enableProxy() {
    sudo firewall-cmd --zone=public --add-masquerade --permanent
    sudo firewall-cmd --zone=public --add-forward-port=port=1433:proto=tcp:toport=1433:toaddr=127.0.0.1 --permanent
    sudo firewall-cmd --reload
    echo -e -n "\nEnabled TCP Proxy for MSSQL\n"
}

disableProxy() {
    sudo firewall-cmd --zone=public --remove-forward-port=port=1433:proto=tcp:toport=1433:toaddr=127.0.0.1 --permanent
    sudo firewall-cmd --zone=public --remove-masquerade --permanent
    sudo firewall-cmd --reload
    echo -e -n "\nDisabled TCP Proxy for MSSQL\n"
}

CONFIRM_PROXY_MSSQL="n"
echo -e -n "Do you want to enable MSSQL proxy on FirewallD?: "
read -n 1 CONFIRM_PROXY_MSSQL

if [[ "${CONFIRM_PROXY_MSSQL}" =~ ^[Yy]$ ]]; then
    enableProxy
elif [[ "${CONFIRM_PROXY_MSSQL}" =~ ^[Nn]$ ]]; then
    disableProxy
else
    echo "No action was made."
    exit 0
fi