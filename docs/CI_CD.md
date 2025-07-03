# CI/CD and Releases

This project includes comprehensive GitHub Actions workflows for automated building, versioning, and releases:

## Workflows

- **build-mcp-versioned.yml**: Full production workflow with semantic versioning, Docker builds, and GitHub releases
- **build-mcp-simple.yml**: Lightweight development workflow for feature branches

## Versioning Strategy

The project follows semantic versioning (MAJOR.MINOR.PATCH) with automatic version detection:

- **Main branch**: Auto-increment based on conventional commits
    - `feat:` → Minor version increment (1.0.0 → 1.1.0)
    - `fix:` → Patch version increment (1.0.0 → 1.0.1)
    - `BREAKING CHANGE` or `!:` → Major version increment (1.0.0 → 2.0.0)
- **Develop branch**: Pre-release versions with commit hash (e.g., 1.1.0-dev.abc1234)
- **Feature branches**: Branch-specific versions (e.g., 1.0.1-feature-auth.abc1234)
- **Tagged releases**: Use exact tag version

## Manual Releases

Use the "Build MCP Server (Versioned)" workflow dispatch to:

- Specify release type (patch, minor, major, custom)
- Override version manually
- Create GitHub releases with changelog

## Docker Images

Docker images are automatically built and published to GitHub Container Registry:

- **Release versions**: `ghcr.io/karlll/kslide/mcp-server:1.0.0`, `latest`
- **Development**: `ghcr.io/karlll/kslide/mcp-server:dev`
- **Feature branches**: `ghcr.io/karlll/kslide/mcp-server:feature-name`