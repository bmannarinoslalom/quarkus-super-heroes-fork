Send a Slack notification about a pull request to the #pull-requests channel.

## Input

`$ARGUMENTS` can be:
- PR number (e.g., `5`)
- Full GitHub PR URL (e.g., `https://github.com/owner/repo/pull/5`)

## Steps

1. **Parse PR identifier** - Extract PR number from arguments
2. **Fetch PR details** using GitHub MCP:
   - `get_pull_request` - PR metadata, title, description, author, state
3. **Format the notification** based on PR state:
   - New/Open PR: Announce for review
   - Merged PR: Celebrate the merge
   - Closed PR: Note the closure
4. **Send to Slack** using the Slack MCP `slack_send_message` tool

## Message Format

### For Open PRs (requesting review)
```
:eyes: *New PR Ready for Review*

*<{PR_URL}|#{PR_NUMBER}: {PR_TITLE}>*
Author: {author}
Branch: `{head}` → `{base}`

{first 200 chars of description...}

:point_right: <{PR_URL}|Review this PR>
```

### For Merged PRs
```
:tada: *PR Merged!*

*<{PR_URL}|#{PR_NUMBER}: {PR_TITLE}>*
Author: {author}
Merged by: {merged_by}

:rocket: Changes are now in `{base}`
```

### For Closed PRs (not merged)
```
:no_entry: *PR Closed*

*<{PR_URL}|#{PR_NUMBER}: {PR_TITLE}>*
Author: {author}

This PR was closed without merging.
```

## Channel Configuration

Send notifications to `#pull-requests` channel (ID: `C0AA38NT7BL`).

If the channel ID is not found or the bot lacks permissions:
1. List available channels using `slack_list_channels`
2. Find a channel named `pull-requests` or similar
3. Inform the user if the channel is not accessible

## Prerequisites

- Slack MCP server must be configured and connected
- Bot must be invited to the target channel
- `SLACK_BOT_TOKEN` environment variable must be set

## Error Handling

If Slack send fails:
- Check if Slack MCP is connected (`/mcp`)
- Verify bot is in the channel
- Report the error to the user with troubleshooting steps

## Example Usage

```
/notify-pr 5
/notify-pr https://github.com/quarkusio/quarkus-super-heroes/pull/123
```
