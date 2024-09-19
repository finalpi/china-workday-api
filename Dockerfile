# 第一阶段：使用 Maven 构建应用程序
FROM maven:3.8.5-openjdk-17 AS build

# 设置工作目录
WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests -Dmaven.repo.local=/app/.m2/repository \
    -Drepository.id=aliyun-public \
    -Drepository.url=https://maven.aliyun.com/repository/public

# 第二阶段：运行应用程序
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 从第一阶段复制构建好的 JAR 文件到当前镜像
COPY --from=build /app/target/china-workday-api-0.0.1.jar /app/china-workday-api-0.0.1.jar

# 运行 Spring Boot 应用
ENTRYPOINT ["java", "-jar", "/app/china-workday-api-0.0.1.jar"]