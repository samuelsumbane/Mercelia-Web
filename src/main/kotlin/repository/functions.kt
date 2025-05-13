package repository
import kotlinx.datetime.*

const val apiPath = "http://0.0.0.0:2000"
//const val apiPath = "http://192.168.48.253:2000"
const val apiCategoriesPath = "$apiPath/categories"
const val apiOwnersPath = "$apiPath/owners"
const val apiClientsPath = "$apiPath/clients"
const val apiProductsPath = "$apiPath/products"
const val apiReportPath = "$apiPath/reports"
const val apiSupplierPath = "$apiPath/suppliers"
const val apiStockPath = "$apiPath/stocks"
const val apiBranchesPath = "$apiPath/branches"
const val apiPayablesPath = "$apiPath/payables"
const val apiReceivablesPath = "$apiPath/receivables"
const val apiNotificationsPath = "$apiPath/notifications"


enum class Role(val desc: String) { // desc -> description
    V("Vendedor/Caixa"),
    A("Administrador"),
    G("Gerente"),
}

enum class SysPackages(val desc: String) { // desc -> description
    L("Lite"),
    PL("Plus"),
    PO("Pro"),
}


fun getUserLocalDateTime(): LocalDateTime {
    val now = Clock.System.now()
    val timeZone = TimeZone.currentSystemDefault() // use browser timezone
    return now.toLocalDateTime(timeZone)
}


fun getUserLocalDateString(): String {
    val localDateTime = getUserLocalDateTime()
    return "${localDateTime.dayOfMonth.toString().padStart(2, '0')}." +
            "${localDateTime.monthNumber.toString().padStart(2, '0')}." +
            localDateTime.year.toString().padStart(4, '0')
}

fun getUserLocalDateTimeString(): String {
    val localDateTime = getUserLocalDateTime()
    return "${localDateTime.dayOfMonth.toString().padStart(2, '0')}." +
            "${localDateTime.monthNumber.toString().padStart(2, '0')}." +
            "${localDateTime.year.toString().padStart(4, '0')} " +
            "${localDateTime.hour.toString().padStart(2, '0')}:" +
            localDateTime.minute.toString().padStart(2, '0')
}



fun moneyFormat(money: Double): String {
    return js("money.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })") as String
}


fun String.cut(limit: Int): String {
    return if (length > limit) take(limit) + " ..." else this
}

