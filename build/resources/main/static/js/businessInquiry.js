$(function () {

    $('#inquiryForm').on('click', function (e) {
        e.preventDefault();

        $.ajax({
            url: '/Shoots/business/inquiryForm',
            method: 'GET',
            success: function (data) {
                $('#layoutSidenav_content').html(data);
            },
            error: function (xhr, status, error) {
                console.log('Error loading content: ', error);
            }
        });
    });

})