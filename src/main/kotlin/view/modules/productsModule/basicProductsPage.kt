package view.modules.productsModule

import androidx.compose.runtime.Composable
import components.BtnDetails
import repository.Role
import view.basicGruppedPages

@Composable
fun basicProductsPage(userRole: String, sysPackage: String) {
    val productsPages = buildList {
        add(BtnDetails("productsBtn", "/products", "Productos"))
        if (userRole != Role.V.desc) {
            add(BtnDetails("categoriesBtn", "/categories", "Categorias"))
        }
    }

    basicGruppedPages("Productos", productsPages, userRole,
        sysPackage, "sidebar-btn-products")
}