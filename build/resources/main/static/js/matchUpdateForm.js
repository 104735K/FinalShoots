function populateTimeOptions() {
    var select = document.getElementById('match_time');
    var savedTime = select.getAttribute('data-saved-time');

    console.log("match_time >>>>>>>>>>>>>>> " + savedTime)

    for (var hour = 9; hour < 24; hour++) {
        for (var minute of [0, 30]) {
            var hourFormatted = hour.toString().padStart(2, '0');
            var minuteFormatted = minute.toString().padStart(2, '0');
            var time = hourFormatted + ":" + minuteFormatted;

            var option = document.createElement('option');
            option.value = time;
            option.textContent = time;

            if (time === savedTime) {
                option.selected = true;
            }

            select.appendChild(option);
        }
    }
}

document.addEventListener('DOMContentLoaded', function() {
    populateTimeOptions();

    document.matchUpdateForm.onsubmit = function(event) {
        var playerMin = parseInt(document.getElementById('player_min').value);
        var playerMax = parseInt(document.getElementById('player_max').value);
        var price = parseInt(document.getElementById('price').value);

        if (playerMin > playerMax) {
            alert('최소 인원은 최대 인원보다 클 수 없습니다.');
            event.preventDefault();
            return;
        }

        else if (price % 1000 !== 0) {
            alert('가격은 1000원 단위로 입력해야 합니다.');
            event.preventDefault();
            return;
        }

        else if (price > 50000) {
            alert('가격은 50,000원 이하로 설정해야 합니다.');
            event.preventDefault();
            return;
        }

        else {
            if(confirm("매치글을 수정하시겠습니까?")) {
                alert("매치글이 수정되었습니다");
                return true;
            } else {
                return false;
            }
        }
    };

    $('.btn-list').click(function(event) {
        event.preventDefault();
        location.href = "../match/list";
    });
});