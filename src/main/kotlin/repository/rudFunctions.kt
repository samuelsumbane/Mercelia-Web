package repository

fun formatToTwoDecimalPlaces(value: Double): Double {
    val formattedString = value.asDynamic().toFixed(2) as String
    return formattedString.toDouble()
}

fun numberToStringMonth(number: String):String {
    return when (number) {
        "1" -> "Janeiro"
        "2" -> "Fevereiro"
        "3" -> "Março"
        "4" -> "Abril"
        "5" -> "Maio"
        "6" -> "Junho"
        "7" -> "Julho"
        "8" -> "Agosto"
        "9" -> "Setembro"
        "10" -> "Outubro"
        "11" -> "Novembro"
        "12" -> "Dezembro"
        else -> "Mês não encontrado"
    }
}