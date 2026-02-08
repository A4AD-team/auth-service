#!/bin/bash

# Conventional Commits Validation Script
# Used by Lefthook for commit-msg hook validation

MESSAGE_FILE=$1

if [ ! -f "$MESSAGE_FILE" ]; then
  echo "❌ Commit message file not found: $MESSAGE_FILE"
  exit 1
fi

# Read the first line of commit message
MESSAGE=$(head -n1 "$MESSAGE_FILE")

# Skip validation for empty messages (git will handle)
if [ -z "$MESSAGE" ]; then
  exit 0
fi

# Conventional commits pattern
# type(scope): description
PATTERN="^(feat|fix|docs|style|refactor|test|chore|perf|ci|build|revert)(\(.+\))?: .{1,50}"

if ! [[ "$MESSAGE" =~ $PATTERN ]]; then
  echo "❌ Invalid commit message format!"
  echo ""
  echo "Current message: $MESSAGE"
  echo ""
  echo "Expected format: <type>[optional scope]: <description>"
  echo ""
  echo "Types:"
  echo "  • feat     - New feature"
  echo "  • fix      - Bug fix"
  echo "  • docs     - Documentation"
  echo "  • style    - Code style (formatting, etc.)"
  echo "  • refactor - Code refactoring"
  echo "  • test     - Tests"
  echo "  • chore    - Maintenance tasks"
  echo "  • perf     - Performance improvements"
  echo "  • ci       - CI/CD changes"
  echo "  • build    - Build system changes"
  echo "  • revert   - Revert previous commit"
  echo ""
  echo "Examples:"
  echo "  • feat(auth): add JWT token validation"
  echo "  • fix: resolve memory leak in rate limiter"
  echo "  • docs(api): update authentication endpoints"
  echo "  • test(auth): add unit tests for user service"
  echo "  • refactor(auth): simplify token generation logic"
  echo ""
  echo "Rules:"
  echo "  • Type must be one of the listed types"
  echo "  • Scope is optional but recommended (use lowercase)"
  echo "  • Description must be 1-50 characters"
  echo "  • Description should be lowercase, no period at end"
  echo "  • Use imperative mood (add, fix, update, not added, fixed, updated)"
  exit 1
fi

# Extract description for additional validation
DESCRIPTION=$(echo "$MESSAGE" | sed 's/^[^(]*: //' | sed 's/^(.*): //')

# Check description length
if [ ${#DESCRIPTION} -lt 1 ] || [ ${#DESCRIPTION} -gt 50 ]; then
  echo "❌ Description must be 1-50 characters long (current: ${#DESCRIPTION})"
  echo "Current description: '$DESCRIPTION'"
  exit 1
fi

# Check if description ends with period
if [[ "$DESCRIPTION" == *"." ]]; then
  echo "❌ Description should not end with a period"
  echo "Remove the period from: '$DESCRIPTION'"
  exit 1
fi

# Check if description starts with uppercase (should be lowercase)
if [[ "$DESCRIPTION" =~ ^[A-Z] ]]; then
  echo "⚠️  Description should start with lowercase"
  echo "Current: '$DESCRIPTION'"
  echo "Consider: $(echo "$DESCRIPTION" | sed 's/./\L&/')"
fi

# Check for common issues
if [[ "$DESCRIPTION" =~ ^(added|fixed|updated|removed|deleted) ]]; then
  echo "⚠️  Use imperative mood: add, fix, update, remove, delete"
  echo "Current: '$DESCRIPTION'"
fi

# Validate scope format if present
if [[ "$MESSAGE" =~ \(([^)]+)\): ]]; then
  SCOPE="${BASH_REMATCH[1]}"
  
  # Scope should be lowercase and alphanumeric with hyphens
  if ! [[ "$SCOPE" =~ ^[a-z0-9-]+$ ]]; then
    echo "⚠️  Scope should use lowercase, numbers, and hyphens only"
    echo "Current scope: '$SCOPE'"
    echo "Consider: $(echo "$SCOPE" | tr '[:upper:]' '[:lower:]' | tr '_' '-')"
  fi
  
  # Scope length validation
  if [ ${#SCOPE} -gt 20 ]; then
    echo "⚠️  Scope should be concise (max 20 characters, current: ${#SCOPE})"
  fi
fi

echo "✅ Commit message format is valid"
echo "   $MESSAGE"

# Optional: Show suggestions for improvement
SUGGESTIONS=()

if [[ "$DESCRIPTION" =~ ^[A-Z] ]]; then
  SUGGESTIONS+=("Use lowercase for description")
fi

if [[ "$DESCRIPTION" =~ ^(added|fixed|updated|removed|deleted) ]]; then
  SUGGESTIONS+=("Use imperative mood (add, fix, update, remove)")
fi

if [ ${#SUGGESTIONS[@]} -gt 0 ]; then
  echo ""
  echo "💡 Suggestions for improvement:"
  for SUGGESTION in "${SUGGESTIONS[@]}"; do
    echo "   • $SUGGESTION"
  done
fi

exit 0