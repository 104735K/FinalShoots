$(function () {
    var ctx = document.getElementById("myBarChart");

    var mixedChart = {
        type: 'bar',
        labels: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월",],
        datasets : [
            {
                label: '월 별 매치',
                data : monthlyMatchData,
                backgroundColor: 'rgba(128, 128, 128, 0.1)'
            },
            {
                label: '매치 확정',
                data: monthlyCompletedRecruitmentCount,
                backgroundColor: 'transparent',
                borderColor: 'blue',
                type: 'line'
            }
        ]
    };
    var myChart = new Chart(ctx, {
        type: 'bar',
        data: mixedChart,
        options: {
            legend: {
                display: true
            }
        }
    });

    data = {
        datasets: [{
            backgroundColor: ['blue','rgba(128, 128, 128, 0.2)'],
            data: [recruitmentPercentage, percentage]
        }],
        labels: ['확정','미확정']
    };

    var ctx1 = document.getElementById("myChart1");
    var myPieChart = new Chart(ctx1, {
        type: 'pie',
        data: data,
        options: {}
    });

    let backgroundColor;
    if (malePercentage > femalePercentage) {
        backgroundColor = ['rgba(0, 0, 255, 0.8)', 'rgba(128, 128, 128, 0.2)'];
    } else if (malePercentage < femalePercentage) {
        backgroundColor = ['rgba(128, 128, 128, 0.2)', 'rgba(255, 0, 0, 0.7)'];
    } else {
        backgroundColor = ['rgba(0, 0, 255, 0.7)', 'rgba(255, 0, 0, 0.7)'];
    }

    data2 = {
        datasets: [{
            backgroundColor: backgroundColor,
            data: [malePercentage, femalePercentage]
        }],
        labels: ['남성','여성']
    };

    var ctx2 = document.getElementById("myChart2");
    var myPieChart2 = new Chart(ctx2, {
        type: 'doughnut',
        data: data2,
        options: {}
    });


    var ctx3 = document.getElementById('myChart3');
    var myChart3 = new Chart(ctx3, {
        type: 'bar',
        data: {
            labels: ['우리구장', '평균가'],
            datasets: [{
                label: '평균가',
                data: [roundedAvgPriceByIdx, roundedAvgPrice],
                backgroundColor: [
                    'rgba(101, 163, 13, 0.2)',
                    'rgba(128, 128, 128, 0.2)'
                ],
                borderColor: [
                    'rgba(101, 163, 13, 1)',
                    'rgba(128, 128, 128, 1)'
                ],
                borderWidth: 0.7,
            }]
        },
        options: {
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero: true
                    }
                }]
            }
        }
    });

});