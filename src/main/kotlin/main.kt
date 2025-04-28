import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.softwork.routingcompose.HashRouter
import app.softwork.routingcompose.Router
import components.pageNotFoundScreen
import components.userHasNotAccessScreen
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.renderComposable
import repository.Role
import repository.UserRepository
import repository.emptyLoggedUser
import view.*
import view.Afiliates.clientsPage
import view.Afiliates.eachUserPage
//import view.Afiliates.eachUserPage
import view.Afiliates.suppliersPage
import view.modules.financeModule.basicFinances
import view.modules.financeModule.payablesPage
import view.modules.financeModule.receivablesPage
import view.modules.partnersModule.basicPartners
import view.modules.productsModule.basicProductsPage
import view.modules.productsModule.categoriesPage
import view.modules.productsModule.productsPage
//import view.modules.productsModule.categoriesPage
//import view.modules.productsModule.productsPage
//import view.modules.reportModule.stockPage
import view.modules.reportModule.basicReportsPage
import view.modules.reportModule.reportsPage
import view.modules.reportModule.stockPage
//import view.modules.reportModule.reportsPage
import view.modules.sellModule.basicSalePage
import view.modules.sellModule.salesPage
//import view.modules.settingsModule.usersPackage.UsersPage
import view.modules.settingsModule.basicSettingsPage
import view.modules.settingsModule.brancesPage
import view.modules.settingsModule.usersPackage.UsersPage

//import view.modules.settingsModule.brancesPage

val httpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json { isLenient = true })
    }
}


fun main() {
    renderComposable(rootElementId = "root") {
        HashRouter(initPath = "/") {
            val router = Router.current
            val users = UserRepository(httpClient)
            var isLoggedIn by remember { mutableStateOf(false) }
            var isLoading by remember { mutableStateOf(false) }
            var hasLoading by remember { mutableStateOf(false) }
            var user by remember { mutableStateOf(emptyLoggedUser) }

            LaunchedEffect(Unit) {
                val session = users.checkSession()
                if (session != null) {
                    if (session.isLogged) {
                        isLoggedIn = true
                        user = session
                    } else {
                        isLoggedIn = false
                    }
                } else {
                    console.log("session expired")
                }
//                console.log(session)
//                if (!isLoggedIn) {
//                    router.navigate("/")
//                }
            }


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



//            route("/") {
//                loginPage()
//            }

            if (user.isLogged) {
//                noMatch {
//                    pageNotFoundScreen()
//                }

                route("/") {
                    loginPage()
                }

                if (user.userRole == Role.V.desc) {

                    route("/eachUser") {
                        eachUserPage(user.userId)
                    }

                    route("/products") {
                        productsPage(user.userRole)
                    }

                    route("/sales") {
                        salesPage(user.userId, user.userRole)
                    }

                    route("/reports") {
                        reportsPage(user.userRole)
                    }

                    route("/stockPage") {
                        stockPage()
                    }

                    for (uRoute in norForSellerUserRoutes) {
                        route(uRoute) {
                            userHasNotAccessScreen()
                        }
                    }

                } else if (user.userRole == Role.G.desc
                    || user.userRole == Role.A.desc) {


                    route("/categories") {
                        categoriesPage(user.userRole)
                    }

                    route("/products") {
                        productsPage(user.userRole)
                    }

                    route("/sales") {
                        salesPage(user.userId, user.userRole)
                    }

                    route("/reports") {
                        reportsPage(user.userRole)
                    }

                    route("/stockPage") {
                        stockPage()
                    }

                    route("/dashboard") {
                        homeScreen(user.userRole)
                    }


                    route("/clients") {
                        clientsPage(user.userRole)
                    }


                    route("/users") {
                        UsersPage(user.userRole)
                    }

                    route("/eachUser") {
                        eachUserPage(user.userId)
                    }

                    route("/suppliers") {
                        suppliersPage(user.userRole)
                    }


                    route("/settings") {
                        settingsPage()
                    }

                    route("/branches") {
                        brancesPage()
                    }
//                    val onlyForProPackage = listOf("/payables", "/receivables", "/finance-history")

                    route("/payables") {
                        payablesPage(user.userRole)
                    }

                    route("/receivables") {
                        receivablesPage(user.userRole)
                    }

//                    route("/finance-history") {
//                    }
                }


                // -------->>

                route("/basicPartnersPage") {
                    basicPartners()
                }

                route("/basicProductsPage") {
                    basicProductsPage()
                }

                route("/basicFinancePage") {
                    basicFinances()
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
            } else {
                route("/") {
                    loginPage()
                }

            }


        }
    }
}


val routesList = listOf(
    "/",
    "/basicSettingsPage",
    "/categories",
    "/products",
    "/sales",
    "/reports",
    "/stockPage",
    "/dashboard",
    "/clients",
    "/users",
    "/eachUser",
    "/suppliers",
    "/settings",
    "/branches",
    "/basicPartnersPage",
    "/basicProductsPage",
    "/basicReportsPage",
    "/basicSellPage",
)

val norForSellerUserRoutes = listOf(
    "/",
    "/basicSettingsPage",
    "/categories",
    "/dashboard",
    "/clients",
    "/users",
    "/suppliers",
    "/settings",
    "/branches",
    "/basicPartnersPage",
)


val onlyForProPackage = listOf("/payables", "/receivables", "/finance-history")




