package repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.browser.localStorage
import kotlinx.browser.sessionStorage


class StockRepository(private val httpClient: HttpClient) {

    private val token = sessionStorage.getItem("jwt_token") ?: ""

    suspend fun getAllStock(): List<StockItem> {
        return httpClient.get("$apiStockPath/all-stocks") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }

    suspend fun fetchDateTimeStocks(
        initialDate: String,
        initialTime: String,
        finalDate: String,
        finalTime: String,
    ): List<StockItem> {
        return httpClient.get("$apiStockPath/filteredStocksByDates/$initialDate/$initialTime/$finalDate/$finalTime?timezoneid=Africa/Harare").body()
    }

}



//Exemplos de reason para vendas
//"Venda normal" → Para uma venda comum ao cliente.
//
//"Venda com desconto" → Se houve um desconto na venda.
//
//"Venda cancelada" → Se a venda foi cancelada e o estoque foi ajustado.
//
//"Venda a crédito" → Para indicar que foi uma venda fiada ou parcelada.
//
//"Devolução de cliente" → Se um cliente devolveu o produto e o estoque foi reajustado.
//
//"Correção de estoque" → Se a venda foi lançada errada e precisou ser corrigida.