$(function () {

    const popoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]')
    let popoverList = [...popoverTriggerList].map(popoverTriggerEl => new bootstrap.Popover(popoverTriggerEl))

    populateTimeOptions();

    function populateTimeOptions() {
        const openingSelect = document.getElementById('open_time');
        const updateOpeningSelect = document.getElementById('updateOpen_time');
        const closingSelect = document.getElementById('close_time');
        const updateClosingSelect = document.getElementById('updateClose_time');

        var openSavedTime = updateOpeningSelect.getAttribute('data-saved-open-time');
        var closeSavedTime = updateClosingSelect.getAttribute('data-saved-close-time');

        for (let hour = 9; hour < 24; hour++) {
            for (let minute of [0, 30]) {
                const hourFormatted = hour.toString().padStart(2, '0');
                const minuteFormatted = minute.toString().padStart(2, '0');
                const time = `${hourFormatted}:${minuteFormatted}`;

                const openingOption = document.createElement('option');
                openingOption.value = time;
                openingOption.textContent = time;
                openingSelect.appendChild(openingOption);
                updateOpeningSelect.appendChild(openingOption);

                const closingOption = document.createElement('option');
                closingOption.value = time;
                closingOption.textContent = time;
                closingSelect.appendChild(closingOption);
                updateClosingSelect.appendChild(closingOption);

                if (time === openSavedTime) {
                    openingOption.selected = true;
                    updateOpeningSelect.value = time;
                }

                if (time === closeSavedTime) {
                    closingOption.selected = true;
                    updateClosingSelect.value = time;
                }
            }
        }
    }

    $('form[id=insertBusinessInfoForm]').submit(function (event) {
        const checkboxes = $('input[name="field_type"]');

        const isChecked = checkboxes.is(':checked');

        if (!isChecked) {
            event.preventDefault();
            alert('잔디 종류를 하나 이상 선택해 주세요.');
        } else {
            const confirmSave = confirm('구장 정보를 저장하시겠습니까?');
            if (!confirmSave) {
                event.preventDefault();
            } else {
                alert('저장되었습니다.');
            }
        }
    });

    $('form[id=updateBusinessInfoForm]').submit(function (event) {
        const checkboxes = $('input[name="field_type"]');

        const isChecked = checkboxes.is(':checked');

        if (!isChecked) {
            event.preventDefault();
            alert('잔디 종류를 하나 이상 선택해 주세요.');
        } else {
            const confirmSave = confirm('구장 정보를 수정하시겠습니까?');
            if (!confirmSave) {
                event.preventDefault();
            } else {
                alert('수정되었습니다.');
            }
        }
    });
})