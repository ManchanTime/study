# JIB 정리

+ 자바 애플리케이션을 간편하게 컨테이너로 만들 수 있도록 돕는 오픈 소스 기반 도구
+ 도커 파일 작성 없이 Maven Or Gradle에서 빌드하면 docker hub로 바로 push

+ 예시

      plugins {
    	id 'java'
    	id 'org.springframework.boot' version '3.2.4'
    	id 'io.spring.dependency-management' version '1.1.4'
    	id 'com.google.cloud.tools.jib' version '3.1.4'
      }
    
      jib {
      	from {
      		image = 'openjdk:17.0.2-slim'
      	}
      	to {
      		image = 'leeyoungchan/board-service'
      		tags = ['latest']
      	}
      	container {
      		jvmFlags = ['-Dspring.profiles.active=prod']
      		//jvmFlags = ["-Xms128m", "-Xmx128m"]
      	}
      }

  + plugin에 jib 등록
  + jib 설정 코드
    + from: jdk 정도
    + to: image 저장 위치(도커 허브) + 태그
    + container: flage로 최대 heap 메모리 정도?
