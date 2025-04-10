function showAlert(icon, title, text) {
    swal({
        icon: icon,
        title: title,
        text: text
    });
}

function showOkayAlert(icon, title, text, onOkay) {
    swal({
        icon: icon,
        title: title,
        text: text
    }).then((ok) => {
        onOkay();
    });
}


function showAlertDelete(icon, title, text, deleteFun) {
    swal({
        title: title,
        text: text,
        icon: icon,
        buttons: true,
        dangerMode: true,
    }).then((willDelete) => {
        if (willDelete) {
            deleteFun();
        }
    });
}


function basicBarChart(canvaId, labelsData, labelName, backgroundcolor, data, optionText) {
    new Chart(document.getElementById(canvaId), {
        type: 'bar',
        data: {
              labels: labelsData,
              datasets: [{
                barPercentage: 1,
                barThickness: 18,
                maxBarThickness: 100,
                minBarLength: 14,
                label: labelName,
                type: "bar",
                backgroundColor: backgroundcolor,
                data: data,
            }]
        },
        options: {
            title: {
                display: true,
                text: optionText
            },
            legend: {
                display: false
            }
        }
    });
}


function soldProductsChart(labelsData, quantityData) {
    basicBarChart("topSales", labelsData, "Top Vendas", "#e93d58", quantityData, "Top Vendas")
}

function topUsersChart(labelsData, valuesData) {
    basicBarChart("topUsers", labelsData, "Top Usuários", "#926ee4", valuesData, "Top Usuários")
}

function salesProfitsByMonthsAndYear(labelsData, profitsData) {
    basicBarChart("monthlyProfits", labelsData, "Lucros Mensais", "#e9643a", profitsData, "Lucros Mensais")
}

function salesQuantitiesByMonthsAndYear(labelsData, quantitiesData) {
    basicBarChart("monthlySalesQuantities", labelsData, "Vendas Mensais", "#3daee9", quantitiesData, "quantitiesData")
}

//
////
//let delayed;
//const config = {
//  type: 'bar',
//  data: data,
//  options: {
//    animation: {
//      onComplete: () => {
//        delayed = true;
//      },
//      delay: (context) => {
//        let delay = 0;
//        if (context.type === 'data' && context.mode === 'default' && !delayed) {
//          delay = context.dataIndex * 300 + context.datasetIndex * 100;
//        }
//        return delay;
//      },
//    },
//    scales: {
//      x: {
//        stacked: true,
//      },
//      y: {
//        stacked: true
//      }
//    }
//  }
//};
//
//// pie
//const config = {
//  type: 'pie',
//  data: data,
//  options: {
//    responsive: true,
//    plugins: {
//      legend: {
//        position: 'top',
//      },
//      title: {
//        display: true,
//        text: 'Chart.js Pie Chart'
//      }
//    }
//  },
//};

//swal({
//  title: "Are you sure?",
//  text: "Once deleted, you will not be able to recover this imaginary file!",
//  icon: "warning",
//  buttons: true,
//  dangerMode: true,
//})
//.then((willDelete) => {
//  if (willDelete) {
//    swal("Poof! Your imaginary file has been deleted!", {
//      icon: "success",
//    });
//  } else {
//    swal("Your imaginary file is safe!");
//  }
//});
//
//swal("Are you sure you want to do this?", {
//  buttons: ["Oh noez!", "Aww yiss!"],
//});