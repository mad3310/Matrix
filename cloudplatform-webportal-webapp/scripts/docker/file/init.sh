#!/bin/bash

function checkvar(){
  if [ ! $2 ]; then
    echo ERROR: need  $1
    exit 1
  fi
}

IFACE=${IFACE:-peth1}

checkvar IP $IP
checkvar NETMASK $NETMASK
checkvar GATEWAY $GATEWAY

#network
cat > /etc/sysconfig/network-scripts/ifcfg-$IFACE << EOF
DEVICE=$IFACE
ONBOOT=yes
BOOTPROTO=static
IPADDR=$IP
NETMASK=$NETMASK
GATEWAY=$GATEWAY
EOF

ifconfig $IFACE $IP/24
echo 'set network successfully'

#route
gateway=`echo $IP | cut -d. -f1,2`.91.1
route del -net 0.0.0.0 netmask 0.0.0.0 dev eth0
route add default gw $gateway

#hosts
umount /etc/hosts

cat > /etc/hosts <<EOF
127.0.0.1 localhost
$IP     `hostname`
EOF

echo 'set host successfully'

cd /opt/letv/jetty/webapps/
mkdir webportal-webapp
cd webportal-webapp
jar -xvf /opt/letv/jetty/webapps/webportal-webapp.war
cp /opt/letv/jetty/jetty-web.xml /opt/letv/jetty/webapps/webportal-webapp/WEB-INF/
rm -rf /opt/letv/jetty/webapps/webportal-webapp.war

service gbalancer start
service jetty start
