FROM openjdk:11-jre-slim

EXPOSE 8080

COPY build/libs/feolife-backend*.jar feolife-backend-service.jar

ENTRYPOINT [ \
"java", \
"-jar", \
"-Xmx1g", \
"-XX:+UseG1GC", \
"-XX:MaxGCPauseMillis=50", \
"-XX:+DisableExplicitGC", \
"/feolife-backend-service.jar"]
