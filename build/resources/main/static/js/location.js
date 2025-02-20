document.addEventListener("DOMContentLoaded", function() {

    var infowindow = new kakao.maps.InfoWindow({zIndex: 1});

    var container = document.getElementById('map');
    var options = {
        center: new kakao.maps.LatLng(37.570791, 126.997383),
        level: 8
    };

    var map = new kakao.maps.Map(container, options);

    // HTML5의 geolocation으로 사용할 수 있는지 확인
    if (navigator.geolocation) {

        // GeoLocation을 이용해서 접속 위치 얻기
        navigator.geolocation.getCurrentPosition(function (position) {

            var lat = position.coords.latitude, // 위도
                lon = position.coords.longitude; // 경도

            console.log("lat : " + lat);
            console.log("lon : " + lon);


            var locPosition = new kakao.maps.LatLng(lat, lon), // 마커가 표시될 위치를 geolocation으로 얻어온 좌표로 생성
                message = '<div style="padding:5px; font-size:12px;">내 위치</div>';

            // 마커와 인포윈도우를 표시합니다
            displayMarker(locPosition, message);

        });

    } else { // HTML5의 GeoLocation을 사용할 수 없을때 마커 표시 위치와 인포윈도우 내용을 설정

        var locPosition = new kakao.maps.LatLng(33.450701, 126.570667),
            message = '위치 정보 불러오기 실패'

        displayMarker(locPosition, message);
    }

    // Redis 데이터(주소) 값 가져오기
    fetch('/Shoots/location/data')
        .then(response => response.json())
        .then(locations => {
            locations.forEach(function(location) {
                // 주소를 통해 위도, 경도 변환
                getLatLngFromAddress(location.address, function(lat, lon) {
                    if (lat && lon) {
                        var locPosition = new kakao.maps.LatLng(lat, lon); // 위도, 경도 좌표
                        var message = '<div style="padding:5px; font-size:12px;">' + location.businessName + '</div>';

                        displayBusinessAddressMarker(locPosition, message);
                    }
                });
            });
        })
        .catch(error => console.error("Error fetching location data:", error));

    function getLatLngFromAddress(address, callback) {
        var geocoder = new kakao.maps.services.Geocoder();

        geocoder.addressSearch(address, function(result, status) {
            if (status === kakao.maps.services.Status.OK) {
                var lat = result[0].y;
                var lon = result[0].x;
                callback(lat, lon);
            } else {
                console.log("주소 검색 실패: " + address);
                callback(null, null);
            }
        });
    }

    // 지도에 마커와 인포윈도우 표시
    function displayMarker(locPosition, message) {

        var imageSrc = 'img/mainMarker.png';

        // 마커 생성
        var marker = new kakao.maps.Marker({
            map: map,
            position: locPosition,
            image: new kakao.maps.MarkerImage(imageSrc, new kakao.maps.Size(35, 35))
        });

        var iwContent = message, // 인포윈도우에 표시할 내용
            iwRemoveable = true;

        // 인포윈도우 생성
        var infowindow = new kakao.maps.InfoWindow({
            content: iwContent,
            removable: iwRemoveable
        });

        // 인포윈도우를 마커위에 표시
        infowindow.open(map, marker);

        // 지도 중심좌표를 접속위치로 변경
        map.setCenter(locPosition);
    }


    function displayBusinessAddressMarker(locPosition, message) {

        var imageSrc = 'img/subMarker.png';

        var marker = new kakao.maps.Marker({
            map: map,
            position: locPosition,
            image: new kakao.maps.MarkerImage(imageSrc, new kakao.maps.Size(30, 30))
        });

        var iwContent = message,
            iwRemoveable = true;

        var infowindow = new kakao.maps.InfoWindow({
            content: iwContent,
            removable: iwRemoveable
        });

        kakao.maps.event.addListener(marker, 'click', function() {
            infowindow.open(map, marker);
        });
    }


    // 지도 zoom in / out
    window.zoomIn = function() {
        var level = map.getLevel();
        map.setLevel(level - 1);
    };

    window.zoomOut = function() {
        var level = map.getLevel();
        map.setLevel(level + 1);
    }


    document.querySelector(".custom-btn-top").addEventListener("click", function() {
        var searchWord = document.querySelector("input[name='search_word']").value;

        fetch(`/Shoots/location?search_word=${encodeURIComponent(searchWord)}`)
            .then(response => response.text())
            .then(data => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(data, 'text/html');
                const newTable = doc.querySelector('.table');

                const matchListContainer = document.getElementById('matchListContainer');
                matchListContainer.innerHTML = '';

                if (newTable && newTable.querySelector('tbody').children.length > 0) {
                    matchListContainer.appendChild(newTable);
                } else {
                    console.log("데이터 없음");
                    matchListContainer.innerHTML = '<div style="text-align: center; margin: 100px 0 100px 0"><p> 검색된 풋살장이 없습니다. </p></div>';
                }
            })
            .catch(error => {
                console.error('Error loading location data:', error);
            });
    });


    document.querySelector(".custom-btn-all").addEventListener("click", function() {
        var searchWord = "";

        fetch(`/Shoots/location?search_word=${encodeURIComponent(searchWord)}`)
            .then(response => response.text())
            .then(data => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(data, 'text/html');
                const newTable = doc.querySelector('.table');

                const matchListContainer = document.getElementById('matchListContainer');
                matchListContainer.innerHTML = '';

                if (newTable && newTable.querySelector('tbody').children.length > 0) {
                    matchListContainer.appendChild(newTable);
                } else {
                    console.log("데이터 없음");
                    matchListContainer.innerHTML = '<div style="text-align: center; margin: 100px 0 100px 0"><p> 연계된 기업이 없습니다. </p></div>';
                }
            })
            .catch(error => {
                console.error('Error loading location data:', error);
            });
    });

})
