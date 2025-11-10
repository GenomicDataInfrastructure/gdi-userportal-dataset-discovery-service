# SPDX-FileCopyrightText: 2024 PNED G.I.E.
#
# SPDX-License-Identifier: Apache-2.0
FROM registry.access.redhat.com/ubi9-minimal:9.6
WORKDIR /work/
RUN chown 1001 /work \
    && chmod "g+rwX" /work \
    && chown 1001:root /work
COPY --chown=1001:root target/*-runner /work/application

EXPOSE 8080
USER 1001

ENV MAINTAINER "PNED G.I.E."
ENV APP_TITLE "userportal-dataset-discovery-service"
ENV APP_DESCRIPTION "Microservice used by User Portal responsible for Dataset Discovery."

LABEL maintainer ${MAINTAINER}
LABEL summary ${APP_TITLE}
LABEL description ${APP_DESCRIPTION}

LABEL org.opencontainers.image.vendor ${MAINTAINER}
LABEL org.opencontainers.image.licenses Apache-2.0
LABEL org.opencontainers.image.title ${APP_TITLE}
LABEL org.opencontainers.image.description ${APP_DESCRIPTION}

LABEL io.k8s.display-name ${APP_TITLE}
LABEL io.k8s.description ${APP_DESCRIPTION}

ENTRYPOINT ["./application", "-Dquarkus.http.host=0.0.0.0"]
