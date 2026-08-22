# Turp 0.24.27

## Complete Turkish UI coverage

- Completes Turkish localization across the model picker, update controls, developer/performance settings, backup and transfer flows, local execution, image generation, generated-content repair, and other Turp-owned UI copy found by a source-wide audit.
- Translates runtime-formatted model counts and capability summaries such as context/output limits, Thinking, Tools, queue counts, update repository/status text, provider/model metadata, package/runtime progress, and performance diagnostics while preserving model names, provider names, URLs, file names, code, and user/model content verbatim.
- Makes static missing copy resource-backed in Android English/Turkish locale resources instead of expanding the legacy exact-string translation maps.
- Normalizes leading/trailing layout whitespace before localization, fixing icon-prefixed controls such as “Check for updates” without duplicating translations.
- Extends the locale layer to accessibility semantics used by model selection, image generation, search, and generated-content repair.
- Adds resource-parity and dynamic-format regression tests, including the model-picker and update strings reported from 0.24.26.
