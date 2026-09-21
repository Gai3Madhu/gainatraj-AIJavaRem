### Flaw #02: Implied Decimal Scaling in Cross-Slice Communication

- **Scenario:** Slice3 (Java, takes `1.15`) -> calls Slice1 (AS400 RPG, validates 2 decimal places)
- **AI Generated (Flawed):** Directly passes `BigDecimal 1.15` or rounded `1`. Slice3 is old AS400 wrapper, strips decimal, sends `1` as `001`.
- **Why Slice1 Fails:** Slice1 expects fixed-length 6,3 (last 3 digits = decimal). It sees `001` and thinks user typed 3 decimals for currency, returns false.
- **Why AI Does It:** LLMs trained on modern Java microservices assume explicit decimals everywhere. No training data on RPG implied decimal `*ZONED 6 3` / `*PACKED` pattern.
- **Impact:** Valid currency amounts like `1.15` rejected as invalid. Production payment failure.
- **Real Fix You Used:** `amount.movePointRight(3)` -> `1150` only for Slice1 validation call, to satisfy legacy contract while preserving architecture.
- **Guardrail Rule:** Any Java -> AS400 RPG call with currency must check `scale`. If RPG scale is 3, Java must `movePointRight(3)` before call.
- **Interview Story:** "In cross-slice, I preserved legacy by scaling input only for validation, not mutating business value."