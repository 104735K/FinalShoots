
function updateClock() {
    const now = new Date();
    const formattedTime = now.toLocaleTimeString();
    document.getElementById("realTimeClock").textContent = formattedTime;
}

setInterval(updateClock, 1000);



fetch('http://localhost:1000/Shoots/getOptions')
    .then(response => response.json())
    .then(data => {
        const firstSelect = document.getElementById("first");
        const secondSelect = document.getElementById("second");
        const thirdSelect = document.getElementById("third");

        Object.keys(data).forEach(city => {
            const option = document.createElement("option");
            option.value = city;
            option.textContent = city;
            firstSelect.appendChild(option);
        });

        firstSelect.addEventListener("change", () => {
            const selectedCity = firstSelect.value;
            secondSelect.innerHTML = '<option value="" disabled selected>구/군</option>';
            thirdSelect.innerHTML = '<option value="" disabled selected>읍/면/동</option>';
            thirdSelect.disabled = true;

            if (selectedCity && data[selectedCity]) {
                secondSelect.disabled = false;
                Object.keys(data[selectedCity]).forEach(district => {
                    const option = document.createElement("option");
                    option.value = district;
                    option.textContent = district;
                    secondSelect.appendChild(option);
                });
            } else {
                secondSelect.disabled = true;
            }
        });

        secondSelect.addEventListener("change", () => {
            const selectedCity = firstSelect.value;
            const selectedDistrict = secondSelect.value;

            thirdSelect.innerHTML = '<option value="" disabled selected>읍/면/동</option>';

            if (selectedCity && selectedDistrict && data[selectedCity][selectedDistrict]) {
                thirdSelect.disabled = false;
                data[selectedCity][selectedDistrict].forEach(town => {
                    const option = document.createElement("option");
                    option.value = town;
                    option.textContent = town;
                    thirdSelect.appendChild(option);
                });
            } else {
                thirdSelect.disabled = true;
            }
        });
    })
    .catch(error => {
        console.error("Error fetching location data:", error);
    });

$(function () {
    $("#matchList").click(function () {
        location.href = "match/list";
    })
})
