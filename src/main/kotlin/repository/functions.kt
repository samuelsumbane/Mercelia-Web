package repository

const val apiPath = "http://0.0.0.0:2000"
//const val apiPath = "http://192.168.48.253:2000"
const val apiCategoriesPath = "$apiPath/categories"
const val apiClientsPath = "$apiPath/clients"
const val apiProductsPath = "$apiPath/products"
const val apiReportPath = "$apiPath/reports"
const val apiSupplierPath = "$apiPath/suppliers"
const val apiStockPath = "$apiPath/stocks"
const val apiBranchesPath = "$apiPath/branches"


enum class Role(val desc: String) { // desc -> description
    V("Vendedor/Caixa"),
    A("Admin"),
    G("Gerente"),
}

enum class sysPackages(val desc: String) { // desc -> description
    L("Lite"),
    PL("Plus"),
    PO("Pro"),
}



fun moneyFormat(money: Double): String {
    return js("money.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })") as String
}