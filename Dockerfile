# SPDX-FileCopyrightText: 2024 PNED G.I.E.
#
# SPDX-License-Identifier: Apache-2.0

# Curated Java trust store (CVE-2026-91403, RHSB-2026-012). The native binary otherwise keeps the
# store baked in by GraalVM at build time. Built from the patched RHEL store (ca-certificates
# 2026.2.92, RHSA-2026:88459); the build fails if keytool fails, the store has fewer than 100 roots,
# or emSign ECC Root CA - G3 or AAA Certificate Services is present. The Temurin stage only provides keytool.
FROM registry.access.redhat.com/ubi9-minimal:1793050429 AS rhel-ca
RUN microdnf update -y ca-certificates && microdnf clean all

FROM eclipse-temurin:21.0.12_8-jdk@sha256:92a2a4d7a928d057e7bd999c418d66c26a34eb9a0442f3ab67721c3f88110b2d AS truststore
COPY --from=rhel-ca /etc/pki/ca-trust/extracted/java/cacerts /tmp/cacerts
RUN keytool -list -v -keystore /tmp/cacerts -storepass changeit > /tmp/list \
 && awk '/^Alias name: /{a=substr($0,13)} /^Owner: CN=(emSign ECC Root CA - G3|AAA Certificate Services),/{print a}' /tmp/list > /tmp/distrust \
 && while IFS= read -r a; do keytool -delete -keystore /tmp/cacerts -storepass changeit -alias "$a"; done < /tmp/distrust \
 && keytool -list -v -keystore /tmp/cacerts -storepass changeit > /tmp/list \
 && [ "$(grep -c '^Alias name: ' /tmp/list)" -ge 100 ] \
 && ! grep -qE "^Owner: CN=(emSign ECC Root CA - G3|AAA Certificate Services)," /tmp/list

FROM registry.access.redhat.com/ubi9-minimal:1793050429
# Apply OS security fixes published after the pinned base image was built.
RUN microdnf update -y \
    && microdnf clean all
WORKDIR /work/
RUN chown 1001 /work \
    && chmod "g+rwX" /work \
    && chown 1001:root /work
COPY --chown=1001:root target/*-runner /work/application
COPY --from=truststore --chown=1001:root /tmp/cacerts /work/cacerts

EXPOSE 8080
USER 1001

ARG VERSION="local"
ARG VCS_REF="dirty"
ARG BUILD_DATE="unknown"

ENV MAINTAINER="PNED G.I.E." \
    APP_TITLE="userportal-dataset-discovery-service" \
    APP_DESCRIPTION="Microservice used by User Portal responsible for Dataset Discovery."

LABEL maintainer="${MAINTAINER}" \
      summary="${APP_TITLE}" \
      description="${APP_DESCRIPTION}" \
      org.opencontainers.image.vendor="${MAINTAINER}" \
      org.opencontainers.image.licenses="Apache-2.0" \
      org.opencontainers.image.title="${APP_TITLE}" \
      org.opencontainers.image.description="${APP_DESCRIPTION}" \
      org.opencontainers.image.version="${VERSION}" \
      org.opencontainers.image.revision="${VCS_REF}" \
      org.opencontainers.image.created="${BUILD_DATE}" \
      io.k8s.display-name="${APP_TITLE}" \
      io.k8s.description="${APP_DESCRIPTION}"

ENTRYPOINT ["./application", "-Dquarkus.http.host=0.0.0.0", "-Djavax.net.ssl.trustStore=/work/cacerts", "-Djavax.net.ssl.trustStorePassword=changeit"]
