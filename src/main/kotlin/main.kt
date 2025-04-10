import app.softwork.routingcompose.HashRouter
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.mediacapture.Settings
import view.*
import view.Afiliates.clientsPage
import view.Afiliates.eachUserPage
import view.Afiliates.suppliersPage
import view.modules.partnersModule.basicPartners
import view.modules.productsModule.basicProductsPage
import view.modules.productsModule.categoriesPage
import view.modules.productsModule.productsPage
import view.modules.reportModule.stockPage
import view.modules.reportModule.basicReportsPage
import view.modules.reportModule.reportsPage
import view.modules.sellModule.basicSalePage
import view.modules.sellModule.salesPage
import view.modules.settingsModule.usersPackage.UsersPage
import view.modules.settingsModule.basicSettingsPage

//import view.Afiliates.afiliatesPage
//import view.Afiliates.eachAfiliatePage

fun main() {
    renderComposable(rootElementId = "root") {
        HashRouter(initPath = "/") {

//            val currentPath = rememberHashPath()
//
//            if (currentPath in validRoutes) {
//                when (currentPath) {
//                    "/" -> loginPage()
//                    "/categories" -> categoriesPage()
//                    "/users" -> usersPage()
//                }
//            } else {
//                PageNotFound()
//            }


            route("/") {
                loginPage()
            }

            route("/categories") {
                categoriesPage()
            }

            route("/dashboard") {
                homeScreen()
            }

            route("/products") {
                productsPage()
            }

            route("/clients") {
                clientsPage()
            }

            route("/sales") {
                salesPage()
            }

            route("/users") {
                UsersPage()
            }

            route("/eachUser") {
                eachUserPage()
            }

            route("*") {
                Text("found")
            }

            route("/reports") {
                reportsPage()
            }

            route("/suppliers") {
                suppliersPage()
            }

            route("/stockPage") {
                stockPage()
            }

            route("/settings") {
                settingsPage()
            }

            // -------->>

            route("/basicPartnersPage") {
                basicPartners()
            }

            route("/basicProductsPage") {
                basicProductsPage()
            }

            route("/basicReportsPage") {
                basicReportsPage()
            }

            route("/basicSellPage") {
                basicSalePage()
            }

            route("/basicSettingsPage") {
                basicSettingsPage()
            }


        }
    }
}

