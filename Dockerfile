FROM clojure:latest AS builder
ENV CLOJURE_VERSION=1.11.1.1182
RUN mkdir -p /build
WORKDIR /build
COPY ./ /build/

RUN clojure -T:build ci

FROM eclipse-temurin:17-alpine AS runner
RUN addgroup -S lanchonete && adduser -S lanchonete -G lanchonete
RUN mkdir -p /service && chown -R lanchonete. /service
USER lanchonete

RUN mkdir -p /service
WORKDIR /service
ENV HTTP_PORT=8080
EXPOSE 8080
COPY --from=builder /build/target/net.clojars.mba-fiap/lanchonete-0.1.0-SNAPSHOT.jar /service/lanchonete.jar
ENTRYPOINT ["java", "-jar", "/service/lanchonete.jar"]
