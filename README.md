    
    1. 하둡 실행
    /usr/local/hadoop/hadoop-2.7.5/sbin/start-dfs.sh
    
    2. 얀 실행
    /usr/local/hadoop/hadoop-2.7.5/sbin/start-yarn.sh
    
    3. 주키퍼 실행
    ${KAFKA_HOME}/bin/zookeeper-server-start.sh -daemon ${KAFKA_HOME}/config/zookeeper.properties
    
    4. 카프카 실행
    ${KAFKA_HOME}/bin/kafka-server-start.sh -daemon ${KAFKA_HOME}/config/server.properties
    
    5. 토픽 생성
    ${KAFKA_HOME}/bin/kafka-topics.sh --create --zookeeper 192.168.56.101:2181 --replication-factor 1 --partitions 1 --topic flume-sink
    
    6. 토픽 확인
    ${KAFKA_HOME}/bin/kafka-topics.sh --list --zookeeper 192.168.56.101:2181
    
    7. server3, 4 플럼 실행
    cd /usr/local/flume/apache-flume-1.7.0-bin/bin
    ./flume-ng agent -c ../conf/ -f ../conf/flume.conf -n agent01 -Dflume.root.logger=DEBUG,console -Dlog4j.configuration=../conf/log4j.properties &
    
    8. 스파크로 jar 실행
    $SPARK_HOME/bin/spark-submit --class 패키지명.클래스명  /mnt/subDisk/이름.jar
    $SPARK_HOME/bin/spark-submit --class com.exsparkbasic.ExSparkBasic.WordCount /mnt/subDisk/ExSparkBasic.jar
    $SPARK_HOME/bin/spark-submit --class com.nbreds.bigdata.test.job.ErrorCountJob /mnt/subDisk/JavaTest.jar
    $SPARK_HOME/bin/spark-submit --class com.nbreds.bigdata.Test /mnt/subDisk/ExSparkBasic.jar
    
    
    
    
    