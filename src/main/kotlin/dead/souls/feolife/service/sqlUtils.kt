package dead.souls.feolife.service

infix fun String.containsAnyOf(strings: List<String>) =
    strings.joinToString(prefix = "(", separator = " OR ", postfix = ")") { "$this ILIKE '%$it%'" }

fun given(condition: () -> Boolean, operandProvider: () -> String) =
    if (condition()) operandProvider() else ""

fun String.and(given: () -> Boolean, rightOperandProvider: () -> String): String {
    val condition = given()
    return when {
        condition && this.isNotBlank() -> "($this) AND (${rightOperandProvider()})"
        condition && this.isBlank() -> rightOperandProvider()
        else -> this
    }
}

infix fun String.or(rightOperand: String) = "$this OR $rightOperand"
