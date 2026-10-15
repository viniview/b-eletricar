# ---------- Estágio 1: build ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copia apenas o pom primeiro para aproveitar o cache de dependências
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Copia o código-fonte e gera o jar executável
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---------- Estágio 2: runtime ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usuário sem privilégios
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
