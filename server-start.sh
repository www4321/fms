#!/bin/bash
cd /root/fms/
PWD_PATH=`pwd`
APP_PATH="${PWD_PATH}/fms-controller.jar"
pid=`ps -ef |grep -v grep|grep "${APP_PATH}" |awk '{print $2}'`
if [ -n "$pid" ]; then
	echo $pid
	kill -9 $pid
fi
nohup java  -Dlog.path=${PWD_PATH}/logs  -jar $APP_PATH --server.port=8081 >&1 &
