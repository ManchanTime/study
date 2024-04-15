# 도커 명령어

### docker build + run

+ 세부 사항 적용 실행

  docker run -d -p 8000:8000 -e  EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://172.16.212.76:8761/eureka --name gateway leeyoungchan/gateway-service

+ 그냥 실행

  docker run -d -p -e  --name gateway leeyoungchan/gateway-service

+ Docker Container 내부 접속

  docker exec -it {컨테이너 이름} /bin/bash

+ jar 파일 추출

  jar -tf {파일 이름}

+ yml 파일 확인

  jar -xf gateway.jar BOOT-INF/classes/application.yml

  cat BOOT-INF/classes/application.yml

### docker 정리

+ 사용하지 않는 Docker 이미지 제거

  docker image prune -a(-a는 사용 중이지 않는 모든 이미지를 제거)

+ 정지된 모든 컨테이너 제거

  docker container prune

+ 사용하지 않는 볼륨 제거

  docker volume prune

+ 사용하지 않는 네트워크 제거

  docker network prune

+ 시스템 전체 정리

  docker system prune -a

### kafka 클라우드 실행(도커는 실패, 경로는 모두 kafka_~~~ 폴더에서)

+ kafka 설치

  wget https://archive.apache.org/dist/kafka/버전/버전.gtz

+ heap 메모리 설정

  export KAFKA_HEAP_OPTS="-Xms400m -Xmx400m"

  해당 환경변수 설정을 유지하려면
  /.bashrc에 접속하여 위 코드를 마지막에 등록 후 source ~/.bashrc

  echo $KAFKA_HEAPS_OPTS로 확인

+ kafka 브로커 실행 옵션 설정

  vim config/server.properties로 접속

  listener~~ 주석 해제
  advertised.linesteners 주석 해제

+ zookeeper 실행

  bin/zookeeper-server-start.sh config/zookeeper.propreties (프론트 실행)

  bin/zookeeper-server-start.sh -daemon config/zookeeper.properties(백 그라운드 실행)

+ kafka 실행

  bin/kafka-server-start.sh config/server.properties
  
  bin/kafka-server-start.sh -daemon config/server.properties

+ kafka 실행 확인

  jps

  QuorumPeerMain
  Jps

+ kafka 종료

  bin/zookeeper-server-stop.sh config/zookeeper.properties
  
  bin/kafka-server-stop.sh config/server.properties


### 포트 허용

sudo ufw allow 포트번호/tcp
