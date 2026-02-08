# Lefthook Configuration

This directory contains Lefthook Git hooks configuration for enforcing Git Flow conventions and code quality standards.

## Files

- `lefthook.yml` - Main configuration with Git Flow validation
- `lefthook-local.yml.example` - Stricter local development rules (optional)
- `.lefthook/` - Custom validation scripts

## Installation

1. Install Lefthook:
```bash
# macOS
brew install lefthook

# Or download from GitHub releases
# https://github.com/evilmartians/lefthook/releases
```

2. Install hooks:
```bash
lefthook install
```

## Git Flow Rules

### Protected Branches
Direct pushes to these branches are **blocked**:
- `main` / `master` - Production branch
- `develop` - Development integration branch  
- `release/*` - Release preparation branches
- `hotfix/*` - Production hotfix branches

### Allowed Branch Names
Branch names must follow Git Flow patterns:
- `feature/<description>` - New features
- `bugfix/<description>` - Bug fixes
- `hotfix/<description>` - Production hotfixes
- `release/<version>` - Release preparation
- `develop` - Development branch
- `main`/`master` - Production branches

### Commit Message Format
Commits must follow **Conventional Commits** format:
```
<type>[optional scope]: <description>
```

**Types:**
- `feat` - New feature
- `fix` - Bug fix
- `docs` - Documentation
- `style` - Code style (formatting, etc.)
- `refactor` - Code refactoring
- `test` - Tests
- `chore` - Maintenance tasks
- `perf` - Performance improvements
- `ci` - CI/CD changes
- `build` - Build system changes
- `revert` - Revert previous commit

**Examples:**
```
feat(auth): add JWT token validation
fix: resolve memory leak in rate limiter
docs(api): update authentication endpoints
```

## Local Development

For stricter local validation, copy the example config:
```bash
cp lefthook-local.yml.example lefthook-local.yml
```

This enables:
- Auto-formatting with Spotless
- Comprehensive test runs
- Security scanning
- Code quality checks

## Bypassing Hooks

If absolutely necessary, you can bypass hooks:
```bash
git commit --no-verify -m "message"
git push --no-verify
```

**Use with caution** - this skips all safety checks.

## Troubleshooting

### Hook not running
```bash
# Reinstall hooks
lefthook uninstall
lefthook install
```

### Script permissions
```bash
# Make scripts executable
chmod +x .lefthook/*.sh
```

### Debug mode
```bash
# Run with verbose output
LEFTHOOK_QUIET=0 git commit
```