import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import app.softwork.routingcompose.HashRouter
import app.softwork.routingcompose.Router
import components.userHasNotAccessScreen
import org.jetbrains.compose.web.renderComposable
import repository.Role
import repository.SettingsRepository
import repository.SysPackages
import repository.UserDataAndSys
import repository.UserRepository
import repository.emptyLoggedUser
import view.*
import view.Afiliates.OwnersPage
import view.Afiliates.clientsPage
import view.Afiliates.suppliersPage
import view.eachUsers.eachUserPage
import view.modules.financeModule.basicFinances
import view.modules.financeModule.payablesPage
import view.modules.financeModule.receivablesPage
import view.modules.partnersModule.basicPartners
import view.modules.productsModule.basicProductsPage
import view.modules.productsModule.categoriesPage
import view.modules.productsModule.productsPage
import view.modules.reportModule.basicReportPage
import view.modules.reportModule.reportsPage
import view.modules.reportModule.stockPage
import view.modules.sellModule.basicSalePage
import view.modules.sellModule.salesPage
import view.modules.settingsModule.basicSettingsPage
import view.modules.settingsModule.brancesPage
import view.modules.settingsModule.notificationsPage
import view.modules.settingsModule.usersPackage.UsersPage
import view.state.AppState.sysPackage


fun main() {
    renderComposable(rootElementId = "root") {
        HashRouter(initPath = "/") {
            val router = Router.current
            val settings = SettingsRepository()
            val users = UserRepository()
            var isLoggedIn by remember { mutableStateOf(false) }
            var hasLoading by remember { mutableStateOf(false) }
            val coroutine = rememberCoroutineScope()
            var user by remember { mutableStateOf(emptyLoggedUser) }

            LaunchedEffect(Unit) {
                val session = users.checkSession()
                if (session != null) {
                    val (status, activePackage) = settings.getPackageName()
                    sysPackage = if (status == 200) {
                        activePackage
                    } else {
                        SysPackages.L.desc
                    }

                    if (session.isLogged) {
                        user = session
                    } else {
                        isLoggedIn = false
                    }
                } else {
                    console.log("session expired")
                }
            }
//            var sysPackage by remember { mutableStateOf(sysPackageC) }

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
                        eachUserPage(user.userId, user.userRole, sysPackage)
                    }

                    route("/products") {
                        productsPage(user.userRole, user.userId, sysPackage)
                    }

                    route("/sales") {
                        salesPage(user.userId, user.userRole, sysPackage)
                    }

                    route("/reports") {
                        val paramData = user.let {
                            UserDataAndSys(it.userId, it.userName, it.userRole, sysPackage)
                        }
                        reportsPage(paramData)
                    }

                    route("/stockPage") {
                        val paramData = user.let {
                            UserDataAndSys(it.userId, it.userName, it.userRole, sysPackage)
                        }
                        stockPage(paramData)
                    }

                    for (uRoute in notForSellerUserRoutes) {
                        route(uRoute) {
                            userHasNotAccessScreen()
                        }
                    }

                } else if (user.userRole == Role.G.desc
                    || user.userRole == Role.A.desc) {

                    route("/categories") {
                        categoriesPage(user.userRole, sysPackage)
                    }

                    route("/products") {
                        productsPage(user.userRole, user.userId, sysPackage)
                    }

                    route("/sales") {
                        salesPage(user.userId, user.userRole, sysPackage)
                    }

                    route("/reports") {
                        val paramData = user.let {
                            UserDataAndSys(it.userId, it.userName, it.userRole, sysPackage)
                        }
                        reportsPage(paramData)
                    }

                    route("/stockPage") {
                        val paramData = user.let {
                            UserDataAndSys(it.userId, it.userName, it.userRole, sysPackage)
                        }
                        stockPage(paramData)
                    }

                    route("/dashboard") {
                        homeScreen(user.userRole, user.userName, sysPackage)
                    }

                    route("/clients") {
                        clientsPage(user.userRole, sysPackage)
                    }

                    route("/owners") {
                        OwnersPage(user.userRole, sysPackage)
                    }

                    route("/users") {
                        UsersPage(user.userRole, sysPackage)
                    }

                    route("/eachUser") {
                        eachUserPage(user.userId, user.userRole, sysPackage)
                    }

                    route("/suppliers") {
                        suppliersPage(user.userRole, sysPackage)
                    }

                    route("/settings") {
                        settingsPage(user.userRole, sysPackage)
                    }

                    route("/branches") {
                        brancesPage(user.userRole, sysPackage)
                    }

                    route("/notifications") {
                        notificationsPage(user.userRole, sysPackage)
                    }

                    if (sysPackage == SysPackages.PO.desc) {
                        route("/payables") {
                            payablesPage(user.userRole, sysPackage)
                        }

                        route("/receivables") {
                            receivablesPage(user.userRole, sysPackage)
                        }
//                    route("/finance-history") {
//                    }
                    }
                }
                // -------->>

                route("/partners-module") {
                    basicPartners(user.userRole, sysPackage)
                }

                route("/products-module") {
                    basicProductsPage(user.userRole, sysPackage)
                }

                route("/finances-module") {
                    basicFinances(user.userRole, sysPackage)
                }

                route("/inventories-module") {
                    basicReportPage(user.userRole, sysPackage)
                }

                route("/sale-module") {
                    basicSalePage(user.userRole, sysPackage)
                }

                route("/basicSettingsPage") {
                    basicSettingsPage(user.userRole, sysPackage)
                }
            } else {
                route("/") {
                    loginPage()
                }
            }
        }
    }
}


val notForSellerUserRoutes = listOf(
    "/",
    "/basicSettingsPage",
    "/categories",
    "/dashboard",
    "/clients",
    "/users",
    "/suppliers",
    "/settings",
    "/branches",
    "/partners-module",
    "/payables",
    "/receivables",
)

val routesList = notForSellerUserRoutes + listOf(
    "/products",
    "/sales",
    "/reports",
    "/stockPage",
    "/eachUser",
    "/settings",
    "/branches",
    "/partners-module",
    "/products-module",
    "/inventories-module",
    "/sale-module",
    "/notifications"
)

val onlyForProPackage = listOf("/payables", "/receivables", "/finance-history")
