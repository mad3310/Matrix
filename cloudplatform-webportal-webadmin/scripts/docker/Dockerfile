FROM 120.132.124.218:5000/polex/centos:6
MAINTAINER bingzheng.zhou <zhoubingzheng@letv.com>

RUN rpm -ivh http://pkg-repo.oss.letv.com/pkgs/centos6/letv-release.noarch.rpm 
RUN yum install jetty -y
RUN yum install vim -y
RUN yum install gbalancer -y
RUN yum install java-1.7.0-openjdk-devel.x86_64 -y
RUN yum install openssl nss -y
RUN rm -f /opt/letv/jetty/contexts/test.xml
RUN yum install dejavu* -y

USER root

ADD ./file/webportal-webadmin.war /opt/letv/jetty/webapps/webportal-webadmin.war
ADD ./file/webportal-webadmin.xml /opt/letv/jetty/contexts/webportal-webadmin.xml
ADD ./file/jetty-nosql-memcached-0.3.0-jar-with-dependencies.jar /opt/letv/jetty/lib/ext/jetty-nosql-memcached-0.3.0-jar-with-dependencies.jar
ADD ./file/jettyadmin.xml /opt/letv/jetty/etc/jetty.xml
ADD ./file/jetty-web.xml /opt/letv/jetty/jetty-web.xml

ADD ./file/jetty /etc/init.d/jetty
ADD ./file/gbalancer /etc/init.d/gbalancer
ADD ./file/cu.sh /opt/letv/jetty/cu.sh
ADD ./file/3306configuration.json /3306configuration.json
ADD ./file/init.sh /opt/letv/init.sh

RUN chmod 755 /etc/init.d/jetty
RUN chmod 755 /etc/init.d/gbalancer
RUN chmod 755 /opt/letv/jetty/cu.sh
RUN chmod 755 /opt/letv/init.sh
RUN bash /opt/letv/jetty/cu.sh

ENTRYPOINT bash /opt/letv/init.sh && /bin/bash
