# Etap budowania
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app

# Kopiowanie plików konfiguracyjnych maven
COPY pom.xml .
# Pobranie zależności (cache'owanie)
RUN mvn dependency:go-offline -B

# Kopiowanie kodu źródłowego
COPY src ./src

# Budowanie aplikacji
RUN mvn clean package -DskipTests

# Etap końcowy
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Kopiowanie zbudowanego pliku jar
COPY --from=build /app/target/Brute_force-0.0.1-SNAPSHOT.jar app.jar

# Port na którym działa aplikacja
EXPOSE 8080

# Uruchomienie aplikacji
ENTRYPOINT ["java", "-jar", "app.jar"]
