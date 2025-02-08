    function requestPayment() {
        const IMP = window.IMP;
        IMP.init("imp35523152");

        const matchIdx = document.getElementById('paymentButton').getAttribute('data-matchIdx');
        const sellerIdx = document.getElementById('paymentButton').getAttribute('data-seller');
        const buyerIdx = document.getElementById('paymentButton').getAttribute('data-buyer');
        const price = document.getElementById('paymentButton').getAttribute('data-price');

        const sessionKey = `currentMerchantUid_${matchIdx}_${buyerIdx}`;

        let merchant_uid = sessionStorage.getItem(sessionKey) || `merchant_${matchIdx}_${buyerIdx}`;

        if (merchant_uid) {
            const storedMatchIdx = merchant_uid.split('_')[1];
            const storedBuyerIdx = merchant_uid.split('_')[2];

            if (storedMatchIdx !== matchIdx || storedBuyerIdx !== buyerIdx) {
                sessionStorage.removeItem(sessionKey);
                merchant_uid = `merchant_${matchIdx}_${buyerIdx}`;
            }
        }

        sessionStorage.setItem(sessionKey, merchant_uid);

        console.log("merchant_uid = " + merchant_uid);

        if (!buyerIdx) {
            alert("매치신청은 로그인 후 이용 가능합니다.");
            window.location.href = "/Shoots/login";
            return;
        }

        sessionStorage.setItem('merchant_uid', merchant_uid);

        fetch("/Shoots/payment/checkPayment", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({merchant_uid: merchant_uid}),
        })
        .then((response) => {
            if(!response.ok) {
                throw new Error("결제 상태 확인 중 오류가 발생했습니다.");
            }
            return response.status === 204 ? null : response.json();
        })
        .then((result) => {
            if (result) {
                if (result.status === 'paid') {
                    alert("이미 결제가 완료된 거래 입니다.");
                    console.log("결제 확인 결과 : ", result);

                    const paymentData = {
                        imp_uid: result.imp_uid,
                        merchant_uid: result.merchant_uid,
                        match_idx: matchIdx,
                        seller_idx: sellerIdx,
                        buyer_idx: buyerIdx,
                        payment_amount: price,
                        payment_method: "card",
                        payment_status: "paid"
                    };

                    fetch("/Shoots/payment/add", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify(paymentData),
                    })
                        .then((response) => {
                        if (!response.ok) {
                            throw new Error("서버 응답 오류: " + response.status);
                        }
                            return response.json();
                    })
                        .then((result) => {
                            console.log("DB 저장 결과:", result);
                            if (result.success) {
                                alert("결제가 완료되었습니다. 매치 신청이 완료되었습니다.");
                                window.location.reload();
                            } else {
                                alert("결제 처리에 실패했습니다. 관리자에게 문의해주세요.");
                                window.location.reload();
                            }
                        })
                        .catch((error) => {
                            console.log("결제 정보 저장 오류:", error);
                            alert("결제 정보 저장 중 오류가 발생했습니다.");
                            window.location.reload();
                        });
                } else if (result.status === 'cancelled' || result.status === 'failed'){
                    console.log("결제 취소 된 건, 재 결제 진행");

                    let newMerchantUid = 'merchant_' + matchIdx + "_" + buyerIdx + "_" + Date.now();
                    console.log("재결제 주문번호 (재설정) newMerchantUid : " + newMerchantUid)

                    sessionStorage.setItem(sessionKey, newMerchantUid);

                    IMP.request_pay(
                        {
                            channelKey: "channel-key-7a6c345a-a185-4466-865b-b2cca8f302d3",
                            pay_method: "card",
                            merchant_uid: newMerchantUid,
                            name: matchIdx + ' 번 매치-플레이어 신청',
                            amount: price,
                            buyer_email: "shk7357@naver.com",
                            buyer_name: "강성현",
                            buyer_tel: "010-9711-7305",
                            language: "ko",
                        },
                        function (response) {
                            console.log(response);

                            if (response.imp_uid) {
                                fetch("/Shoots/payment/add", {
                                    method: "POST",
                                    headers: {
                                        "Content-Type": "application/json"
                                    },
                                    body: JSON.stringify({
                                        imp_uid: response.imp_uid,
                                        merchant_uid: response.merchant_uid,
                                        match_idx: matchIdx,
                                        seller_idx: sellerIdx,
                                        buyer_idx: buyerIdx,
                                        payment_amount: price,
                                        payment_method: "card"
                                    }),
                                })
                                    .then((notified) => notified.ok ? notified.json() : Promise.reject('서버 응답 오류'))
                                    .then((result) => {
                                        console.log("서버 응답 데이터:", result);

                                        if (result.success) {
                                            alert("결제가 완료되었습니다. 매치 신청이 완료되었습니다.");
                                            window.location.reload();
                                        } else {
                                            alert("결제 처리에 실패했습니다. 관리자에게 문의해주세요.");
                                            window.location.reload();
                                        }
                                    })
                                    .catch((error) => {
                                        console.log("결제 데이터 전송 오류 : ", error);
                                        alert("결제 처리 중 오류가 발생했습니다. 관리자에게 문의해주세요");
                                        window.location.reload();
                                    });
                            } else {
                                alert("결제 오류 : " + response.error_code + ", " + response.error_msg);
                            }
                        }
                    );
                }
            } else {
                console.log("결제 정보 없음, 새로운 결제 진행");
                IMP.request_pay(
                    {
                        channelKey: "channel-key-7a6c345a-a185-4466-865b-b2cca8f302d3",
                        pay_method: "card",
                        merchant_uid: merchant_uid,
                        name: matchIdx + ' 번 매치-플레이어 신청',
                        amount: price,
                        buyer_email: "shk7357@naver.com",
                        buyer_name: "강성현",
                        buyer_tel: "010-9711-7305",
                        language: "ko",
                    },
                    function (response) {
                        console.log(response);

                        if (response.imp_uid) {
                            fetch("/Shoots/payment/add", {
                                method: "POST",
                                headers: {
                                    "Content-Type": "application/json"
                                },
                                body: JSON.stringify({
                                    imp_uid: response.imp_uid,
                                    merchant_uid: response.merchant_uid,
                                    match_idx: matchIdx,
                                    seller_idx: sellerIdx,
                                    buyer_idx: buyerIdx,
                                    payment_amount: price,
                                    payment_method: "card"
                                }),
                            })
                                .then((notified) => notified.ok ? notified.json() : Promise.reject('서버 응답 오류'))
                                .then((result) => {
                                    console.log("서버 응답 데이터:", result);

                                    if (result.success) {
                                        alert("결제가 완료되었습니다. 매치 신청이 완료되었습니다.");
                                        window.location.reload();
                                    } else {
                                        alert("결제 처리에 실패했습니다. 관리자에게 문의해주세요.");
                                        window.location.reload();
                                    }
                                })
                                .catch((error) => {
                                    console.log("결제 데이터 전송 오류 : ", error);
                                    alert("결제 처리 중 오류가 발생했습니다. 관리자에게 문의해주세요");
                                    window.location.reload();
                                });
                        } else {
                            alert("결제 오류 : " + response.error_code + ", " + response.error_msg);
                        }
                    }
                );
            }
        })
        .catch((error) => {
            console.log("결제 상태 확인 중 오류 : ", error);
            alert("결제 상태 확인 중 오류가 발생했습니다. 관리자에게 문의해주세요.")
        });
    }

    function clearSessionForMatch(matchIdx, buyerIdx) {
        const sessionKey = `currentMerchantUid_${matchIdx}_${buyerIdx}`;
        console.log(`초기화된 세션 키: ${sessionKey}`);
        sessionStorage.removeItem(sessionKey);
    }


    function closedMatch(){
        alert("마감된 매치입니다");
    }

    function limitedMatch(){
        alert("신청인원이 다 찼습니다");
    }

    function genderBlock(){
        alert("신청 가능한 성별이 아닙니다");
    }

    function roleBlock(){
        alert("신청 가능한 대상이 아닙니다");
    }


    function requestRefund() {

        const userConfirmed = confirm("매치 신청을 취소하시겠습니까?");

        if (!userConfirmed) {
            return;
        }

        const paymentIdx = document.getElementById('refundButton').getAttribute('data-payment_idx');
        const impUid = document.getElementById('refundButton').getAttribute('data-imp_uid');
        const merchantUid = document.getElementById('refundButton').getAttribute('data-merchant_uid');
        const refundAmount = document.getElementById('refundButton').getAttribute('data-amount');
        const matchIdx = document.getElementById('refundButton').getAttribute('data-matchIdx');
        const buyerIdx = document.getElementById('refundButton').getAttribute('data-buyer');

        console.log("impUid = " + impUid)
        console.log("merchantUid = " + merchantUid)
        console.log("refundAmount = " + refundAmount)

        var refundData = {
            payment_idx: paymentIdx,
            imp_uid: impUid,
            merchant_uid: merchantUid,
            refund_amount: refundAmount,
            payment_status: 'refunded',
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
                    clearSessionForMatch(matchIdx, buyerIdx);
                    window.location.reload();
                } else {
                    alert('환불 처리에 실패했습니다.');
                    console.log(response);
                    console.log("Error code:", response.error_code);
                    window.location.reload();
                }
            },
            error: function(xhr, status, error) {
                alert('환불 요청 중 오류가 발생했습니다.');
                console.error('Error:', error);
                console.log("Error code:", xhr.status);
            }
        });
    };

