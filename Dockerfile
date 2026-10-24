# SPDX-FileCopyrightText: 2024 PNED G.I.E.
#
# SPDX-License-Identifier: Apache-2.0
# Curated Java trust store (CVE-2026-91403, RHSB-2026-012): the patched RHEL root list,
# with emSign ECC Root CA - G3 and AAA Certificate Services removed by subject if present.
# The build fails if either root is still in the store, or if keytool cannot read a store of at least 100 roots.
FROM registry.access.redhat.com/ubi9-minimal:1792742129 AS rhel-ca
RUN microdnf update -y ca-certificates && microdnf clean all

FROM eclipse-temurin:21-jdk@sha256:23cc5734ff46e776f51ce66eb9ee08444fd47caabec0a95b7eb6e94be2d3696d AS truststore
COPY --from=rhel-ca /etc/pki/ca-trust/extracted/java/cacerts /tmp/cacerts
RUN keytool -list -v -keystore /tmp/cacerts -storepass changeit > /tmp/list \
 && awk '/^Alias name: /{a=substr($0,13)} /^Owner: CN=(emSign ECC Root CA - G3|AAA Certificate Services),/{print a}' /tmp/list > /tmp/distrust \
 && while IFS= read -r a; do keytool -delete -keystore /tmp/cacerts -storepass changeit -alias "$a"; done < /tmp/distrust \
 && keytool -list -v -keystore /tmp/cacerts -storepass changeit > /tmp/list \
 && [ "$(grep -c '^Alias name: ' /tmp/list)" -ge 100 ] \
 && ! grep -qE "^Owner: CN=(emSign ECC Root CA - G3|AAA Certificate Services)," /tmp/list

FROM registry.access.redhat.com/ubi9-minimal:1792742129
RUN microdnf update -y && microdnf clean all
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
