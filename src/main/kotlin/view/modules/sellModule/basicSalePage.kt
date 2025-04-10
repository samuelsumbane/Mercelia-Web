package view.modules.sellModule

import androidx.compose.runtime.Composable
import components.BtnDetails
import view.basicGruppedPages

@Composable
fun basicSalePage() {
    val reportPages = listOf(
        BtnDetails("sellsBtn", "/sales", "Vendas"),
    )

    basicGruppedPages("Vendas", reportPages)
}