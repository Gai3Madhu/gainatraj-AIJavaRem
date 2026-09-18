### Flaw #1: Missing @Transactional
- **AI Generated:** JDBC update without @Transactional
- **Why:** RPG implicit COMMIT
- **Impact:** Data inconsistency in prod
- **Fix:** Guardrail checker + prompt rule