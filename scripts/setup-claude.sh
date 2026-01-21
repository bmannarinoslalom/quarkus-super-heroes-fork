#!/bin/bash
#
# Setup script for Claude Code MCP configuration
# Run this script to configure MCP servers for the Quarkus Super Heroes project
#

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "🤖 Claude Code Setup for Quarkus Super Heroes"
echo "=============================================="
echo ""

# Check if .mcp.json already exists
if [ -f "$PROJECT_ROOT/.mcp.json" ]; then
  echo "⚠️  .mcp.json already exists."
  read -p "Do you want to overwrite it? (y/N) " -n 1 -r
  echo ""
  if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Keeping existing .mcp.json"
    exit 0
  fi
fi

# Copy example config
echo "📋 Copying .mcp.json.example to .mcp.json..."
cp "$PROJECT_ROOT/.mcp.json.example" "$PROJECT_ROOT/.mcp.json"

# Check for GitHub token
if [ -z "$GITHUB_TOKEN" ]; then
  echo ""
  echo "⚠️  GITHUB_TOKEN environment variable is not set."
  echo ""
  echo "To use GitHub MCP features, you need to:"
  echo "1. Create a Personal Access Token at https://github.com/settings/tokens"
  echo "   - Required scopes: repo, read:org, read:user"
  echo "2. Set the environment variable:"
  echo "   export GITHUB_TOKEN=\"your-token-here\""
  echo ""
  echo "Add this to your ~/.bashrc or ~/.zshrc to persist it."
else
  echo "✅ GITHUB_TOKEN is set"
fi

echo ""
echo "✅ MCP configuration created at .mcp.json"
echo ""
echo "📖 Next steps:"
echo "1. Open the project in Claude Code"
echo "2. Run /mcp to verify MCP servers are connected"
echo "3. For Atlassian, you'll be prompted to authenticate via browser on first use"
echo ""
echo "📚 See CLAUDE.md for full documentation on available skills and conventions."
