FROM shopify/toxiproxy:2.1.3
COPY config.json config.json
EXPOSE ${DYNAMIC_PORT}
CMD ["-config", "config.json", "-host", "0.0.0.0", "-port", "${DYNAMIC_PORT}"]
