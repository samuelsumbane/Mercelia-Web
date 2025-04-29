package view.modules.reportModule

import androidx.compose.runtime.Composable
import components.BtnDetails
import view.basicGruppedPages

@Composable
fun basicReportsPage(userRole: String, sysPackage: String) {
    val reportPages = listOf(
        BtnDetails("reportsBtn", "/reports", "Inv. de Vendas"),
        BtnDetails("stocksBtn", "/stockPage", "Inv. de Estoques"),
    )

    basicGruppedPages("Inventários", reportPages, userRole, sysPackage)
}