# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [v1.0.1]
### Added
- Command 'reload' to enable reloading of files without a restart.

### Fixed
- Protect against adding empty string when processing player add lists.


## [v1.0.0]
### Changed
- Change messages to allow for choosing a random message out of multiple.

## [v0.9.1]
### Added
- Config option `send_message` allowing to send a message to the player or entire if the definition was processed for the user.

### Changed:
- Exclude untestable classes from coverage report
- Edit the CHANGELOG for readability and better representation of changes

### Fixed
- Release list in this CHANGELOG

## [v0.9.0]
### Changed
- Use maven shade plugin to control dependencies and trim jar size
- Cleanup and prepare for stable release.

### Added
- Add tests and validation


[Unreleased]: https://github.com/mooeypoo/PlayingWithTime/compare/v1.0.1...HEAD
[v1.0.1]: https://github.com/mooeypoo/PlayingWithTime/compare/v1.0.0...v1.0.1
[v1.0.0]: https://github.com/mooeypoo/PlayingWithTime/compare/v0.9.1...v1.0.0
[v0.9.1]: https://github.com/mooeypoo/PlayingWithTime/compare/v0.9.0...v0.9.1
[v0.9.0]: https://github.com/mooeypoo/PlayingWithTime/compare/v0.0.1...v0.9.0
