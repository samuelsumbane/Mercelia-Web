package view.modules.reportModule

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import repository.ActivityItemGrupped
import repository.SaleReportItem
import repository.twoDigits


@Composable
fun reportPaper(
    reportData: List<SaleReportItem>
) {
    var totalFromSell by remember { mutableDoubleStateOf(0.0) }
//    val gruped = mutableListOf<ActivityItemGrupped>()
    val gruped = mutableListOf<ActivityItemGrupped>()


    Div(attrs = {
        classes("parentMainDiv")
        id("parentMainDiv")
        style {
            width(794.px) //570.px
            height(1123.px) //800.px
            backgroundColor(Color.white)
            property("margin", "2% auto 0 auto")
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            backgroundColor(Color.white)
        }
    }) {
        // Top div
        Div(attrs = {
            id("topdiv")
            style {
                width(98.percent)
                height(10.percent)
                backgroundColor(Color.white)
                property("margin", "30px auto 0 auto")
                display(DisplayStyle.Flex)
            }
        }) {
            H4(attrs = {
                id("Dados")
                style { paddingLeft(10.px) }
            }) {
                Text("Inventário de Vendas")
            }
            H4(attrs = {
                style { property("margin-left", "auto") }
            }) {
                Text("Mercelia")
            }
        }

        // Main div
        Div(attrs = {
            id("maindiv")
            style {
                width(95.percent)
                backgroundColor(Color.white)
                property("margin", "30px auto 0 auto")
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
            }
        }) {
            // Inner rec div
            Div(attrs = {
                classes("rec")
                id("recdiv")
                style {
                    width(98.percent)
                    backgroundColor(Color.white)
                    border(2.px, LineStyle.Solid, Color.black)
//                    borderRadius(bottomLeft = 5.px, bottomRight = 5.px)
                    property("margin", "0 auto 0 auto")
                }
            }) {
                var fromSell = 0.0

                Table(attrs = {
                    style {
                        width(98.percent)
//                        borderCollapse(BorderCollapse.Collapse)
                        property("margin", "7px auto 0 auto")
                    }
                }) {
                    Tr(attrs = {
                        style {
                            backgroundColor(Color.lightgray)
                            fontWeight(400)
                        }
                    }) {
                        Th { Text("Producto") }
                        Th { Text("Qtd.") }
                        Th { Text("SubTotal") }
                        Th { Text("Lucro") }
                        Th { Text("Estado") }
                        Th { Text("Usuário") }
                        Th { Text("Data e hora") }
                    }
                    Tbody(attrs = {
                        id("tbodyActivitiesList")
                    }) {
                        reportData.forEach { saleItem ->
                            val i = saleItem.copy()
                            Tr {
                                Td { Text(saleItem.productName) }
                                Td { Text(saleItem.quantity.toString()) }
                                Td { Text(saleItem.subTotal.twoDigits()) }
                                Td { Text(saleItem.profit.twoDigits()) }
                                Td { Text(saleItem.status) }
                                Td { Text(saleItem.userName) }
                                Td { Text(saleItem.datetime!!) }
                            }
                        }
                        totalFromSell = fromSell
                    }
                }
                Br()
            }
            Br()
        }

        // Bottom div
        Div(attrs = {
            id("bottomdiv")
            style {
                width(95.percent)
                height(5.percent)
                backgroundColor(Color.white)
                property("margin-bottom", "200%")
            }
        }) {
            Br()
            P(attrs = {
                style {
                    fontSize(13.6.px)
                    paddingLeft(25.px)
                }
            }) {
                Text("Impreso por Administrador  12/01/2025 12:35")
            }
        }
    }
}


//@Composable
//fun totalLabelDiv(text: String, value: Double) {
//    Div(attrs = {
//        style {
//            property("margin", "0 0 0 10px")
//        }
//    }) {
//        Text(text)
//        Label(attrs = {
//            style { fontWeight("Bold") }
//        }) { Text("$value MT") }
//    }
//}
