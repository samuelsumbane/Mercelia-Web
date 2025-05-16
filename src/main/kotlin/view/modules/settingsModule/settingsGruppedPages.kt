package view.modules.settingsModule

import androidx.compose.runtime.Composable
import components.BtnDetails
import repository.Role
import view.basicGruppedPages

@Composable
fun basicSettingsPage(userRole: String, sysPackage: String) {

    val reportPages = buildList {
        add(BtnDetails("sellsBtn", "/notifications", "Notificações"))
        if (userRole != Role.V.desc) {
            add(BtnDetails("sellsBtn", "/settings", "Configurações do sistema"))
            add(BtnDetails("sellsBtn", "/users", "Usuários"))
            add(BtnDetails("sellsBtn", "/branches", "Sucursais"))
        }
    }

    basicGruppedPages("Configurações", reportPages, userRole,
        sysPackage, "sidebar-btn-settings")
}