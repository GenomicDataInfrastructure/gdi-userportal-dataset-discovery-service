<!--
SPDX-FileCopyrightText: 2024 PNED G.I.E.

SPDX-License-Identifier: CC-BY-4.0
-->

[![REUSE status](https://api.reuse.software/badge/github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service)](https://api.reuse.software/info/github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service)
![example workflow](https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/actions/workflows/main.yml/badge.svg)
![example workflow](https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/actions/workflows/test.yml/badge.svg)
![example workflow](https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/actions/workflows/release.yml/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=GenomicDataInfrastructure_gdi-userportal-dataset-discovery-service&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=GenomicDataInfrastructure_gdi-userportal-dataset-discovery-service)
[![GitHub contributors](https://img.shields.io/github/contributors/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service)](https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/graphs/contributors)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg)](code_of_conduct.md)

<div style="display: flex; justify-content: center; padding: 20px;">
  <img src="gdi_logo.svg" alt="Genomic Data Infrastructure Logo" width="300">
</div>

# GDI User Portal - Dataset Discovery Service

The GDI User Portal Dataset Discovery Service is a crucial component of the Genomic Data
Infrastructure (GDI) project, which aims to facilitate access to genomic, phenotypic, and clinical
data across Europe. The GDI project is committed to establishing a federated, sustainable, and
secure infrastructure to enable seamless data access. Leveraging the outcomes of the Beyond 1
Million Genomes (B1MG) project, the GDI project is actively realizing the ambitious goals set forth
by the 1+Million Genomes (1+MG) initiative.

The GDI User Portal Dataset Discovery Service serves as an interface between User Portal and the data
discovery tools. It is developed using [Quarkus](https://quarkus.io/) version 3
and [GraalVM](https://www.graalvm.org/) for Java 21. This application plays a crucial role in
enabling access request integration between the Data User and different Data Authorities.

- **Status**: 0.0.0
- **Related Project**: [1+ Million Genomes Project](https://gdi.onemilliongenomes.eu/)

## Installation

Ensure you have [Maven](https://maven.apache.org/) and [GraalVM](https://www.graalvm.org/) installed
in your machine. We recommend to use [SDKMAN!](https://sdkman.io/).

```shell script
sdk install java 21.0.2-graal
sdk install maven 3.9.6
echo -e "\nexport GRAALVM_HOME=\"$HOME/.sdkman/candidates/java/21.0.2-graal/\"" >> $HOME/.zprofile
echo -e "\nryuk.container.privileged=true" >> $HOME/.testcontainers.properties
echo -e "\nexport TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=\"${HOME}/.colima/docker.sock\"" >> $HOME/.zprofile
echo -e "\nexport DOCKER_HOST=\"unix:///${HOME}/.colima/docker.sock\"" >> $HOME/.zprofile
echo -e "\nexport TESTCONTAINERS_RYUK_DISABLED=true" >> $HOME/.zprofile
```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
mvn compile quarkus:dev
```

> **_NOTE:_** Quarkus now ships with a Dev UI, which is available in dev mode only
> at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:

```shell script
mvn package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into
the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
mvn package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
mvn package -Dnative
```

You can then execute your native executable with: `./target/*-runner`

If you want to learn more about building native executables, please
consult https://quarkus.io/guides/maven-tooling.

## Running tests

All tests are automatically executed when you build a new package.

## License

- All original source code is licensed under [Apache-2.0](./LICENSES/Apache-2.0.txt).
- All documentation is licensed under [CC-BY-4.0](./LICENSES/CC-BY-4.0.txt).
- For more accurate information, check the individual files.
