package repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.browser.localStorage
import kotlinx.serialization.json.JsonObject

class ReportsRepository(
    private val httpClient: HttpClient,
//    val token: String,
) {
    val token = localStorage.getItem("jwt_token") ?: ""

    suspend fun fetchSaleReports(): List<SaleReportItem> {
        val encodedRegion = "Africa/Harare"
        return httpClient.get("$apiReportPath/sales_reports") {
//            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }

    suspend fun getTotalProfitAndSales(): Pair<Double, Double> {
        return httpClient.get("$apiReportPath/totalProfitAndSales").body()
    }

    suspend fun getTotalClientsAndSuppliers(): Pair<Int, Int> {
        return httpClient.get("$apiReportPath/totalClientsAndSuppliers").body()
    }

    suspend fun getUsersTotalSales(): List<JsonObject> {
        return httpClient.get("$apiPath/reports/usersTotalSales").body()
    }

    suspend fun getEachProductTotalProfit(): List<JsonObject> {
        return httpClient.get("$apiReportPath/eachProductTotalProfit").body()
    }

    suspend fun getTotalQuantitiesByMonthAndYear(): List<JsonObject> {
        return httpClient.get("$apiReportPath/totalQuantitiesByMonthAndYear").body()
    }

    suspend fun getProductsMostBoughts(): List<JsonObject> {
        return httpClient.get("$apiReportPath/productsMostBoughts").body()
    }

    suspend fun fetchDateTimeSales(): List<JsonObject> {
        return httpClient.get("$apiReportPath/productsMostBoughts").body()
    }


    suspend fun fetchDateTimeSales(
        initialDate: String,
        initialTime: String,
        finalDate: String,
        finalTime: String,
    ): List<SaleReportItem> {
        return httpClient.get("$apiReportPath/filteredSalesByDates/$initialDate/$initialTime/$finalDate/$finalTime?timezoneid=Africa/Harare").body()
    }
}

//filteredSalesByDates