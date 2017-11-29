FROM fabric8/java-alpine-openjdk8-jdk

ENTRYPOINT ["java" ,"-jar", "/app/app.jar"]

# Get executable properly
ADD ./target/scala-2.12/Reduc.io-assembly-0.1-SNAPSHOT.jar /app/app.jar
