package view.modules.settingsModule

import androidx.compose.runtime.Composable
import components.BtnDetails
import view.basicGruppedPages

@Composable
fun basicSettingsPage(userRole: String, sysPackage: String) {
    val reportPages = listOf(
        BtnDetails("sellsBtn", "/settings", "Configurações do sistema"),
        BtnDetails("sellsBtn", "/users", "Usuários"),
        BtnDetails("sellsBtn", "/branches", "Sucursais"),
        BtnDetails("sellsBtn", "/notifications", "Notificações"),
    )

    basicGruppedPages("Configurações", reportPages, userRole,
        sysPackage, "sidebar-btn-settings")
}