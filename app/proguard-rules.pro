# R8 (release) keep rules.
#
# Intentionally minimal: a blanket `-keep class com.kliq.loanapp.** { *; }` would defeat the point
# of R8. Library-specific rules ship with their dependencies (Hilt, Compose, kotlinx-coroutines),
# and the only reflection surface — Gson binding loans.json onto data.dto.LoanDto — is kept by the
# data module's own consumer-rules.pro, which R8 merges into this build. Add narrow, justified rules
# here only if a new reflection/serialization site appears.
