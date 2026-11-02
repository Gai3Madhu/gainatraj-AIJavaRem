# IBM i to Java Modernization - AI Guardrails

AI generates Java but misses IBM i specifics. This project detects 2 real flaws.

**Flaws detected:**
- Missing COMMIT/ROLLBACK boundaries
- BigDecimal precision loss from RPG packed decimals

**How:** Java 17, Gemini API (HttpClient), Guardrail pattern

** To Run:**
export GOOGLE_API_KEY=xxx
mvn compile exec:java -Dexec.mainClass="com.gai3madhu.guardrails.GuardrailEngine"

Why this matters: Most AI converters don't understand *INLR, S/36 indicators. Guardrails prevent prod bugs.
