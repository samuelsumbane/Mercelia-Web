function showAlert(icon, title, text) {
    Swal.fire({
      title: title,
      text: text,
      icon: icon
    });
}

function showOkayAlert(icon, title, text, onOkay) {
    Swal.fire({
        icon: icon,
        title: title,
        text: text
    }).then((ok) => {
        onOkay();
    });
}


function showAlertDelete(title, text, deleteFun) {
    Swal.fire({
      title: title,
      text: text,
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Sim, deletar"
    }).then((result) => {
      if (result.isConfirmed) {
        deleteFun();
      }
    });
}

function showAlertTimer(title) {
    Swal.fire({
      position: "top-end",
      icon: "success",
      title: title,
      showConfirmButton: false,
      timer: 1500
    });
}


function basicPieChart(canvaId, labelsData, labelName, backgroundcolor, data, optionText) {
    new Chart(document.getElementById(canvaId), {
        type: 'doughnut',
        data: {
              labels: labelsData,
              datasets: [{
                label: labelName,
                type: 'doughnut',
                backgroundColor: ['#02152a', '#f64535', '#185cee', '#60A5FA', '#93C5FD'],
                data: data,
            }]
        },
        options: {
            responsive: true,
            plugins: {
                 legend: {
                   position: 'top',
                   maintainAspectRatio: false,
                 },
            },
        }
    });
}


function soldProductsChart(labelsData, quantityData) {
    basicBarChart("topSales", labelsData, "Top Vendas", "#e93d58", quantityData, "Top Vendas")
}

function topUsersChart(labelsData, valuesData) {
    basicPieChart("topUsers", labelsData, "Top Usuários", "#926ee4", valuesData, "Top Usuários")
}

function salesProfitsByMonthsAndYear(labelsData, profitsData) {
    basicBarChart("monthlyProfits", labelsData, "Lucros Mensais", "#e9643a", profitsData, "Lucros Mensais")
}

function salesQuantitiesByMonthsAndYear(labelsData, quantitiesData) {
    basicBarChart("monthlySalesQuantities", labelsData, "Vendas Mensais", "#3daee9", quantitiesData, "quantitiesData")
}

function basicBarChart(canvaId, labelsData, labelName, backgroundcolor, data, optionText) {
//    let delayed;
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
                borderRadius: 6,
                borderSkipped: false,
                data: data,
            }]
        },
        options: {
            scales: {
                y: {
                  beginAtZero: true
                }
            }
        }
    });
}