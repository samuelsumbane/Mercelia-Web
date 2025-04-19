package view.modules.reportModule

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import repository.StockItem
import repository.twoDigits


@Composable
fun stockPaper(
    reportData: List<StockItem>
) {
    var totalFromSell by remember { mutableDoubleStateOf(0.0) }

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
                Text("Inventário de Estoque")
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
                        Th { Text("Tipo") }
                        Th { Text("Qtd.") }
                        Th { Text("Qtd. Antes") }
                        Th { Text("Qtd. Depois") }
                        Th { Text("Custo") }
                        Th { Text("Preço") }
                        Th { Text("Razão") }
                        Th { Text("Data e Hora") }
                        Th { Text("Usuário") }
                    }
                    Tbody(attrs = {
                        id("tbodyActivitiesList")
                    }) {
                        reportData.forEach { stock ->
                                               val i = stock.copy()
                            Tr {
                                Td { Text(stock.productName) }
                                Td { Text(stock.type) }
                                Td { Text(stock.quantity.toString()) }
                                Td { Text(stock.beforeQty.toString()) }
                                Td { Text(stock.afterQty.toString()) }
                                Td { Text(stock.cost.twoDigits()) }
                                Td { Text(stock.price.twoDigits()) }
                                Td { Text(stock.reason) }
                                Td { Text(stock.datetime) }
                                Td { Text(stock.userName) }
                            }
                            console.log("Actividade = $stock")

                            when (stock.type) {
                                "Saída" -> fromSell += stock.price
                            }
                        }
                        totalFromSell = fromSell
                    }
                }
            }

            Br()
            Br()
//            totalLabelDiv("Valor de saidas (Stock): ", totalFromStock)
            totalLabelDivStock("Total de entrada (Venda): ", totalFromSell)
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


@Composable
fun totalLabelDivStock(text: String, value: Double) {
    Div(attrs = {
        style {
            property("margin", "0 0 0 10px")
        }
    }) {
        Text(text)
        Label(attrs = {
            style { fontWeight("Bold") }
        }) { Text("$value MT") }
    }
}
