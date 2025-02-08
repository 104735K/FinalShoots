$(function () {

    $(document).on('click', '.btn-unblock', function(event) {
        event.preventDefault();
        const checkConfirm = confirm("차단을 해제하시겠습니까?");

        if (checkConfirm) {
            alert("차단이 해제되었습니다");
            $(this).closest("form").submit();
        }
    });


    $("#allBlacklist").click(function () {

        fetch('/Shoots/business/blacklist')
            .then(response => response.text())
            .then(data => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(data, 'text/html');
                const newTable = doc.querySelector('#blacklistContainer table');
                const blacklistContainer = document.getElementById('blacklistContainer');

                blacklistContainer.innerHTML = '';

                if (newTable && newTable.querySelector('tbody').children.length > 0) {
                    const scrollDiv = document.createElement('div');
                    scrollDiv.classList.add('scroll');

                    scrollDiv.appendChild(newTable);

                    blacklistContainer.appendChild(scrollDiv);

                    const caption = newTable.querySelector('caption');
                    if (caption) {
                        caption.textContent = "전체 차단 기록";
                    }
                } else {
                    blacklistContainer.innerHTML = '<div style="text-align: center; margin: 100px 0 100px 0"><p> 블랙리스트 데이터가 존재하지 않습니다 </p></div>';
                }
            })
            .catch(error => {
                console.error('Error loading all matches:', error);
            });
    });

    $("#blockList").click(function () {

        fetch('/Shoots/business/blacklist?block=true')
            .then(response => response.text())
            .then(data => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(data, 'text/html');
                const newTable = doc.querySelector('#blacklistContainer table');
                const blacklistContainer = document.getElementById('blacklistContainer');

                blacklistContainer.innerHTML = '';

                if (newTable && newTable.querySelector('tbody').children.length > 0) {
                    const scrollDiv = document.createElement('div');
                    scrollDiv.classList.add('scroll');

                    scrollDiv.appendChild(newTable);

                    blacklistContainer.appendChild(scrollDiv);

                    const caption = newTable.querySelector('caption');
                    if (caption) {
                        caption.textContent = "차단 고객";
                    }
                } else {
                    blacklistContainer.innerHTML = '<div style="text-align: center; margin: 100px 0 100px 0"><p> 현재 차단된 고객이 없습니다 </p></div>';
                }
            })
            .catch(error => {
                console.error('Error loading all matches:', error);
            });
    });

    $("#unblockList").click(function () {

        fetch('/Shoots/business/blacklist?unblock=true')
            .then(response => response.text())
            .then(data => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(data, 'text/html');
                const newTable = doc.querySelector('#blacklistContainer table');
                const blacklistContainer = document.getElementById('blacklistContainer');

                blacklistContainer.innerHTML = '';

                if (newTable && newTable.querySelector('tbody').children.length > 0) {
                    const scrollDiv = document.createElement('div');
                    scrollDiv.classList.add('scroll');

                    scrollDiv.appendChild(newTable);

                    blacklistContainer.appendChild(scrollDiv);

                    const caption = newTable.querySelector('caption');
                    if (caption) {
                        caption.textContent = "차단 해제 기록";
                    }
                } else {
                    blacklistContainer.innerHTML = '<div style="text-align: center; margin: 100px 0 100px 0"><p> 차단 해제된 고객이 없습니다 </p></div>';
                }
            })
            .catch(error => {
                console.error('Error loading all matches:', error);
            });
    });

});