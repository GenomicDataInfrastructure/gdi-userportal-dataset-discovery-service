<!--
SPDX-FileCopyrightText: 2024 PNED G.I.E.

SPDX-License-Identifier: CC-BY-4.0
-->

# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security 

Sure! Here's the organized list:


## [v1.2.2] - 2024-08-21

### Added
* feat: make service agnostic on repo impl by @sulejmank in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/31
* feat: renovate integration by @sehaartuc in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/35
* feat: trivy and ort implementation by @sehaartuc in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/34
* feat: add dataset relations and dictionary by @zalborzi in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/74
* Added creator field to discovery service by @Markus92 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/80

### Changed
* chore(deps): update oss-review-toolkit/ort-ci-github-action digest to 81698a9 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/38
* chore(deps): update docker/login-action digest to 0d4c9c5 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/37
* chore(deps): update azure/webapps-deploy digest to 5c1d76e by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/36
* chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.4-1134 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/41
* chore(deps): update quarkus-wiremock.version to v1.3.3 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/40
* chore(deps): update dependency org.apache.maven.plugins:maven-failsafe-plugin to v3.3.1 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/45
* chore(deps): update dependency org.apache.maven.plugins:maven-compiler-plugin to v3.13.0 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/44
* chore(deps): update dependency org.jacoco:jacoco-maven-plugin to v0.8.12 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/39
* chore(deps): update dependency net.revelc.code.formatter:formatter-maven-plugin to v2.24.1 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/43
* chore(deps): update docker/build-push-action action to v6 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/50
* chore(deps): update fsfe/reuse-action action to v4 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/51
* chore(deps): update dependency org.apache.maven.plugins:maven-surefire-plugin to v3.3.1 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/48
* chore(deps): update dependency org.assertj:assertj-core to v3.26.3 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/49
* chore(deps): update quarkus.platform.version to v3.12.3 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/72
* chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.4-1194 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/73
* chore(deps): update quarkus.platform.version to v3.13.0 (minor) by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/78
* chore(deps): update dependency org.apache.maven.plugins:maven-failsafe-plugin to v3.4.0 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/90
* chore(deps): update quarkus.platform.version to v3.13.1 (patch) by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/86
* chore(deps): update quarkus.platform.version to v3.13.2 (patch) by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/87
* chore(deps): update quarkus.platform.version to v3.13.3 (patch) by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/92
* chore(deps): update dependency org.apache.maven.plugins:maven-surefire-plugin to v3.4.0 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/91
* chore: #76 retrieve dataset content in different formats by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/81
* chore: retrieve organization metadata by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/82

### Fixed
* fix(deps): update dependency org.projectlombok:lombok to v1.18.34 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/42
* fix: revert removing hasversion and conformsto by @zalborzi in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/77
* fix: fix contact point parsing by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/83
* Fix for contact point mapping by @Markus92 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/84
* fix(deps): update dependency io.quarkiverse.openapi.generator:quarkus-openapi-generator to v2.4.6 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/85
* fix(deps): update dependency io.quarkiverse.openapi.generator:quarkus-openapi-generator to v2.4.7 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/88
* fix: compatibility of CKAN creator field with new scheming by @Markus92 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/89


## [v1.2.0] - 2024-06-12

### Added

- feat: Design API by @zalborzi in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/5
- feat: enhance api by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/6
- feat: #7 how are ls aai access token passport and visas retrieved via keycloak by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/8
- feat: #3 integrate with CKAN by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/9
- feat: #3 add retrieve dataset endpoint by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/10
- feat: extend api with fields needed for ui by @sulejmank in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/15
- feat: #4 integrate with Beacon by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/13
- 16 gdi dataset discovery service integrate with new endpoints to retrieve label of values by @sulejmank in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/19
- feat: Make continious deployment possible to azure by @hcvdwerf in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/20
- feat: #26 add facet query operator by @sulejmank in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/27

### Changed

- chore: replace private constructors by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/12

### Fixed

- fix: handle correctly missing beacon access token and empty beacon re… by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/14
- fix: #17 Retrieve Beacon Facets when logged-in by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/18
- fix: fix beacon integration by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/25
- fix: retrieve multiple datasets from Beacon by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/28
