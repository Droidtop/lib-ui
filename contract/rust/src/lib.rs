//! The backend contract (`contract/README.md`): every answer is `ok`, `unavailable`,
//! `error` or `pending`, as one JSON object with a `status` field.
//!
//! An operation table builds its answers with [`ok`], [`unavailable`], [`error`] and
//! [`pending`], and runs each operation inside [`guard`], so a panic becomes an `error`
//! answer instead of tearing down the process (across JNI that would abort the app).
//! A caller reads what it got with [`decode`].

use serde_json::{json, Map, Value};
use std::panic::{catch_unwind, AssertUnwindSafe};

pub const NO_ANSWER: &str = "There was no answer.";
pub const MALFORMED: &str = "The answer could not be read.";
const STATUSES: [&str; 4] = ["ok", "unavailable", "error", "pending"];
const OWN: [&str; 6] = ["status", "slot", "message", "title", "code", "request"];

/// An `ok` answer: the operation's own `fields` beside `status`.
pub fn ok(slot: Option<&str>, fields: Map<String, Value>) -> Value {
    let mut answer = fields;
    answer.insert("status".into(), "ok".into());
    if let Some(slot) = slot {
        answer.insert("slot".into(), slot.into());
    }
    Value::Object(answer)
}

/// No backend for this here; `message` says why, in words a screen can show.
pub fn unavailable(message: &str, slot: Option<&str>, title: Option<&str>) -> Value {
    let mut answer = own("unavailable", slot);
    answer.insert("message".into(), message.into());
    if let Some(title) = title {
        answer.insert("title".into(), title.into());
    }
    Value::Object(answer)
}

/// It was tried and failed; `message` is the reason in plain words.
pub fn error(message: &str, slot: Option<&str>, code: Option<&str>) -> Value {
    let mut answer = own("error", slot);
    answer.insert("message".into(), message.into());
    if let Some(code) = code {
        answer.insert("code".into(), code.into());
    }
    Value::Object(answer)
}

/// The answer comes later, settled by an answer that carries the same `request`.
pub fn pending(request: &str, slot: Option<&str>) -> Value {
    let mut answer = own("pending", slot);
    answer.insert("request".into(), request.into());
    Value::Object(answer)
}

fn own(status: &str, slot: Option<&str>) -> Map<String, Value> {
    let mut answer = Map::new();
    answer.insert("status".into(), status.into());
    if let Some(slot) = slot {
        answer.insert("slot".into(), slot.into());
    }
    answer
}

/// Runs one operation. A panic becomes an `error` answer naming the operation
/// (contract rule 1), and `log` is told once.
pub fn guard(slot: &str, log: impl FnOnce(&str, &str), work: impl FnOnce() -> Value) -> Value {
    match catch_unwind(AssertUnwindSafe(work)) {
        Ok(answer) => answer,
        Err(panic) => {
            let reason = panic
                .downcast_ref::<&str>()
                .map(|s| s.to_string())
                .or_else(|| panic.downcast_ref::<String>().cloned())
                .unwrap_or_else(|| "it stopped unexpectedly".into());
            log(slot, &reason);
            error(&format!("{slot} stopped unexpectedly."), Some(slot), Some("panic"))
        }
    }
}

/// The one reading of what a backend sent: `status`, the contract's own fields when
/// present, and every other field under `fields`. Anything that is not a well-formed
/// answer reads as an `error`; a bare list is the legacy `ok` with the list under
/// `fields.value`.
pub fn decode(value: &Value) -> Value {
    let object = match value {
        Value::Null => return json!({"status": "error", "message": NO_ANSWER, "code": "no_answer", "fields": {}}),
        Value::Array(_) => return json!({"status": "ok", "fields": {"value": value}}),
        Value::Object(object) => object,
        _ => return malformed(),
    };
    let status = match object.get("status").and_then(Value::as_str) {
        Some(s) if STATUSES.contains(&s) => s,
        _ => return malformed(),
    };
    if OWN[1..].iter().any(|name| object.get(*name).is_some_and(|v| !v.is_string())) {
        return malformed();
    }
    let has = |name: &str| object.get(name).and_then(Value::as_str).is_some_and(|s| !s.is_empty());
    if matches!(status, "unavailable" | "error") && !has("message") {
        return malformed();
    }
    if status == "pending" && !has("request") {
        return malformed();
    }
    let mut decoded = Map::new();
    let mut fields = Map::new();
    for (key, v) in object {
        if OWN.contains(&key.as_str()) {
            decoded.insert(key.clone(), v.clone());
        } else {
            fields.insert(key.clone(), v.clone());
        }
    }
    decoded.insert("fields".into(), Value::Object(fields));
    Value::Object(decoded)
}

fn malformed() -> Value {
    json!({"status": "error", "message": MALFORMED, "code": "malformed", "fields": {}})
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn every_fixture_decodes_as_written() {
        let text = include_str!("../../fixtures/answers.json");
        let fixtures: Value = serde_json::from_str(text).unwrap();
        for case in fixtures["cases"].as_array().unwrap() {
            assert_eq!(decode(&case["input"]), case["decoded"], "{}", case["name"]);
        }
    }

    #[test]
    fn builders_decode_to_themselves() {
        let mut fields = Map::new();
        fields.insert("games".into(), json!([]));
        for answer in [
            ok(Some("games"), fields),
            unavailable("Not built yet.", Some("x"), Some("T")),
            error("Unreachable.", None, Some("unreachable")),
            pending("x-1", Some("x")),
        ] {
            assert_eq!(decode(&answer)["status"], answer["status"]);
            assert_ne!(decode(&answer)["code"], "malformed");
        }
    }

    #[test]
    fn a_panic_becomes_an_error_answer() {
        let mut logged = None;
        let answer = guard("boom", |slot, reason| logged = Some(format!("{slot}: {reason}")), || panic!("no disk"));
        assert_eq!(answer["status"], "error");
        assert_eq!(answer["code"], "panic");
        assert_eq!(logged.as_deref(), Some("boom: no disk"));
    }
}
