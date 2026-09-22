# SPDX-FileCopyrightText: 2024 PNED G.I.E.
#
# SPDX-License-Identifier: Apache-2.0
FROM registry.access.redhat.com/ubi9-minimal:1789639833
WORKDIR /work/
RUN chown 1001 /work \
    && chmod "g+rwX" /work \
    && chown 1001:root /work
COPY --chown=1001:root target/*-runner /work/application

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

ENTRYPOINT ["./application", "-Dquarkus.http.host=0.0.0.0"]
