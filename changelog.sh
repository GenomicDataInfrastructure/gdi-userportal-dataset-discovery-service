# SPDX-FileCopyrightText: 2024 PNED G.I.E.
#
# SPDX-License-Identifier: Apache-2.0

#!/bin/bash

# Ensure GH CLI is authenticated
if ! gh auth status &>/dev/null; then
  echo "❌ GitHub CLI is not authenticated. Run: gh auth login"
  exit 1
fi

# File where the changelog will be updated
CHANGELOG_FILE="CHANGELOG.md"

# Initialize CHANGELOG if it does not exist
if [ ! -f "$CHANGELOG_FILE" ]; then
  echo -e "# Changelog\n\nAll notable changes to this project will be documented in this file.\n\nThe format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).\n" > "$CHANGELOG_FILE"
fi

git fetch --prune origin "+refs/tags/*:refs/tags/*"
git fetch --prune --tags

git pull

# Get all tags sorted by date (latest first)
TAGS=($(git tag --sort=-creatordate))

# Get latest and previous tag
LATEST_TAG=${TAGS[1]}
PREVIOUS_TAG=${TAGS[2]}

echo "📢 Generating changelog for $LATEST_TAG (since $PREVIOUS_TAG)..."

# Get commit history between the two tags
COMMITS=$(git log "$PREVIOUS_TAG".."$LATEST_TAG" --no-merges --pretty=format:"%s by @%an in %h")

# Categorize changes
ADDED=""
CHANGED=""
FIXED=""
REMOVED=""
SECURITY=""

while IFS= read -r line; do
  case "$line" in
    *feat*|*implement*) ADDED+="- ${line}\n" ;;
    *fix*|*bug*|*resolve*|*patch*) FIXED+="- ${line}\n" ;;
    *update*|*modify*|*change*|*refactor*|*chore*) CHANGED+="- ${line}\n" ;;
    *decommission*|*delete*|*remove*) REMOVED+="- ${line}\n" ;;
    *security*|*vuln*|*"patch security"*) SECURITY+="- ${line}\n" ;;
    *) CHANGED+="- ${line}\n" ;;  # Default to "Changed"
  esac
done <<< "$COMMITS"

touch temp_changelog.md
# Format the changelog entry
{
  echo -e "## [$LATEST_TAG] - $(date +'%Y-%m-%d')\n"
  [[ -n "$ADDED" ]] && echo -e "### Added\n$ADDED\n"
  [[ -n "$CHANGED" ]] && echo -e "### Changed\n$CHANGED\n"
  [[ -n "$FIXED" ]] && echo -e "### Fixed\n$FIXED\n"
  [[ -n "$REMOVED" ]] && echo -e "### Removed\n$REMOVED\n"
  [[ -n "$SECURITY" ]] && echo -e "### Security\n$SECURITY\n"
  echo ""
} >> temp_changelog.md

# Append existing changelog content
sed -i '' '14r temp_changelog.md' "$CHANGELOG_FILE"
# Clean up temporary file
rm temp_changelog.md

# Commit and push the updated CHANGELOG.md
git add "$CHANGELOG_FILE"
git commit -m "📜 Update CHANGELOG.md for $LATEST_TAG"
git push origin main

echo "✅ CHANGELOG.md updated successfully!"
