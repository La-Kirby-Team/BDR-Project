FROM eclipse-temurin:21-jre

LABEL authors="Lestiboudois Maxime, Parisod Nathan, Surbeck Léon"
WORKDIR /app
COPY target/Winventory-0.9.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
CMD ["--help"]