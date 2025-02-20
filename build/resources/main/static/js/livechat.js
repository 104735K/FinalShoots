'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var chatHeader = $('.chat-header');

var stompClient = null;
var username = null;
//topicName이 chat_room_idx
var topicName = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

usernameForm.addEventListener('submit', connect, true) //true는 캡처링. false는 버블링
messageForm.addEventListener('submit', sendMessage, true)
connect();

function connect(event) {
    username = $('#session_id').val().trim();
    topicName = $('#chatRoomNumber').val().trim();

    if(username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket = new SockJS('http://localhost:1000/Shoots/livechat/livechat/ws'); // 포트에 맞게 수정
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, onError);//1헤더 2성공 3실패
    }
    event.preventDefault();
}


function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/' + topicName, onMessageReceived);
    chatHeader.text("채팅방 이름 : " + topicName);
    loadChat();
    // Tell your username to the server

    // 01/23 여기 만지세요
    stompClient.send("/app/"+topicName,
                    {},
                    JSON.stringify({sender: username, type: 'JOIN'})
                    )

    connectingElement.classList.add('hidden');

}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send("/app/chat"+topicName, {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        // messageElement.classList.add('event-message');
        // message.content = message.sender + ' 님이 참여했습니다.';
        return;
    }
    else if (message.type === 'LEAVE') {
        // messageElement.classList.add('event-message');
        // message.content = message.sender + ' 님이 떠났습니다.';
        return;
    }
    else {
        var avatarElement = document.createElement('i');
        avatarElement.appendChild(document.createTextNode(message.sender[0])); //sender의 맨 첫글자
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        var usernameElement = document.createElement('span');
        usernameElement.appendChild(document.createTextNode(message.sender));

        messageElement.classList.add('chat-message');
        messageElement.classList.add(message.sender === username ? 'my-message' : 'other-message');
        messageElement.appendChild(avatarElement);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    textElement.appendChild(document.createTextNode(message.content));

    messageElement.appendChild(textElement);
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
    insert_chat_log({'sender' : message.sender, 'content' : message.content});
}
function makeMessageElement(message){
    var avatarElement = document.createElement('i');
    avatarElement.appendChild(document.createTextNode(message.sender[0]));
    avatarElement.style['background-color'] = getAvatarColor(message.sender);

    var usernameElement = document.createElement('span');
    usernameElement.appendChild(document.createTextNode(message.sender));

    var textElement = document.createElement('p');
    textElement.appendChild(document.createTextNode(message.content));

    var messageElement = document.createElement('li');
    messageElement.classList.add('chat-message');
    messageElement.classList.add(message.sender === username ? 'my-message' : 'other-message');
    messageElement.appendChild(avatarElement);
    messageElement.appendChild(usernameElement);
    messageElement.appendChild(textElement);
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

function insert_chat_log(paramData){
    var reqData = {
        chat_room_idx : topicName,
        sender : paramData?.sender,
        content : paramData?.content
    };
    fetchInsert(reqData);
}

function fetchInsert(reqData) {
    //fetch start
    //category in ('POST','COMMENT','USER')
    fetch('/Shoots/livechat/makelog',{
        method:'POST',
        headers: {
            'Content-Type' : 'application/json'
        },
        body : JSON.stringify(reqData)
    })
        .then(resp => resp.json())
        .then(data => {
        })
        .catch(error => alert("에러 뜸 : " + error))
    //fetch end
};

async function loadChat() {
    let datas = await get_chat_log({chat_room_idx : topicName}); //datas = chat_log 도메인클래스의 리스트
    console.log('datas : ' + datas);
    datas.forEach(data =>{
        makeMessageElement(data);
    });
}

async function get_chat_log(paramData){
    var reqData = {
        chat_room_idx : paramData?.chat_room_idx
    };
    return await fetchGetChat(reqData);
}

async function fetchGetChat(reqData) {
    // //fetch start
    // //category in ('POST','COMMENT','USER')
    // fetch('/Shoots/livechat/getlog',{
    //     method:'POST',
    //     headers: {
    //         'Content-Type' : 'application/json'
    //     },
    //     body : JSON.stringify(reqData)
    // })
    //     .then(resp => resp.json())
    //     .then(datas => {
    //         return datas;
    //     })
    //     .catch(error => alert("에러 뜸 : " + error))
    // //fetch end

    try {
        const response = await fetch('/Shoots/livechat/getlog', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(reqData)
        });

        if (!response.ok)
            throw new Error('Network response was not ok');

        const datas = await response.json();  // 비동기적으로 데이터 받아오기
        console.log('Received datas from server:', datas);
        return datas;  // 받아온 데이터를 반환
    }
    catch (error) {
        alert("에러 뜸 : " + error);
    }
};


