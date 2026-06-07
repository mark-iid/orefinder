# Orefinder Dev Container

A ready-to-code environment for the Orefinder Spigot plugin.

## What's inside

- **Java 21 (Temurin)** via the base image + `java` feature, matching the `pom.xml` compile target.
- **Maven** (latest) for building (`mvn package`) and running tests (MockBukkit + JUnit 5).
- **Node.js LTS** — required by the Claude Code CLI.
- **Claude Code CLI** via the official Anthropic dev container feature.

## VS Code extensions

| Extension | Purpose |
|-----------|---------|
| `vscjava.vscode-java-pack` | Java language support, debugger, Maven, project view, test runner, IntelliCode |
| `redhat.vscode-yaml` | Editing `plugin.yml` / `config.yml` |
| `anthropic.claude-code` | Claude Code in the editor |
| `github.vscode-github-actions` | Edit/view the Maven CI workflow |
| `eamodio.gitlens` | Git history & blame |
| `streetsidesoftware.code-spell-checker` | Catch typos |

## First run

`postCreateCommand` pre-fetches Maven dependencies offline so the first build is fast.

```bash
mvn -B package          # build + test, jar lands in target/
mvn -B package -DskipTests
```

Port **25565** is forwarded so you can drop the built jar into a Spigot/Paper
server's `plugins/` folder and connect a Minecraft client for manual testing.

## Claude Code

The CLI is installed in the container — just run `claude` in the terminal, or use
the `anthropic.claude-code` extension. Authenticate on first use.
