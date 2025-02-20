$(function () {

    const popoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]')
    let popoverList = [...popoverTriggerList].map(popoverTriggerEl => new bootstrap.Popover(popoverTriggerEl))

    const currentYear = new Date().getFullYear();
    const yearSelect = document.getElementById('match_year');

    for (let year = currentYear; year >= currentYear - 5; year--) {
        const option = document.createElement('option');
        option.value = year;
        option.textContent = `${year}년`;
        yearSelect.appendChild(option);
    }

    function fetchSalesData() {
        const year = $("#match_year").val();
        const month = $("#match_month").val();

        let queryParams = [];
        if (year) queryParams.push(`year=${year}`);
        if (month) queryParams.push(`month=${month}`);

        const queryString = queryParams.length > 0 ? `?${queryParams.join('&')}` : '';

        fetch(`/Shoots/business/sales${queryString}`)
            .then(response => response.text())
            .then(data => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(data, 'text/html');
                const newTable = doc.querySelector('#salesListContainer table');
                const salesListContainer = document.getElementById('salesListContainer');

                salesListContainer.innerHTML = '';

                if (newTable && newTable.querySelector('tbody').children.length > 0) {
                    salesListContainer.appendChild(newTable);

                    let captionText = '';
                    if (year && month) {
                        captionText = `${year}년 ${month}월 매출`;
                    } else if (year) {
                        captionText = `${year}년도 매출`;
                    } else if (month) {
                        const monthNames = [
                            "1월 매출", "2월 매출", "3월 매출", "4월 매출", "5월 매출",
                            "6월 매출", "7월 매출", "8월 매출", "9월 매출", "10월 매출",
                            "11월 매출", "12월 매출"
                        ];
                        captionText = monthNames[month - 1];
                    } else {
                        captionText = "전체 매출";
                    }

                    document.querySelector('caption').textContent = captionText;

                    const newPopoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]');
                    popoverList = [...newPopoverTriggerList].map(popoverTriggerEl => new bootstrap.Popover(popoverTriggerEl));

                } else {
                    salesListContainer.innerHTML = '<div style="text-align: center; margin: 100px 0 100px 0"><p> 매출 데이터가 존재하지 않습니다 </p></div>';
                }
            })
            .catch(error => {
                console.error('Error loading sales data:', error);
            });
    }

    // 연도와 월 선택 시 함께 처리
    $("#match_year").change(fetchSalesData);
    $("#match_month").change(fetchSalesData);


    $("#allSales").click(function () {

        $("#match_year").val('');
        $("#match_month").val('');

        fetch('/Shoots/business/sales')  // 전체 리스트를 가져오는 요청
            .then(response => response.text())
            .then(data => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(data, 'text/html');
                const newTable = doc.querySelector('#salesListContainer table');
                const salesListContainer = document.getElementById('salesListContainer');

                salesListContainer.innerHTML = '';

                if (newTable && newTable.querySelector('tbody').children.length > 0) {
                    salesListContainer.appendChild(newTable);
                    document.querySelector('caption').textContent = "전체 매출";

                    const newPopoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]');
                    popoverList = [...newPopoverTriggerList].map(popoverTriggerEl => new bootstrap.Popover(popoverTriggerEl));

                } else {
                    salesListContainer.innerHTML = '<div style="text-align: center; margin: 100px 0 100px 0"><p> 매출 데이터가 존재하지 않습니다  </p></div>';
                }
            })
            .catch(error => {
                console.error('Error loading all matches:', error);
            });
    });

});