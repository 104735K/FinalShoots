document.addEventListener('DOMContentLoaded', function() {

    $('.btn-list').click(function() {
        location.href = "../match/list";
    });

    $('.btn-delete').click(function(event) {
        event.preventDefault();
        const userConfirmed = confirm("매치글을 삭제하시겠습니까?");

        if (userConfirmed) {
            alert("매치글이 삭제되었습니다");
            $(this).closest("form").submit();
        }
    });


});