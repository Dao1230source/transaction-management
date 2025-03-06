# 使用官方 OpenJDK 21 基础镜像
FROM openjdk:21

WORKDIR /app

# 将构建好的 jar 文件复制到容器中
COPY target/transaction-management-0.0.1-SNAPSHOT.jar .

# 暴露应用端口
EXPOSE 8080

# 定义运行应用的命令
ENTRYPOINT ["java", "-jar", "transaction-management-0.0.1-SNAPSHOT.jar"]
