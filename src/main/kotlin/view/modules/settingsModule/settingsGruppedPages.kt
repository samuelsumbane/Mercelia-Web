package view.modules.settingsModule

import androidx.compose.runtime.Composable
import components.BtnDetails
import view.basicGruppedPages

@Composable
fun basicSettingsPage() {
    val reportPages = listOf(
        BtnDetails("sellsBtn", "/settings", "Configurações do sistema"),
        BtnDetails("sellsBtn", "/users", "Usuários"),
    )

    basicGruppedPages("Configurações", reportPages)
}