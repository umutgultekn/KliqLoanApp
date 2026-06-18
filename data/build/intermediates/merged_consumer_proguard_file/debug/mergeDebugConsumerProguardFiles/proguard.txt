# Gson binds JSON to LoanDto via reflection. @SerializedName protects field names under R8, and this
# rule keeps the DTO classes and their members so release builds parse loans.json correctly.
-keep class com.kliq.loanapp.data.dto.** { *; }
