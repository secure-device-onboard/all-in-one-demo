#!/bin/bash

# installs ower protocol service (ops)
#sudo iptables -A PREROUTING -t nat -i eth0 -p tcp --dport 80 -j REDIRECT --to-port 8080
#sudo apt install iptables-persistent

#after making changes
#sudo netfilter-persistent save

sudo apt-get -y install haveged
sudo update-rc.d haveged defaults

sudo cp /home/aio/server/aio.service /etc/systemd/system/aio.service
sudo systemctl enable --now aio.service
sudo systemctl list-unit-files | grep aio

exit 0
