package view.modules.partnersModule

import androidx.compose.runtime.Composable
import components.BtnDetails
import view.basicGruppedPages

@Composable
fun basicPartners(userRole: String, sysPackage: String) {
    val partnersPages = listOf(
        BtnDetails("clientsBtn", "/clients", "Clientes"),
        BtnDetails("suppliersBtn", "/suppliers", "Fornecedores"),
        BtnDetails("ownersBtn", "/owners", "Proprietários"),
    )

    basicGruppedPages("Parceiros", partnersPages, userRole, sysPackage)
}