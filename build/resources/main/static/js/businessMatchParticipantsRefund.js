function BrequestRefund(button) {

    const name = button.getAttribute('data-name');

    const userConfirmed = confirm(name + "님의 매치 신청을 취소하시겠습니까?");

    if (!userConfirmed) {
        return;
    }

    const paymentIdx = button.getAttribute('data-payment_idx');
    const impUid = button.getAttribute('data-imp_uid');
    const merchantUid = button.getAttribute('data-merchant_uid');
    const refundAmount = button.getAttribute('data-amount');

    console.log(">>>> paymentIdx = " + paymentIdx)
    console.log(">>>> impUid = " + impUid)
    console.log(">>>> merchantUid = " + merchantUid)
    console.log(">>>> refundAmount = " + refundAmount)

    var refundData = {
        payment_idx: paymentIdx,
        imp_uid: impUid,
        merchant_uid: merchantUid,
        refund_amount: refundAmount,
        payment_status: 'refunded'
    };

    $.ajax({
        url: '/Shoots/refund/refundProcess',
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        data: JSON.stringify(refundData),
        success: function(response) {
            if (response.success) {
                alert('환불 처리되었습니다.');
                console.log(response);
                console.log("Error code:", response.error_code);
            } else {
                alert('환불 처리에 실패했습니다.');
                console.log(response);
                console.log("Error code:", response.error_code);
            }
        },
        error: function(xhr, status, error) {
            alert('환불 요청 중 오류가 발생했습니다.');
            console.error('Error:', error);
            console.log("Error code:", xhr.status);
        }
    });
};