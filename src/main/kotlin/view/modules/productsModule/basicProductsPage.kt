package view.modules.productsModule

import androidx.compose.runtime.Composable
import components.BtnDetails
import view.basicGruppedPages

@Composable
fun basicProductsPage(userRole: String, sysPackage: String) {
    val productsPages = listOf(
        BtnDetails("categoriesBtn", "/categories", "Categorias"),
        BtnDetails("productsBtn", "/products", "Productos"),
    )

    basicGruppedPages("Productos", productsPages, userRole,
        sysPackage, "sidebar-btn-products")
}