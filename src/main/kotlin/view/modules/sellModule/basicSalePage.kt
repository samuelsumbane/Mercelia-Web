package view.modules.sellModule

import androidx.compose.runtime.Composable
import components.BtnDetails
import view.basicGruppedPages

@Composable
fun basicSalePage(userRole: String, sysPackage: String) {
    val reportPages = listOf(
        BtnDetails("sellsBtn", "/sales", "Vendas"),
    )

    basicGruppedPages("Vendas", reportPages, userRole, sysPackage)
}