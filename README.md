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
- `tools/runner/bin/start-local-runner.ps1` is the Windows entry point for the local runner daemon, with automatic Windows legacy fallback when WSL startup fails
- invoke the PowerShell script directly from IntelliJ or a terminal to avoid the usual batch Ctrl+C confirmation in IDE terminals
- `tools/runner/bin/start-local-runner.sh` starts the Node.js local runner inside WSL and keeps a Java `Dispatcher` process resident
- the local runner stores compile artifacts under `/dev/shm/atcoder-local-runner` to reduce I/O latency
- browser userscripts are developed under `tools/userscripts/<Name>/src/main.ts` and built into `tools/userscripts/<Name>/dist/<Name>.user.js` with Bun (`cd tools/userscripts && bun run build`); `AtCoder_Scripts/` is kept as a legacy distribution path whose `@updateURL`/`@downloadURL` redirect Tampermonkey to the new `dist` location
- CLI submission and local runner tooling is under `tools/runner`; shared TypeScript modules (AtCoder URL parsing, types) live under `tools/shared`
