FROM openjdk:8-jre-alpine
COPY app/Divider.jar app/Divider.jar
COPY app/Divider.properties app/Divider.properties
COPY /data/ /data/
CMD ["java", "-cp", "app/Divider.jar:app/.", "Main.AppEntryPoint"]