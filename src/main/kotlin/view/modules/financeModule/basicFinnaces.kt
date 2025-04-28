package view.modules.financeModule

import androidx.compose.runtime.Composable
import components.BtnDetails
import view.basicGruppedPages

@Composable
fun basicFinances() {
    val financesPages = listOf(
        BtnDetails("cpagar", "/payables", "C. Pagar"),
        BtnDetails("creceber", "/receivables", "C. Receber"),
        BtnDetails("historic", "/finance-history", "Histórico"),
    )

    basicGruppedPages("Finanças", financesPages)
}