# Use the official NATS Docker image as the base
FROM nats:alpine
RUN addgroup -S nonroot \
    && adduser -S nonroot -G nonroot

USER nonroot
# Expose the required ports for NATS and NATS Streaming
EXPOSE 8222 4222

# Set the command to start NATS with the desired options
CMD ["--http_port", "8222"]