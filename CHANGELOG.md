# Coco/R Plugin for IntelliJ IDEA Changelog

## [1.5.0]
### Compatibility
- Ensure compatibility with 2020.3

### Development
- Adapt build to be based on [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
- Update gradle wrapper to 6.8
- Update kotlin to 1.4.21
- Introduce ktlint and detekt
- Utilize GitHub Actions for build/release
- Update to java 11

### Fixed
- Correctly handle EOF in within 'arbitrary code' sections (applied from v1.2.1/v1.3.1/v1.4.1 - it wasn't applied on the main branch before)

## [1.4.1]
### Fixed
- Correctly handle EOF in within 'arbitrary code' sections (applied from v1.2.1/v1.3.1)


## [1.4.0]
### Changed
- Development - Replaced all references from 'Scheinecker' to 'Salzinger' (except plugin id)

### Fixed
- Compatibility with 2020.1 EAP