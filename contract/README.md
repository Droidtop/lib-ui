# The backend contract

Every call a UI makes to its backend gets one answer, and the answer always says which of four things
happened. One wire format serves every program and every transport (the owner, Droidtop/tracker#867 D8:
"the UI reaches its backend through one contract (`ok`, `unavailable`, `error`, `pending`), a local socket
on desktop and in-process calls on Android").

This is Pythia's contract as its web UI and backend already use it (`pythia/ui/backend.py`, docstring and
`_guarded`, `_unavailable`, `_answer_later`; `pythia/ui/desktop/web/app.js` `call`). It is written down
here so droidtop, Enginehost and the agent answer the same way. It is not a second design.

## An answer

An answer is one JSON object with a `status` field:

| `status` | Means | Other fields |
|---|---|---|
| `ok` | The operation ran. | The operation's own fields sit beside `status`. |
| `unavailable` | There is no backend for this here: not built yet, or not on this platform (an Android-only operation on a PC). | `message` (required): the reason in plain words a screen can show. `title` (optional): a heading when the reason is not "not built yet". |
| `error` | It was tried and failed. | `message` (required): the reason in plain words. `code` (optional): a stable word for programs, such as `unreachable`. |
| `pending` | The work touches the disk or the network, so it runs off the caller's thread. The answer comes later. | `request` (required): an id unique among this backend's pending requests. |

Fields every answer may carry:
- `slot`: the operation's name, for logs and the error console.
- `request`: on a settled answer, the id of the `pending` answer it settles.

The rules:
1. **A caller never sees a raw failure.** An exception, a Rust panic (caught with `catch_unwind`), a missing
   operation, no answer at all and an answer that is not an object with a known `status` all become an
   `error`, logged once on the backend's side.
2. **`pending` is settled exactly once**, by an answer whose `request` is the pending id and whose `status` is
   `ok`, `unavailable` or `error`, never `pending` again. How it is delivered depends on the transport
   (a signal, a callback, a message on the socket). The caller's `call` resolves to the settled answer, so
   code above it never handles `pending` itself.
3. **Nothing is mocked.** An operation with no backend answers `unavailable` with its reason; it never
   answers `ok` with sample data (Pythia, `pythia/ui/desktop/web/README.md`: "nothing is sampled, mocked
   or invented").
4. **`message` is for people.** It is written to be shown as it is. It carries no stack trace and no
   unredacted path or credential.
5. **Unknown fields are kept and ignored**, so an older caller reads a newer backend.

Legacy: Pythia's oldest slots answer with a bare list (`get_all_plugins`, `get_downloader_plugins`). Pythia's
`call` treats those as `ok`, and this library's `decode` does the same, as `ok` with the list under `value`.
New operations always answer an object.

## Transports

- **Android:** an in-process call into the program's operation table, off the main thread.
- **Desktop:** a local socket (a Unix socket, or a named pipe on Windows) with a per-install token. Never TCP.
- **The test UI:** an in-process bridge in a local webview. No server and no listening port (#867 D9).

## Implementations

All four are tested against the same cases in [`fixtures/answers.json`](fixtures/answers.json).

| Language | Where | Used by |
|---|---|---|
| Kotlin (plain JVM) | [`kotlin/`](kotlin), package `dev.droidtop.lib.ui.contract` | droidtop, Enginehost; the Compose UI |
| Rust | [`rust/`](rust), crate `ui-contract` | the scaffolds' operation tables (droidtop, Enginehost, Pythia, the agent) |
| Python | [`python/ui_contract.py`](python/ui_contract.py) | Pythia's backend |
| JavaScript | [`js/contract.mjs`](js/contract.mjs) | the HTML test UI (Pythia's web UI) |
