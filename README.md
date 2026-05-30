# zen_export — Java / Jexer

Selective Zen Browser profile exporter with an xterm-256 TUI built on
[Jexer](https://jexer.sourceforge.io/) and Gradle.

Just a personal test project for jexer usage test.

## Keybindings

| Key | Action |
|-----|--------|
| `↑` `↓` | move cursor |
| `Enter` | confirm / proceed |
| `Space` | toggle checkbox |
| `a` | select all |
| `n` | deselect all |
| `←` | go back |
| `r` | new export (on done screen) |
| `q` | quit |

## Profile locations searched

| OS | Path |
|----|------|
| Linux | `~/.zen`, `~/.config/zen` |
| Linux Flatpak | `~/.var/app/app.zen_browser.zen/zen` |
| macOS | `~/Library/Application Support/zen/Profiles` |
| Windows | `%APPDATA%\zen\Profiles` |
