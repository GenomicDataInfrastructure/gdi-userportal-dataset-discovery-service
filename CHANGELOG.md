<!--
SPDX-FileCopyrightText: 2024 PNED G.I.E.

SPDX-License-Identifier: CC-BY-4.0
-->

# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [v1.3.34] - 2026-01-22

### Changed
- applied review comments by @Hans-christian in 307d05b


### Fixed
- fix(hvd_catagory) Unit tests are fixed now by @Hans-christian in 24a4ac9
- fix(hvd) fix hvd catagory and property size by @Hans-christian in 3cd40a9
- fix(redhat) Use correct container image by @Hans-christian in aca9b85


## [v1.3.32] - 2026-01-21

### Fixed
- fix(redhat) Use correct container image (#293) by @Hans-Christian in fa4ce93


## [v1.3.29] - 2025-12-01

### Added
- feat: add includeBeacon parameter for optional Beacon Network queries (#273) by @Inderpal Singh in 923d101


### Changed
- chore(deps): update quarkus.platform.version to v3.30.1 by @Renovate Bot in 3dbd59a
- chore(deps): update quarkus.platform.version to v3.29.4 by @Renovate Bot in 03fe587
- chore(deps): update quarkus-wiremock.version to v1.5.2 by @Renovate Bot in a481f80
- Clean up CHANGELOG.md by removing old entries by @Quinten in a1460b5
- doc: update CHANGELOG.md for v1.3.28 by @LNDS-Sysadmins in 9c8e2fe


## [v1.3.28] - 2025-11-18

### Added
- feat(ValueLabel) add more fields which are resolvable by @Hans-christian in 1c6d1a0

### Fixed
- fix UT by @Hans-christian in 6c7da35
- fix: docker pulls the latest version when creating the image by @Rania Hamdani in 88624bb
- fix(qualified relation) remove obsolete uri by @Hans-christian in 9af5bc0
- fix: upgrade from the implicit BOM version to 3.18.0 (#263) by @RaniaHamdani2 in 43f86cb
- fix: upgrade from the implicit BOM version to 3.18.0 by @Rania Hamdani in 802a9ba


## [v1.3.27] - 2025-10-30

### Fixed
- fix(role-attribution) fix role attribution (#260) by @Hans-Christian in 3da9008
- Revert "fix: update CKAN schema for array-type fields" (#259) by @Hans-Christian in 0120e0d


## [v1.3.25] - 2025-10-28

### Fixed
- fix: update CKAN schema for array-type fields by @Inderpal Singh in 1740c0d


## [v1.3.24] - 2025-10-28

### Changed
- test: add keywords to expected empty collections test by @Inderpal Singh in 5da7ff2
- doc: update CHANGELOG.md for v1.3.23 by @LNDS-Sysadmins in 5b71fe5


### Fixed
- fix: update test to use CkanValueLabel for tags instead of strings by @Inderpal Singh in 19e7795
- fix: add mapper method for CkanValueLabel list to string list conversion by @Inderpal Singh in 337845c
- fix: update CKAN integration for data structure changes by @Inderpal Singh in 5703d92


## [v1.3.23] - 2025-10-28

### Changed
- chore(deps): update quarkus.platform.version to v3.28.5 by @Renovate Bot in 76e5e36
- doc: update CHANGELOG.md for v1.3.22 by @LNDS-Sysadmins in cf394f2


### Fixed
- fix: enable HTTP redirect following for CKAN REST client by @Inderpal Singh in 70de3bd
- Revert "fix(tags) reverts tags" by @Hans-christian in ecfd4c2
- fix(pulbisher note) fix publisher note serialisation by @Hans-christian in e4500c4


## [v1.3.22] - 2025-10-21

### Changed
- doc: update CHANGELOG.md for v1.3.21 by @LNDS-Sysadmins in 8858610


### Fixed
- fix(tags) reverts tags by @Hans-christian in efbc270


## [v1.3.21] - 2025-10-21

### Changed
- doc: update CHANGELOG.md for v1.3.20 (#244) by @github-actions[bot] in ea3e2d2
- chore(deps): update quarkus.platform.version to v3.28.4 by @Renovate Bot in 8e914c2
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.6-1760515502 by @Renovate Bot in 1bc5bd6
- chore(deps): update quarkus.platform.version to v3.28.3 by @Renovate Bot in 3604bdf
- chore(deps): update dependency org.jacoco:jacoco-maven-plugin to v0.8.14 by @Renovate Bot in bf20426


### Fixed
- fix(status) fix onttology by @Hans-christian in 174cb32


## [v1.3.20] - 2025-10-09

### Added
- feat(translate) more translated fields (#243) by @Hans-Christian in 7390a6e


### Changed
- chore(deps): update fsfe/reuse-action action to v6 (#239) by @LNDS-Sysadmins in f75eaf3
- chore: update changelog for v1.3.19 (#242) by @github-actions[bot] in 64be5ee


## [v1.3.19] - 2025-10-08

### Fixed
- fix(model) CKAN <-> Discovery service and use type for filter by @Hans-Christian in 5b51efe


## [v1.3.18] - 2025-10-07

### Added
- feat(ValueLabel) add values label for more fields to be resolved by @Hans-Christian in b944430
- feat(filters) support for datetime filters (#236) by @Hans-Christian in b06d3a3


### Changed
- Support number filters (#237) by @Hans-Christian in ca3b918
- chore(deps): update dependency org.assertj:assertj-core to v3.27.6 by @Renovate Bot in 58828d7
- chore(deps): update dependency org.apache.maven.plugins:maven-compiler-plugin to v3.14.1 by @Renovate Bot in c8a5a10
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.6-1758184547 by @Renovate Bot in 95ced69
- chore(deps): update dependency org.assertj:assertj-core to v3.27.5 by @Renovate Bot in 6a50ef4
- chore(deps): update quarkus.platform.version to v3.26.4 by @Renovate Bot in ef0da9e
- doc: update CHANGELOG.md for v1.3.17 by @LNDS-Sysadmins in 2b95fa2


### Fixed
- fix(deps): update dependency org.projectlombok:lombok to v1.18.42 by @Renovate Bot in 36952a3


## [v1.3.17] - 2025-09-17

### Added
- feat(api): implement Accept-Language header support for dataset queries and fetch filters by @Hans-christian in aaac22e
- feat(fields) Add minimum 0 to number of individueal by @Hans-christian in 23c9cad
- feat(datasearchResults) Add number of fields, temporal Coverage and access rights to SearchDataResults by @Hans-christian in 67ce965
- feat Extend Search data results by @Hans-christian in 95330c4


### Changed
- chore(deps): update surefire.version to v3.5.4 by @Renovate Bot in 717dd76
- doc: update CHANGELOG.md for v1.3.16 by @LNDS-Sysadmins in 01c0e1b


### Fixed
- fix build by @Hans-christian in 6b44f85
- fix Unit tests by @Hans-christian in 9b707e0


## [v1.3.16] - 2025-09-12

### Changed
- chore(deps): update dependency net.revelc.code.formatter:formatter-maven-plugin to v2.29.0 by @Renovate Bot in e0625f7
- doc: update CHANGELOG.md for v1.3.15 by @LNDS-Sysadmins in 1f4f1da
- chore(deps): update dependency net.revelc.code.formatter:formatter-maven-plugin to v2.28.0 by @Renovate Bot in 2248b26


### Fixed
- fix: UT for conformsTO and documentation by @Hans-christian in 983ccea
- fix: openapi file for arrays by @Hans-christian in b3dbfe2


## [v1.3.15] - 2025-09-11

### Added
- feat: group filters by key by @Bruno Pacheco in 4867717


### Changed
- doc: update CHANGELOG.md for v1.3.14 by @LNDS-Sysadmins in 5343371


### Fixed
- fix(test): adjust test to reflect changes in count logic by @kburger in 85aab27
- fix(count) Fix paging by @Hans-christian in ceae146


## [v1.3.14] - 2025-09-10

### Changed
- chore(deps): update quarkus.platform.version to v3.26.3 by @Renovate Bot in 627064f
- doc: update CHANGELOG.md for v1.3.13 by @LNDS-Sysadmins in fde2770


### Fixed
- fix(openapi): avoid complex objects in access service (unsupported for now) by @Hans-christian in 0f18b4e
- fix(deps): update dependency org.projectlombok:lombok to v1.18.40 by @Renovate Bot in 8f76758


## [v1.3.13] - 2025-09-04

### Changed
- doc: update CHANGELOG.md for v1.3.12 by @LNDS-Sysadmins in 4380bf6


### Fixed
- fix: remove null objects from list by @Bruno Pacheco in af41fe9


## [v1.3.12] - 2025-09-04

### Changed
- chore(deps): update aquasecurity/trivy-action action to v0.33.1 by @Renovate Bot in 684a8b6
- chore(deps): update quarkus.platform.version to v3.26.2 by @Renovate Bot in 498b467
- doc: update CHANGELOG.md for v1.3.11 by @LNDS-Sysadmins in 84fcf6b


### Fixed
- fix: avoid nullpointer when gvariants return empty results by @Bruno Pacheco in 950d88a
- fix(deps): update quarkus-openapi-generator.version to v2.12.1-lts by @Renovate Bot in 6c7e893


## [v1.3.11] - 2025-09-02

### Added
- feat(missing fields) Add missing fields to OpenAPI specs (#212) by @Hans-Christian in a1f544a


### Changed
- chore(deps): update actions/checkout action to v5 by @Renovate Bot in f609eab
- chore(deps): update quarkus.platform.version to v3.26.1 by @Renovate Bot in d06d190
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.6-1755695350 by @Renovate Bot in a7d1a64
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.6-1754584681 by @Renovate Bot in c113bb3
- chore(deps): update dependency org.assertj:assertj-core to v3.27.4 by @Renovate Bot in a92a7f9
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.6-1754456323 by @Renovate Bot in d292386
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.6-1754356396 by @Renovate Bot in 9319eed
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.6-1754000177 by @Renovate Bot in e569949
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.6-1753762263 by @Renovate Bot in 07e9bab
- chore(deps): update quarkus.platform.version to v3.24.5 by @Renovate Bot in 561b8d7
- chore(deps): update quarkus.platform.version to v3.24.4 by @Renovate Bot in 0295354
- chore(deps): update quarkus-wiremock.version to v1.5.1 by @Renovate Bot in a7412ed
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.6-1752587672 by @Renovate Bot in 6d0e7c8
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.6-1752069876 by @Renovate Bot in 8125b5b
- chore(deps): update quarkus.platform.version to v3.24.3 by @Renovate Bot in a25b45b
- doc: update CHANGELOG.md for v1.3.10 by @LNDS-Sysadmins in 0fd0fa3


### Fixed
- fix(deps): update quarkus-openapi-generator.version to v2.12.0-lts by @Renovate Bot in 2533e49
- fix(license) Make it a string instead of URI by @Hans-christian in 8577234
- fix: add workaround for the license field type assumption by @kburger in 85488da
- fix: typo in field name and field types by @kburger in 097bb25
- fix(deps): update quarkus-openapi-generator.version to v2.11.0-lts by @Renovate Bot in 52eaac8


## [v1.3.10] - 2025-07-08

### Changed
- chore(deps): update quarkus.platform.version to v3.24.2 by @Renovate Bot in c61559a
- chore(deps): update aquasecurity/trivy-action action to v0.32.0 by @Renovate Bot in f9fea4a
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.6-1751286687 by @Renovate Bot in aaafdad
- chore(deps): update dependency net.revelc.code.formatter:formatter-maven-plugin to v2.27.0 by @Renovate Bot in 55c5491
- chore(deps): update quarkus-wiremock.version to v1.5.0 by @Renovate Bot in 4f4e507
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.5-1747111267 by @Renovate Bot in b217b3a
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.5-1745855087 by @Renovate Bot in 58f0ceb
- chore(deps): update quarkus.platform.version to v3.21.4 by @Renovate Bot in cc82724
- chore(deps): update quarkus.platform.version to v3.21.3 by @Renovate Bot in ca764bd
- chore(deps): update quarkus.platform.version to v3.21.2 by @Renovate Bot in 487821c
- refactor scripts by @jadzlnds in f00585c
- refactor scripts by @jadzlnds in 5fdda68
- doc: Update CHANGELOG.md for v1.3.9 by @LNDS-Sysadmins in 7851010


### Fixed
- fix: show correct count value when multiple datasets share the same identifier by @kburger in df7a8da
- fix typo by @jadzlnds in cd833d8
- fix typo by @jadzlnds in 5085ad7
- fix typo by @jadzlnds in 64f16dc
- fix typo by @jadzlnds in 1dbc70b


## [v1.3.9] - 2025-04-07

### Added
- feat(dataset): add HealthDCAT fields to RetrievedDataset and update mapping by @Hans-Chrstian in b8e1f4b


### Changed
- refactor script by @jadzlnds in 386fe55
- doc: Update CHANGELOG.md for v1.3.8 by @LNDS-Sysadmins in 748aa53
- Appllied comments. Discovery part of a openAPI follows camCase format by @Hans-Chrstian in 746ba7e
- Remove unused frequency by @Hans-Chrstian in 71cc0ab
- Apply suggestions from code review by @Hans-Christian in 468223c


## [v1.3.8] - 2025-04-04

### Changed
- refactor script by @jadzlnds in 7647c84
- refactor script by @jadzlnds in c1ad616
- refactor script by @jadzlnds in f04dca3
- refactor script by @jadzlnds in bf8c93c
- refactor script by @jadzlnds in 2641dd4
- refactor script by @jadzlnds in d60e512
- 📜 Update CHANGELOG.md for v1.3.8 by @jadzlnds in f291c33
- refactor script by @jadzlnds in 64232ae
- refactor script by @jadzlnds in 0d97769
- refactor script by @jadzlnds in 7ba291a
- refactor script by @jadzlnds in 2dc6de5
- refactor script by @jadzlnds in 7180171
- refactor script by @jadzlnds in 068332e
- refactor update scripts by @jadzlnds in d0a54f6
- refactor update scripts by @jadzlnds in f620c4a
- refactor update scripts by @jadzlnds in 753d894
- refactor update scripts by @jadzlnds in 3720985
- refactor update scripts by @jadzlnds in 0f2e434
- refactor scripts by @jadzlnds in 85f50b9


## [v1.3.7] - 2025-04-04

### Changed
- refactor changelog.sh by @jadzlnds in 48892ad
- refactor changelog.sh by @jadzlnds in 2d0084b
- chore(deps): update quarkus.platform.version to v3.21.1 by @Renovate Bot in f4673d8
- chore(deps): update dependency org.jacoco:jacoco-maven-plugin to v0.8.13 by @Renovate Bot in 3fbbbb5
- chore(deps): update quarkus.platform.version to v3.21.0 by @Renovate Bot in 304fa0f
- chore(deps): update surefire.version to v3.5.3 by @Renovate Bot in e003577
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.5-1742914212 by @Renovate Bot in 55e2f78
- Feat/g variant does not require auth (#198) by @Younès Adem in 4b3e51a
- chore(deps): update dependency net.revelc.code.formatter:formatter-maven-plugin to v2.26.0 by @Renovate Bot in 7a85997
- chore(deps): update quarkus.platform.version to v3.19.4 by @Renovate Bot in ba18dee
- chore(deps): update aquasecurity/trivy-action action to v0.30.0 by @Renovate Bot in 83221fc
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.5-1741850109 by @Renovate Bot in 6f2f844
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.5-1741599792 by @Renovate Bot in 6ab02a8
- chore add changelog.sh by @jadzlnds in c1f1d0c
- chore add changelog.sh by @jadzlnds in f272c55
- 📜 Update CHANGELOG.md for v1.3.6 by @jadzlnds in 521467d
- 📜 Update CHANGELOG.md for v1.3.1 by @jadzlnds in 2b582b9
- chore(deps): update dependency org.apache.maven.plugins:maven-compiler-plugin to v3.14.0 by @Renovate Bot in 490243b
- chore(deps): update quarkus.platform.version to v3.18.4 by @Renovate Bot in 0da0b0b
- chore: use gh release create by @jadzlnds in ee880f8
- chore: use gh release create by @jadzlnds in 0b484b9
- chore: use gh release create by @jadzlnds in 2b9616e
- chore: add user by @jadzlnds in 4a8e18f
- chore: add user by @jadzlnds in 64822cd
- chore: add user by @jadzlnds in 0cc5ef9
- chore: update changelog script by @jadzlnds in 7b72bf8
- chore: update changelog script by @jadzlnds in 007de8f


### Fixed
- fix(deps): update quarkus-openapi-generator.version to v2.9.1-lts by @Renovate Bot in 1d2cf2a
- fix(deps): update dependency org.projectlombok:lombok to v1.18.38 by @Renovate Bot in 86d52c7
- fix(deps): update quarkus-openapi-generator.version to v2.9.0-lts by @Renovate Bot in e86ce40
- fix(mapping): fix uri creator and publisher by @Hans-Chrstian in 3847563
- fix(deps): update quarkus-openapi-generator.version to v2.8.2-lts by @Renovate Bot in 267898e
- fix(deps): update quarkus-openapi-generator.version to v2.8.1-lts by @Renovate Bot in c96050b



## [v1.3.6] - 2025-02-27

### Added
- feat: add release trigger by @jadz94 in cba50eb
- feat: query on genomic variants by @jadz94 in c51253e


### Changed
- chore: update changelog script by @jadz94 in a46dddd
- chore: update changelog script by @jadz94 in ca6313e
- chore: update changelog script by @jadz94 in 3e26704
- chore: update hem installation script by @jadz94 in e5f7c0f
- chore: add user identity by @jadz94 in 9b4c7af
- chore: update script by @jadz94 in 54a246f
- chore: update script by @jadz94 in 9dd0583
- chore: update script by @jadz94 in ba34f06
- chore: update script by @jadz94 in b10178e
- chore: update script by @jadz94 in d10f195
- chore: polish by @jadz94 in 6fc146d
- chore: polish by @jadz94 in 8340c3e
- chore(deps): update softprops/action-gh-release action to v2 by @Renovate Bot in c7231be
- Update release.yml by @jadzlnds in 2aba103
- chore(deps): update dependency net.revelc.code.formatter:formatter-maven-plugin to v2.25.0 by @Renovate Bot in 99287e2
- chore: polish by @jadz94 in 8d9f675
- chore: polish script logs by @jadz94 in 3601d3a
- chore: format code by @jadz94 in ed75ba4
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.5-1739420147 by @Renovate Bot in a28d9a6
- chore(deps): update quarkus.platform.version to v3.18.3 by @Renovate Bot in 53d11b3


### Fixed
- fix(deps): update quarkus-openapi-generator.version to v2.8.1 by @Renovate Bot in eed98d4
- fix: fix mapper by @jadz94 in 58530de
- fix: type by @jadz94 in 3c590b0




## [v1.3.5] - 2025-02-27

### Changed
- chore: update tests by @jadz94 in acac345
- chore(deps): update quarkus.platform.version to v3.18.2 by @Renovate Bot in 07cfdf7
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.5-1738816775 by @Renovate Bot in 4d738cf
- chore(deps): update quarkus.platform.version to v3.17.8 by @Renovate Bot in cd58abd


### Fixed
- fix(deps): update quarkus-openapi-generator.version to v2.8.0-lts by @Renovate Bot in e4ad44a



## [v1.3.4] - 2025-02-27

### Added
- feat(ART-11336): add type to RetrievedDataset by @Antoine Dorard in 55c9301


### Changed
- chore(deps): update dependency org.assertj:assertj-core to v3.27.3 by @Renovate Bot in 7cf5485
- chore(deps): update quarkus.platform.version to v3.17.7 by @Renovate Bot in 1a3dbee
- chore: add labels to docker image by @Bruno Pacheco in 124c942
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.5-1736404155 by @Renovate Bot in 84ed1f6
- chore(deps): update quarkus.platform.version to v3.17.6 by @Renovate Bot in 036c0d7
- chore(deps): update dependency org.assertj:assertj-core to v3.27.2 by @Renovate Bot in 90a9953
- chore(deps): update dependency org.assertj:assertj-core to v3.27.0 by @Renovate Bot in 74d6620
- chore(deps): update quarkus.platform.version to v3.17.5 by @Renovate Bot in f5bb528


### Fixed
- fix(ART-11336): change from ckan attribute 'type' to 'dcat_type' by @Antoine Dorard in 1868cb4
- chore: fix REPOSITORY_URL by @Bruno Pacheco in 0298841
- chore: (ART-11568) fix quarkus issue by @jadz94 in bf6f006
- fix(deps): update dependency io.quarkiverse.openapi.generator:quarkus-openapi-generator to v2.7.1-lts by @Renovate Bot in 1e72bd0
- chore: automerge patches by @Bruno Pacheco in 7cad42c
- fix(deps): update dependency io.quarkiverse.openapi.generator:quarkus-openapi-generator to v2.7.0-lts by @Renovate Bot in 95b9694



## [v1.3.3] - 2025-02-27

### Changed
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.5-1734497536 by @Renovate Bot in b76ba44
- chore: specify required fields in open api specs by @Younès Adem in 91edde6
- chore(deps): update quarkus-wiremock.version to v1.4.1 by @Renovate Bot in c4b3bee
- chore(deps): update quarkus.platform.version to v3.17.4 by @Renovate Bot in c67c41e
- chore: (ART-10814) update operator filters by @jadz94 in 035101e
- chore: (ART-10814) update beacon api by @jadz94 in fe7e3ca
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.5-1733767867 by @Renovate Bot in 6f6ade7
- chore(deps): update quarkus.platform.version to v3.17.3 by @Renovate Bot in 289de61
- chore: (ART-10521) update license by @jadz94 in 9c2f3c0
- chore: (ART-10521) add local otel collector by @jadz94 in 991f677


### Fixed
- fix(deps): update mapstruct monorepo to v1.6.3 by @Renovate Bot in 79b285d



## [v1.3.2] - 2025-02-27

### Added
- feat(mapping): Update mapping with missing fields after updating to CKAN DCAT EXTENSION 2.1.0 by @Hans-Chrstian in 6503b68
- feat: align beacon filters with Beacon Network options by @Bruno Pacheco in 9bf0ef5
- feat: ART-9692/add themes and publishers by @Kacem Bechka in 0aca192


### Changed
- chore: (ART-10688) polishing by @jadz94 in 350c818
- chore: (ART-10688) polishing by @jadz94 in e97062a
- chore(deps): update quarkus.platform.version to v3.17.2 by @Renovate Bot in be2a40a
- chore(deps): update quarkus.platform.version to v3.17.0 by @Renovate Bot in 37e6d70
- chore: (ART-10688) refactor by @jadz94 in 670f9a7
- chore: (ART-10688) update license by @jadz94 in 3dbe99d
- chore: (ART-10688) refactor ckan mapper by @jadz94 in e6e881d
- chore(deps): update quarkus.platform.version to v3.16.4 by @Renovate Bot in c1ad239
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.5-1731604394 by @Renovate Bot in 1fd8042
- chore(deps): update aquasecurity/trivy-action action to v0.29.0 by @Renovate Bot in ebe59a0
- Revert "chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.5-1731518200" by @Bruno Pacheco in 7064ca6
- chore: (ART-10607) change beacon-filters location by @jadz94 in aa1541a
- chore(deps): update fsfe/reuse-action action to v5 by @Renovate Bot in 7042fab
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.5-1731518200 by @Renovate Bot in a4ce995
- chore(deps): update quarkus.platform.version to v3.16.3 by @Renovate Bot in fa7b15e
- chore: (ART-10607) polishing by @jadz94 in d4d2671
- chore: (ART-10607) add license by @jadz94 in 7d29339
- chore: (ART-10607) add license by @jadz94 in 994273f
- chore: (ART-10607) add config and doc for local deployment by @jadz94 in f87690d
- Revert "chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.5-1730489338" by @Kacem Bechka in 06bc747
- chore: rename the filters by @Kacem Bechka in eb29d4b
- chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.5-1730489338 by @Renovate Bot in 0b7aa93
- chore: revert the tests by @Kacem Bechka in 6e26649
- chore:  polish tests stub mappings by @jadz94 in dfa864c
- add tests for filters by @Kacem Bechka in 0212555
- chore(deps): update quarkus.platform.version to v3.16.2 by @Renovate Bot in b68c780


### Fixed
- fix: set request parameter to null when there is no variant by @Bruno Pacheco in 1c0c056
- fix(deps): update dependency org.projectlombok:lombok to v1.18.36 by @Renovate Bot in b3670fe
- bugfix: (ART-10607) fix issue with resource not found exception by @jadz94 in 2128446
- chore: fix compliance by @Kacem Bechka in 6a8092d
- chore:  fix tests by @jadz94 in 59f1c80
- fix(deps): update dependency io.quarkiverse.openapi.generator:quarkus-openapi-generator to v2.6.0-lts by @Renovate Bot in f0f6175
- fix(deps): update dependency io.quarkiverse.openapi.generator:quarkus-openapi-generator to v2.6.0 by @Renovate Bot in 96e4e9c



## [v1.3.1] - 2025-02-27

### Added
- feat: add languages to distributions by @Bruno Pacheco in 0a777a9
- feat: add access_url and download_url to distribution by @Bruno Pacheco in 96e2bbb


### Changed
- chore(deps): update surefire.version to v3.5.2 by @Renovate Bot in 39b30b4
- chore: add otel by @Bruno Pacheco in d783ccd
- chore: use cached trivy db by @Bruno Pacheco in 0537287
- chore: cache triby db by @Bruno Pacheco in def7a26
- chore(deps): update quarkus.platform.version to v3.16.1 by @Renovate Bot in 692fc68
- Feat: alphanumeric + variant filters (#133) by @Younès Adem in e9e1681
- chore: migrate services to use cases by @Bruno Pacheco in 5bc431c
- chore(deps): update quarkus-wiremock.version to v1.4.0 by @Renovate Bot in 70db3e7
- chore(deps): update aquasecurity/trivy-action action to v0.28.0 by @Renovate Bot in cd5899d
- docs: update CHANGELOG.md by @Bruno Pacheco in e64a805
- chore(deps): update aquasecurity/trivy-action action to v0.27.0 (#131) by @LNDS-Sysadmins in c609e0d
- chore(deps): update aquasecurity/trivy-action action to v0.26.0 (#129) by @LNDS-Sysadmins in 69e3d8e


### Fixed
- fix: ignore blank strings when parsing datetime values by @Bruno Pacheco in 75e338d
- fix: remove content-length from dataset in format endpoint by @Bruno Pacheco in 0e91358
- fix: replace CKAN dates by DCAT-AP dates in distributions by @Bruno Pacheco in fb398e7
- fix(deps): add missing dependency by @Bruno Pacheco in c605f64
- fix(deps): update dependency io.quarkiverse.openapi.generator:quarkus-openapi-generator to v2.5.0 by @Renovate Bot in 5ef93ec



## [v1.3.0] - 2024-10-07

### Added
* fix: add keywords to searched dataset schema by @zalborzi in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/114
* feat(ckan): use modified / issued dates from dcat metadata by @Markus92 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/108

### Changed
* chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.4-1227.1725849298 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/110
* chore: stop calling record counter if beacon is disable by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/116
* chore(deps): update quarkus.platform.version to v3.14.4 (patch) by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/111
* Update enhanced package search to post by @admy7 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/119
* chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.4-1227.1726694542 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/121
* chore: add content disposition and length in headers for binary by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/124
* chore(deps): update quarkus.platform.version to v3.15.1 (minor) by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/123
* Upgrade discovery service for ckanext-dcat 2.0 by @Markus92 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/126
* chore(deps): update surefire.version to v3.5.1 (patch) by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/127
* chore(deps): update aquasecurity/trivy-action action to v0.25.0 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/128

### Fixed
* style: fix linting issues by @Markus92 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/117
* fix: revert OffsetDateTime parsing by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/118
* Deactive keep alive header from rest client by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/120
* Revert "fix: revert OffsetDateTime parsing" by @hcvdwerf in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/122
* fix: accept date without time by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/125

### Removed
* 109 gdi dataset discovery service decommission facetgroups from datasetssearchresponse by @admy7 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/115

## [v1.2.3] - 2024-09-09

### Added
* feat: add endpoint to retrieve list of facets by @admy7 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/112

### Changed
* chore: make ckan filters configurable by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/93
* Replace "title" by "title_string" in sort string in CKAN API calls by @Markus92 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/95
* Revert sort title string by @hcvdwerf in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/97
* 32/remove logic from repository by @admy7 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/94
* Revert "32/remove logic from repository" by @zalborzi in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/99
* chore(deps): update quarkus.platform.version to v3.14.1 (minor) by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/107
* chore(deps): update surefire.version to v3.5.0 (minor) by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/103

### Fixed
* 32/fix issues by @admy7 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/102
* fix: remove fl attribute from ckan by @admy7 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/105
* fix: filter null identifiers by @admy7 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/106

## [v1.2.2] - 2024-08-21

### Added
* feat: renovate integration by @sehaartuc in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/35
* feat: trivy and ort implementation by @sehaartuc in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/34
* feat: add dataset relations and dictionary by @zalborzi in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/74

### Changed
* feat: make service agnostic on repo impl by @sulejmank in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/31
* chore(deps): update oss-review-toolkit/ort-ci-github-action digest to 81698a9 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/38
* chore(deps): update docker/login-action digest to 0d4c9c5 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/37
* chore(deps): update azure/webapps-deploy digest to 5c1d76e by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/36
* chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.4-1134 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/41
* chore(deps): update quarkus-wiremock.version to v1.3.3 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/40
* chore(deps): update dependency org.apache.maven.plugins:maven-failsafe-plugin to v3.3.1 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/45
* chore(deps): update dependency org.apache.maven.plugins:maven-compiler-plugin to v3.13.0 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/44
* fix(deps): update dependency org.projectlombok:lombok to v1.18.34 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/42
* chore(deps): update dependency org.jacoco:jacoco-maven-plugin to v0.8.12 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/39
* chore(deps): update dependency net.revelc.code.formatter:formatter-maven-plugin to v2.24.1 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/43
* chore(deps): update docker/build-push-action action to v6 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/50
* chore(deps): update fsfe/reuse-action action to v4 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/51
* chore(deps): update dependency org.apache.maven.plugins:maven-surefire-plugin to v3.3.1 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/48
* chore(deps): update dependency org.assertj:assertj-core to v3.26.3 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/49
* chore(deps): update quarkus.platform.version to v3.12.3 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/72
* chore(deps): update registry.access.redhat.com/ubi9-minimal docker tag to v9.4-1194 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/73
* fix: revert removing hasversion and conformsto by @zalborzi in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/77
* chore(deps): update quarkus.platform.version to v3.13.0 (minor) by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/78
* 75 gdi dataset discovery service retrieve organization details by @sulejmank in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/79
* chore: #76 retrieve dataset content in different formats by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/81
* Added creator field to discovery service by @Markus92 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/80
* chore: retrieve organization metadata by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/82
* chore(deps): update quarkus.platform.version to v3.13.1 (patch) by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/86
* chore(deps): update quarkus.platform.version to v3.13.2 (patch) by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/87
* chore(deps): update dependency org.apache.maven.plugins:maven-failsafe-plugin to v3.4.0 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/90
* chore(deps): update quarkus.platform.version to v3.13.3 (patch) by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/92
* chore(deps): update dependency org.apache.maven.plugins:maven-surefire-plugin to v3.4.0 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/91

### Fixed
* fix: fix contact point parsing by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/83
* Fix for contact point mapping by @Markus92 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/84
* fix(deps): update dependency io.quarkiverse.openapi.generator:quarkus-openapi-generator to v2.4.6 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/85
* fix(deps): update dependency io.quarkiverse.openapi.generator:quarkus-openapi-generator to v2.4.7 by @LNDS-Sysadmins in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/88
* fix: compatibility of CKAN creator field with new scheming by @Markus92 in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/89

## [v1.2.1] - 2024-07-02

### Fixed
* fix(mapping): correct title mapping issue closes #29 by @hcvdwerf in https://github.com/GenomicDataInfrastructure/gdi-userportal-dataset-discovery-service/pull/30

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
