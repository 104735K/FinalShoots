$(function () {
    $("#matchForm").click(function () {
        location.href = "write";
    })

    $(".today").click(function () {
        const urlParams = new URLSearchParams(window.location.search);
        const business_idx = urlParams.get("business_idx");

        $("#player_gender").val('');
        $("#match_level").val('');

        let fetchUrl = '/Shoots/match/list?filter=today';
        if (business_idx) {
            fetchUrl += `&business_idx=${business_idx}`;
        }

        fetch(fetchUrl)
            .then(response => response.text())
            .then(data => {

                // 서버에서 반환된 HTML에서 테이블만 추출하여 matchListContainer 갱신
                const parser = new DOMParser();
                const doc = parser.parseFromString(data, 'text/html');
                const newTable = doc.querySelector('#matchListContainer table');
                const matchListContainer = document.getElementById('matchListContainer');

                matchListContainer.innerHTML = '';

                if (newTable && newTable.querySelector('tbody').children.length > 0) {
                    matchListContainer.appendChild(newTable); // 테이블이 있으면 추가
                    document.querySelector('caption').textContent = "오늘의 매치"; // 캡션 수정
                } else {
                    console.log(">>>>>>>>>>>>>>>>>> 데이터 없음")
                    matchListContainer.innerHTML = '<div style="text-align: center; margin: 100px 0 100px 0"><p> 오늘 날짜에 해당하는 매치가 없습니다 </p></div>';
                }

            })
            .catch(error => {
                console.error('Error loading today matches:', error);
            });
    });

    $("#allMatchesBtn").click(function () {

        const urlParams = new URLSearchParams(window.location.search);
        const business_idx = urlParams.get("business_idx");

        if (business_idx) {
            location.href = `/Shoots/match/list`;
        } else {
            $("#player_gender").val('');
            $("#match_level").val('');

            fetch('/Shoots/match/list')  // 전체 리스트를 가져오는 요청
                .then(response => response.text())
                .then(data => {
                    const parser = new DOMParser();
                    const doc = parser.parseFromString(data, 'text/html');
                    const newTable = doc.querySelector('#matchListContainer table');
                    const matchListContainer = document.getElementById('matchListContainer');

                    matchListContainer.innerHTML = '';

                    if (newTable && newTable.querySelector('tbody').children.length > 0) {
                        matchListContainer.appendChild(newTable);
                        document.querySelector('caption').textContent = "전체 매치";
                    } else {
                        matchListContainer.innerHTML = '<div style="text-align: center; margin: 100px 0 100px 0"><p> 매치글이 존재하지 않습니다 </p></div>';
                    }
                })
                .catch(error => {
                    console.error('Error loading all matches:', error);
                });
        }
    });

    function fetchMatchData() {
        const urlParams = new URLSearchParams(window.location.search);
        const business_idx = urlParams.get("business_idx");
        const gender = $("#player_gender").val();
        const level = $("#match_level").val();

        let queryParams = [];
        if (business_idx) queryParams.push(`business_idx=${business_idx}`);
        if (gender) queryParams.push(`gender=${gender}`);
        if (level) queryParams.push(`level=${level}`);

        const queryString = queryParams.length > 0 ? `?${queryParams.join('&')}` : '';

        fetch(`/Shoots/match/list${queryString}`)
            .then(response => response.text())
            .then(data => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(data, 'text/html');
                const newTable = doc.querySelector('#matchListContainer table');
                const matchListContainer = document.getElementById('matchListContainer');

                matchListContainer.innerHTML = '';

                if (newTable && newTable.querySelector('tbody').children.length > 0) {
                    matchListContainer.appendChild(newTable);

                    let captionText = '';
                    if (gender && level) {
                        const genderText = gender === 'm' ? "남성 매치"
                            : gender === 'f' ? "여성 매치"
                                : "혼성 매치";
                        const levelText = level === '초급' ? "초급"
                            : level === '중급' ? "중급"
                                : level === '고급' ? "고급"
                                    : "무관";
                        captionText = `${genderText}, ${levelText}`;
                    } else if (gender) {
                        captionText = gender === 'm' ? "남성 매치"
                            : gender === 'f' ? "여성 매치"
                                : "혼성 매치";
                    } else if (level) {
                        captionText = level === '초급' ? "초급 매치"
                            : level === '중급' ? "중급 매치"
                                : level === '고급' ? "고급 매치"
                                    : "난이도 무관 매치";
                    } else {
                        captionText = "매치 리스트";
                    }

                    document.querySelector('caption').textContent = captionText;
                } else {
                    matchListContainer.innerHTML = `
                    <div style="text-align: center; margin: 100px 0 100px 0">
                        <p>${gender ? "해당 성별에 맞는 매치가 없습니다" : "해당 난이도에 맞는 매치가 없습니다"}</p>
                    </div>`;
                }
            })
            .catch(error => {
                console.error('Error loading matches:', error);
            });
    }

    $("#player_gender").change(fetchMatchData);
    $("#match_level").change(fetchMatchData);

    $(document).ready(fetchMatchData);

});