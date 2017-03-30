#!/bin/bash


moxi_install() {

grep -q 'pkg-repo.oss.letv.com' /etc/yum.repos.d/letv-pkgs.repo

if [ $? -eq 1 ]; then
cat <<EOF > /etc/yum.repos.d/letv-pkgs.repo
# letv-updates.repo
#
# Pkgs updates for letv internal systems
#
#
[letv-updates]
name=CentOS-$releasever - letv-updates
baseurl=http://115.182.51.149/pkgs/centos6/updates/
gpgcheck=0

[letv-extras]
name=CentOS-$releasever - letv-extras
baseurl=http://115.182.51.149/pkgs/centos6/extras/
gpgcheck=0

[letv-drivers]
name=CentOS-$releasever - letv-drivers
baseurl=http://115.182.51.149/pkgs/centos6/drivers/
gpgcheck=0

[staging]
name=CentOS-$releasever - staging
baseurl=http://115.182.51.149/pkgs/centos6/staging/
enabled=0
gpgcheck=0
EOF

fi

#rpm -ivh http://pkg-repo.oss.letv.com/pkgs/centos6/letv-release.noarch.rpm
#rpm -q moxi;echo "The moxi had been installed,exit!" || yum --disablerepo=* --enablerepo=base,letv* install moxi -y
rpm -q moxi --quiet&&echo "The moxi had been installed"&& exit 0 || yum --disablerepo=* --enablerepo=base,letv* install moxi -y

#/etc/init.d/moxi status

#sleep 5s
}

LDNS() {
#cat <<EOF > /etc/resolv.conf
#nameserver 117.121.58.247
#nameserver 10.200.0.3
#nameserver 202.106.0.20
#search et.letv.com
#options single-request-reopen
#EOF

grep -q 'search et.letv.com' /etc/resolv.conf || echo "search et.letv.com" >> /etc/resolv.conf
}

#monit() {

#grep -q 'include /etc/services.cfg' /etc/monitrc || echo -e "\ninclude /etc/services.cfg" >> /etc/monitrc

#test -e /etc/services.cfg
#cat <<EOF >> /etc/services.cfg

#check process moxi
#        with pidfile "/var/run/moxi/moxi.pid"
#        start program = "/etc/init.d/moxi start"
#        stop program = "/etc/init.d/moxi stop"
#        if totalmem > 1024 MB then restart
#        every "* 3-6 * * *"
#        depends on moxibase

#check process moxibase
#        with pidfile "/var/run/moxi/moxi.pid"
#        start program = "/etc/init.d/moxi start"
#        stop program = "/etc/init.d/moxi stop"
#EOF

#}

moxi()
{
dm="et.letv.com"
HOST_JXQ="'dcache-zb1-node1.$dm,dcache-zb1-node2.$dm,dcache-zb1-node3.$dm'"
HOST_CU="'dcache-zb2-node1.$dm,dcache-zb2-node2.$dm,dcache-zb2-node3.$dm'"
HOST_CT="'dcache-zb3-node1.$dm,dcache-zb3-node2.$dm,dcache-zb3-node3.$dm'"
HOST_LG="'dcache-zb4-node1.$dm,dcache-zb4-node2.$dm,dcache-zb4-node3.$dm'"
HOST_TEST="'dcache-za1-node1.$dm,dcache-za1-node2.$dm,dcache-za1-node3.$dm'"
CT_NEW="'cbase-ct1-node1.$dm,cbase-ct1-node2.$dm,cbase-ct1-node3.$dm,cbase-ct1-node4.$dm,cbase-ct1-node5.$dm'"
CT1_NEW="'cbase-ct3-node1.$dm,cbase-ct3-node2.$dm,cbase-ct3-node3.$dm,cbase-ct3-node4.$dm,cbase-ct3-node5.$dm'"
JXQ_NEW="'cbase-jxq-node1.$dm,cbase-jxq-node2.$dm,cbase-jxq-node3.$dm,cbase-jxq-node4.$dm,cbase-jxq-node5.$dm'"
LG_NEW="'dcache-zb5-node1.$dm,dcache-zb5-node2.$dm,dcache-zb5-node3.$dm,dcache-zb5-node4.$dm,dcache-zb5-node5.$dm'"
CU_NEW="'dcache-zb7-node1.$dm,dcache-zb7-node2.$dm,dcache-zb7-node3.$dm,dcache-zb7-node4.$dm,dcache-zb7-node5.$dm'"
CT_APP="'cbase-ct2-node1.$dm,cbase-ct2-node2.$dm,cbase-ct2-node3.$dm'"
HP_SO="'cbase-hp-node1.$dm,cbase-hp-node2.$dm,cbase-hp-node3.$dm,cbase-hp-node4.$dm,cbase-hp-node5.$dm'"
CU1_NEW="'cbase-cu2-node1.$dm,cbase-cu2-node2.$dm,cbase-cu2-node3.$dm'"
HP_DC="'cbase-hp1-node1.$dm,cbase-hp1-node2.$dm,cbase-hp1-node3.$dm,cbase-hp1-node4.$dm,cbase-hp1-node5.$dm'"
HK="'cbase-HK-node1.$dm,cbase-HK-node2.$dm,cbase-HK-node3.$dm,cbase-HK-node4.$dm'"
USA="'cbase-USA-node1.$dm,cbase-USA-node2.$dm,cbase-USA-node3.$dm'"
CT2_NEW="'cbase-ct4-node1.$dm,cbase-ct4-node2.$dm,cbase-ct4-node3.$dm,cbase-ct4-node4.$dm'"


cat <<EOF > /etc/sysconfig/moxi
# Change this with the cbase server info
# use {host1,host2} form for a multi-server configuration
CBASE_HOST=${CT2_NEW}
CBASE_BUCKET='mcluster'
CBASE_PWD='mcluster'
USER="nobody"
MAXCONN="1024"
CPROXY_ARG="/etc/moxi.conf"
OPTIONS=""

EOF
}

#yum install moxi -y
moxi_install
#LDNS
#monit
moxi

#/usr/bin/monit reload
