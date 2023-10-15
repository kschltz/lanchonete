FROM clojure:temurin-17-alpine AS builder
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
COPY --from=builder /build/target/net.clojars.mba-fiap/lanchonete-0.1.0-SNAPSHOT.jar /service/lanchonete.jar
CMD ["java", "-jar", "/service/lanchonete.jar"]