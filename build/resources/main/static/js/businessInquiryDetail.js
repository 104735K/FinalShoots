document.addEventListener('DOMContentLoaded', function() {

    $('.btn-list').click(function (event) {
        event.preventDefault();
        location.href = "../business/dashboard?tab=inquiry";
    });
})