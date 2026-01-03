# 第一阶段：使用 Maven 构建应用程序
FROM maven:3.9-eclipse-temurin-17-alpine AS build

# 设置工作目录
WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests -Dmaven.repo.local=/app/.m2/repository \
    -Drepository.id=aliyun-public \
    -Drepository.url=https://maven.aliyun.com/repository/public

# 第二阶段：运行应用程序（使用JRE而不是JDK，更轻量）
FROM eclipse-temurin:17-jre-alpine

# 设置工作目录
WORKDIR /app

# 创建非root用户运行应用（安全最佳实践）
RUN addgroup -S spring && adduser -S spring -G spring

# 从第一阶段复制构建好的 JAR 文件到当前镜像，并设置正确的所有者
COPY --from=build --chown=spring:spring /app/target/china-workday-api-0.0.1.jar /app/china-workday-api-0.0.1.jar

USER spring:spring

# 暴露端口
EXPOSE 8080

# 运行 Spring Boot 应用（添加JVM优化参数）
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "/app/china-workday-api-0.0.1.jar"]