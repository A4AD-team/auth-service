#!/bin/bash

# Git Flow Branch Name Validation Script
# Used by Lefthook for pre-push hook validation

CURRENT_BRANCH=$(git symbolic-ref --short HEAD 2>/dev/null)

if [ -z "$CURRENT_BRANCH" ]; then
  echo "❌ Not on any branch (detached HEAD state)"
  exit 1
fi

# Protected branches that should never be pushed to directly
PROTECTED_BRANCHES=(
  "main"
  "master" 
  "develop"
)

# Check if current branch is protected
for PROTECTED in "${PROTECTED_BRANCHES[@]}"; do
  if [ "$CURRENT_BRANCH" = "$PROTECTED" ]; then
    echo "❌ Cannot push directly to protected branch: '$CURRENT_BRANCH'"
    echo ""
    echo "Protected branches require pull requests:"
    echo "  • Create a feature branch: git checkout -b feature/your-feature"
    echo "  • Make changes and commit"
    echo "  • Push feature branch and create PR"
    echo "  • Merge via pull request"
    exit 1
  fi
done

# Check if branch matches protected patterns
if [[ "$CURRENT_BRANCH" =~ ^(release/|hotfix/).+ ]]; then
  echo "❌ Cannot push directly to protected branch pattern: '$CURRENT_BRANCH'"
  echo ""
  echo "Release and hotfix branches require pull requests for proper review"
  exit 1
fi

# Allowed branch patterns for Git Flow
PATTERNS=(
  "^feature/.+"
  "^bugfix/.+" 
  "^hotfix/.+"
  "^release/.+"
  "^develop$"
  "^main$"
  "^master$"
)

VALID=false
for PATTERN in "${PATTERNS[@]}"; do
  if [[ "$CURRENT_BRANCH" =~ $PATTERN ]]; then
    VALID=true
    break
  fi
done

if [ "$VALID" = false ]; then
  echo "❌ Invalid branch name: '$CURRENT_BRANCH'"
  echo ""
  echo "Branch names must follow Git Flow patterns:"
  echo "  • feature/<description>     - New features"
  echo "  • bugfix/<description>      - Bug fixes"
  echo "  • hotfix/<description>      - Production hotfixes"
  echo "  • release/<version>        - Release preparation"
  echo "  • develop                   - Development integration"
  echo "  • main/master               - Production branches"
  echo ""
  echo "Examples:"
  echo "  • feature/jwt-authentication"
  echo "  • feature/user-registration"
  echo "  • bugfix/memory-leak-fix"
  echo "  • bugfix/login-validation"
  echo "  • hotfix/security-patch"
  echo "  • release/v1.2.0"
  echo ""
  echo "To rename current branch:"
  echo "  git branch -m $CURRENT_BRANCH feature/your-feature-name"
  exit 1
fi

# Additional validation for specific branch types
case "$CURRENT_BRANCH" in
  feature/*)
    FEATURE_NAME=$(echo "$CURRENT_BRANCH" | sed 's/^feature\///')
    if [ ${#FEATURE_NAME} -lt 3 ]; then
      echo "❌ Feature name too short (minimum 3 characters)"
      exit 1
    fi
    if [[ "$FEATURE_NAME" =~ [A-Z] ]]; then
      echo "⚠️  Feature name should use lowercase and hyphens"
      echo "   Consider: feature/$(echo "$FEATURE_NAME" | tr '[:upper:]' '[:lower:]' | tr '_' '-')"
    fi
    ;;
    
  bugfix/*)
    BUGFIX_NAME=$(echo "$CURRENT_BRANCH" | sed 's/^bugfix\///')
    if [ ${#BUGFIX_NAME} -lt 3 ]; then
      echo "❌ Bugfix name too short (minimum 3 characters)"
      exit 1
    fi
    ;;
    
  release/*)
    VERSION=$(echo "$CURRENT_BRANCH" | sed 's/^release\///')
    if ! [[ "$VERSION" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
      echo "❌ Release version must follow semantic versioning: v1.2.3"
      exit 1
    fi
    ;;
    
  hotfix/*)
    HOTFIX_NAME=$(echo "$CURRENT_BRANCH" | sed 's/^hotfix\///')
    if [ ${#HOTFIX_NAME} -lt 3 ]; then
      echo "❌ Hotfix name too short (minimum 3 characters)"
      exit 1
    fi
    ;;
esac

echo "✅ Branch name '$CURRENT_BRANCH' follows Git Flow conventions"
exit 0