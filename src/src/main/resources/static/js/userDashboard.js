window.addEventListener('DOMContentLoaded', event => {

    const sidebarToggle = document.body.querySelector('#sidebarToggle');
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', event => {
            event.preventDefault();
            document.body.classList.toggle('sb-sidenav-toggled');
            localStorage.setItem('sb|sidebar-toggle', document.body.classList.contains('sb-sidenav-toggled'));
        });
    }

    $('#Match').on('click', function (e) {

        e.preventDefault();

        $.ajax({
            url: '/Shoots/myPage/myMatchList',
            method: 'GET',
            success: function (data) {
                $('#layoutSidenav_content').html(data);
            },
            error: function (xhr, status, error) {
                console.log('Error loading content: ', error);
            }
        });
    });


    $('#Post').on('click', function (e) {

        e.preventDefault();

        $.ajax({
            url: '/Shoots/myPage/myPostList',
            method: 'GET',
            success: function (data) {
                $('#layoutSidenav_content').html(data);
            },
            error: function (xhr, status, error) {
                console.log('Error loading content: ', error);
            }
        });
    });

    $('#Comment').on('click', function (e) {

        e.preventDefault();

        $.ajax({
            url: '/Shoots/myPage/myCommentList',
            method: 'GET',
            success: function (data) {
                $('#layoutSidenav_content').html(data);
            },
            error: function (xhr, status, error) {
                console.log('Error loading content: ', error);
            }
        });
    });


    $('#Inquiry').on('click', function (e) {

        e.preventDefault();

        $.ajax({
            url: '/Shoots/myPage/myInquiryList',
            method: 'GET',
            success: function (data) {
                $('#layoutSidenav_content').html(data);
            },
            error: function (xhr, status, error) {
                console.log('Error loading content: ', error);
            }
        });
    });

    $('#Calendar').on('click', function (e) {

        e.preventDefault();

        $.ajax({
            url: '/Shoots/myPage/myCalendar',
            method: 'GET',
            success: function (data) {
                $('#layoutSidenav_content').html(data);
            },
            error: function (xhr, status, error) {
                console.log('Error loading content: ', error);
            }
        });
    });

    //회원탈퇴 기능
    $('#deleteUser').click(function(event){ //문의글 삭제 버튼 누르면 삭제하는 메서드
        event.preventDefault();
        if (confirm("정말 문의글을 삭제하시겠습니까?")) {
            $("#deleteUserForm").submit();
        }
    });

});