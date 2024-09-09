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


## [v1.2.3] - 2024-09-09

### Added
- feat: add endpoint to retrieve list of facets by @admy7 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/112

### Changed
- chore: make CKAN filters configurable by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/93
- Replace "title" by "title_string" in sort string in CKAN API calls by @Markus92 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/95
- Revert sort title string by @hcvdwerf in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/97

### Fixed
- 32/fix issues by @admy7 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/102
- fix: remove fl attribute from CKAN by @admy7 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/105
- fix: filter null identifiers by @admy7 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/106

### Removed
- 32/remove logic from repository by @admy7 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/94
- Revert "32/remove logic from repository" by @zalborzi in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/99

### Dependencies
- chore(deps): update quarkus.platform.version to v3.14.1 (minor) by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/107
- chore(deps): update surefire.version to v3.5.0 (minor) by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/103

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
