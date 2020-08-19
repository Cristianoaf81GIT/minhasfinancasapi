#openjdk:8
FROM anapsix/alpine-java:8u201b09_jdk
EXPOSE 8080
VOLUME [ "/tmp" ]
#ARG JAR_FILE=target/minhasfinancas-0.0.1-SNAPSHOT.jar
#COPY ${JAR_FILE} minhas-financas.jar
ADD target/*.jar minhas-financas.jar
ENTRYPOINT ["java","-Xmx32m", "-Xss256k","-jar","/minhas-financas.jar"]