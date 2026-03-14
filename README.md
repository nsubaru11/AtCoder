# atcoder

## commit message

- Solved: ABC000 [A ~ G]
- Resolved: ABC000 [A ~ G]
- Update: JDK17→24.0.2 ABC000 [A ~ G]

## notes

- site: https://atcoder.jp/
- profile: https://atcoder.jp/users/nsubaru
- template entry points: `TemplateCode.java` / `TemplateCode17.java` sit at the repo root for quick starts
- problem folders under `ABC000~` et al. keep each contest isolated in its `src` directory
- `tools/atcoder-cli-runner/bin/start-local-runner.ps1` is the Windows entry point for the local runner daemon, with automatic Windows legacy fallback when WSL startup fails
- invoke the PowerShell script directly from IntelliJ or a terminal to avoid the usual batch Ctrl+C confirmation in IDE terminals
- `tools/atcoder-cli-runner/bin/start-local-runner.sh` starts the Node.js local runner inside WSL and keeps a Java `Dispatcher` process resident
- the local runner stores compile artifacts under `/dev/shm/atcoder-local-runner` to reduce I/O latency
- `AtCoder_Scripts` is kept for browser userscripts; CLI and local runner tooling is under `tools/atcoder-cli-runner`
