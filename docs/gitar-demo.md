# Gitar Demo: Catching What SonarQube Misses

## The thesis

SonarQube enforces rules: injection sinks, hotspots, complexity thresholds, style smells.
It is very good at detecting *how* code is written. It cannot detect *whether the code does
what the documentation says it does*.

Gitar reviews code against its own stated intent — Javadoc, Swagger `@Operation` / `@Parameter`
annotations, inline comments. When the narrative and the implementation diverge, Gitar flags it.
SonarQube cannot, because no rule exists for "this Javadoc is lying."

This file documents four such bugs seeded in the Audit Export feature. Every one of them compiles,
passes all tests, raises zero SonarQube issues, and is wrong.

---

## Expected outcome

| Tool | Result |
|------|--------|
| SonarQube Quality Gate | **PASS** — 0 new issues on this PR |
| Gitar AI review | **4 findings** — one per seeded bug below |

---

## Bug table

| # | Name | Stated intent | Actual behavior | Why Sonar misses it |
|---|------|--------------|-----------------|---------------------|
| 1 | Non-deterministic pagination | Javadoc says "stable chronological order (timestamp DESC / orderDate DESC)" | `boundPageable` passes `pageable.getSort()` unchanged; controller default has no sort → no ORDER BY → rows repeat / skip across pages | No rule checks whether a sort claim in a Javadoc matches the Pageable construction |
| 2 | Inclusive-range off-by-one | `@Operation` says "dates are inclusive in UTC" | `activityLogSpec` uses `cb.lessThan` (exclusive) for the `to` bound → records at the exact boundary instant are dropped | No rule compares predicate operator choice to a prose description |
| 3 | Case-sensitive filter | `@Parameter` says "matched case-insensitively (e.g. 'login' matches 'LOGIN')" | Service uses `cb.equal(root.get("action"), action)` — exact-match, case-sensitive → `action=login` returns empty when only `LOGIN` rows exist | No rule detects that a case-insensitivity claim requires a `lower()` / `ILIKE` predicate |
| 4 | PII export vs minimization intent | Javadoc says "Export applies data minimization: no PII (IP addresses, emails) is included." | `toActivityLogDto` maps `e.getIpAddress()` directly into the response DTO | No rule matches a "data minimization" claim in a comment against which fields are included in a record constructor |

---

## Exact seed locations

All line numbers are approximate and may shift ±5 lines from editing.

| # | File | Approx. line | What to look for |
|---|------|-------------|-----------------|
| 1 | `backend/src/main/java/com/sonarshowcase/service/AuditExportService.java` | ~35 (Javadoc) / ~70 (comment + `boundPageable` body) | Javadoc claims "timestamp DESC"; `boundPageable` never applies a default sort |
| 2 | `backend/src/main/java/com/sonarshowcase/service/AuditExportService.java` | ~92 (`activityLogSpec`, `to` predicate) | `cb.lessThan` vs `@Operation` "dates are inclusive" |
| 3 | `backend/src/main/java/com/sonarshowcase/controller/AuditExportController.java` | ~53 (`action` param) and ~77 (`status` param) | `@Parameter` "case-insensitively" vs service `cb.equal` |
| 4 | `backend/src/main/java/com/sonarshowcase/service/AuditExportService.java` | ~119 (Javadoc above `toActivityLogDto`) | "no PII" claim vs `e.getIpAddress()` in DTO constructor |

---

## Repro steps

Prerequisites: app running locally, seed data present.

```bash
BASE=http://localhost:8080/api/v1/audit
KEY="X-Admin-Key: demo-audit-admin-key"
```

### Bug 1 — Non-deterministic pagination

Iterate pages without a sort parameter and observe inconsistent ordering:

```bash
# Page 0
curl -s -H "$KEY" "$BASE/activity-logs?page=0&size=5" | jq '[.content[].id]'

# Page 1 — with enough data some IDs from page 0 may reappear
curl -s -H "$KEY" "$BASE/activity-logs?page=1&size=5" | jq '[.content[].id]'
```

**Fix signal:** Pass `sort=timestamp,desc` to get a stable order — proving no default is applied.

### Bug 2 — Inclusive-range off-by-one

Insert a record with timestamp exactly at midnight on 2026-01-01, then query with `to` equal to that instant:

```bash
# Expect: the record appears (doc says inclusive). Actual: empty (exclusive boundary).
curl -s -H "$KEY" \
  "$BASE/activity-logs?from=2026-01-01T00:00:00Z&to=2026-01-01T00:00:00Z" \
  | jq '.totalElements'
# Returns 0 — the record is excluded despite "to" matching its timestamp exactly.
```

### Bug 3 — Case-sensitive filter

```bash
# Swagger docs say case-insensitive. Try lowercase while data has uppercase.
curl -s -H "$KEY" "$BASE/activity-logs?action=login" | jq '.totalElements'
# Returns 0 even when LOGIN records exist.

curl -s -H "$KEY" "$BASE/activity-logs?action=LOGIN" | jq '.totalElements'
# Returns the expected count — proving exact-match case sensitivity.
```

### Bug 4 — PII in response despite minimization claim

```bash
# Javadoc says no PII. Check the response for ipAddress.
curl -s -H "$KEY" "$BASE/activity-logs?page=0&size=1" \
  | jq '.content[0].ipAddress'
# Returns an actual IP address string — PII is present in every row.
```
