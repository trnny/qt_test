import QtQuick 2.7
import QtQuick.Window 2.2
import QtMultimedia 5.0
import QtQuick.Controls 1.4
import Union.Funcs 1.0

Window {
    property int u_id : 0 // 选择的会话
    property int m_id : 0 // 自己的id
    id: mainWindow
    visible: true
    width: 640
    height: 480
    minimumHeight: 320
    minimumWidth: 240
    title: qsTr("Chat")

    Rectangle {
        id: afterLogin  // 登录成功界面
        anchors.fill: parent
        visible: false
        clip: true

        Audio {
            id: auPlay
            autoLoad: true
            source: "qrc:///audio/f6m"
        }

        Column {
            id: uList
            y:10
            x:5
            anchors.topMargin: 8
            spacing: 4
            anchors.top: parent.top
            height: parent.height
            move: Transition {
                NumberAnimation {
                    properties:"y";
                    duration: 500
                }
            }
            clip: true
        }

        Rectangle {
            x: 9
            anchors.bottom: parent.bottom
            anchors.bottomMargin: 10
            width: 72
            height: 26
            radius: 4
            color: "#50cc8a"
            Text {
                id: showM_id_text   //显示自己的id
                height: 26
                width: 72
                color: "#fff"
                verticalAlignment: Text.AlignVCenter;
                horizontalAlignment: Text.AlignHCenter;
            }
            MouseArea {
                anchors.fill: parent
                onDoubleClicked: {
                    setTitle("Chat")
                    funcs.logout();
                    cleuc();  // 清空好友列表
                    u_id = 0;
                    m_id = 0;
                    showM_id_text.text = 0;
                    beforeLogin.visible = true;
                    afterLogin.visible = false;
                }
            }
        }

        Rectangle {
            id: showMsgRect
            x: 93
            y: 10
            anchors.top: parent.top
            anchors.topMargin: 8
            height: parent.height - 160
            width: parent.width - 103
            color: "#f3f3f3"
            clip: true
            Column {
                id:showMsg_Col  // 显示消息的容器
                width: parent.width
                anchors.topMargin: 4
                spacing: 6
                add: Transition {  // 添加时
                    NumberAnimation {
                        properties:"y";  // 从上到下
                        duration: 300
                    }
                }
            }

            MouseArea {
                anchors.fill: parent
                onDoubleClicked: {
                    cleml();
                }
                onWheel: {
                    var datl = wheel.angleDelta.y/120;
                    if(datl > 0){  // 页面向下
                        if(showMsg_Col.y < 0) showMsg_Col.y += 40
                    }else{
                        if(showMsg_Col.height+showMsg_Col.y>showMsgRect.height)showMsg_Col.y -= 40
                    }
                }
            }
        }

        Rectangle {
            x: 93
            anchors.top: showMsgRect.bottom
            anchors.topMargin: 8
            color: "#f3f3f3"
            width: parent.width - 103
            height: 134
            radius: 5
            clip: true

            TextEdit {
                id: textEdit   // 消息编辑
                width: parent.width - 10
                height: 124
                x:5
                y:5
                anchors.margins: 5
                wrapMode: TextEdit.Wrap
                clip: true
            }

            Rectangle {
                width: 48
                height: 26
                border.width: 1
                border.color: "#289cff"
                radius: 4
                anchors.right: parent.right
                anchors.bottom: parent.bottom
                anchors.margins: 8
                color: "#289cff"

                Text {
                    text: "发送"
                    verticalAlignment: Text.AlignVCenter;
                    horizontalAlignment: Text.AlignHCenter;
                    anchors.fill: parent
                    anchors.margins: 0
                    color: "#fff"
                    wrapMode: TextEdit.Wrap
                }

                MouseArea {
                    anchors.fill: parent
                    onClicked: {
                        if(textEdit.text && u_id !=0 && m_id !=0)
                        {
                            funcs.sendMsg(u_id, textEdit.text);
                            msgShow_send(u_id, textEdit.text);
                        }
                        if(u_id !=0) textEdit.clear();
                    }
                }
            }
        }
    }

    Rectangle {
        id:beforeLogin  // 登录界面
        anchors.fill: parent
        color: "#f3f6f5"

        Rectangle {
            width: 320
            height: 200
            radius: 10
            color: "#fff"
            anchors.centerIn: parent

            Text {
                text: "账号"
                x:56
                y:40
                width: 60
                height: 24
                font.pixelSize: 14
                verticalAlignment: TextInput.AlignVCenter
            }

            Rectangle {
                x:120
                y:40
                width: 140
                height: 22
                border.color: "#b4b5c6"
                border.width: 1

                TextInput {
                    id: l_idEdit
                    focus: true
                    anchors.fill: parent
                    maximumLength: 10
                    font.letterSpacing: 1
                    font.pixelSize: 16
                    font.bold: true
                    verticalAlignment: TextInput.AlignVCenter
                    anchors.margins: 2
                    validator: IntValidator{bottom: 10000;top:2147483647}
                }
            }

            Text {
                text: "密码"
                x:56
                y:88
                width: 60
                height: 24
                font.pixelSize: 14
                verticalAlignment: TextInput.AlignVCenter
            }

            Rectangle {
                x:120
                y:88
                width: 140
                height: 22
                border.color: "#b4b5c6"
                border.width: 1

                TextInput {
                    id: l_passEdit
                    anchors.fill: parent
                    maximumLength: 18
                    echoMode: TextInput.Password
                    passwordCharacter: "*"
                    font.pixelSize: 14
                    anchors.margins: 2
                    verticalAlignment: TextInput.AlignVCenter
                    Keys.onEnterPressed: {
                        if(l_idEdit.text && l_passEdit.text){
                            login(l_idEdit.text,l_passEdit.text);
                            l_passEdit.clear();
                        }
                    }
                }
            }

            Rectangle {
                width: 56
                height: 28
                border.width: 1
                border.color: "#28f3fc"
                radius: 5
                anchors.right: parent.right
                anchors.bottom: parent.bottom
                anchors.bottomMargin: 30
                anchors.rightMargin: 60
                color: "#28d39f"

                Text {
                    text: "登录"
                    verticalAlignment: Text.AlignVCenter;
                    horizontalAlignment: Text.AlignHCenter;
                    anchors.fill: parent
                    anchors.margins: 0
                    color: "#fff"
                    font.pixelSize: 14
                    font.wordSpacing: 5
                }

                MouseArea {
                    anchors.fill: parent
                    onClicked: {
                        if(l_idEdit.text && l_passEdit.text){
                            login(l_idEdit.text,l_passEdit.text);
                            l_passEdit.clear();
                        }
                    }
                }
            }
        }

        Rectangle {  // 设置ip
            width: 26
            height: 26
            radius: 13
            color: "#f3f5f6"
            anchors {
                right: parent.right
                bottom: parent.bottom
                bottomMargin: 20
                rightMargin: 36
            }

            MouseArea {
                anchors.fill: parent
                hoverEnabled: true
                onEntered: {
                    parent.color = "#2bbacb";
                }
                onExited: {
                    parent.color = "#f3f5f6";
                }
            }
        }
    }

    Funcs {
        id: funcs
        onRecvMsg: {
            if(auPlay.seekable)auPlay.seek(0);
            auPlay.play();
            msgShow_recv(id,msg);
        }
    }

    function addu(uname,uid){  // 添加会话 name,id
        var str =
                ' import QtQuick 2.7;
                    Rectangle {
                        color: "#f8f8f8";
                        width:80;height:32;
                        Text {
                            text: "'+uname+'";
                            anchors.centerIn: parent;
                        }
                        MouseArea {
                            anchors.fill: parent
                            onClicked: {
                                u_id = '+uid+'
                                cleuc();
                                parent.color = "#e7e9d1";
                            }
                        }
                    }';
        Qt.createQmlObject(str, uList, "addOne");
    }

    function cleuc(){  //  还原显色
        var ul = uList.children;
        for(var i =0;i<ul.length;i++){
            ul[i].color = "#f8f8f8";
        }
    }

    function cleul(){  // 清空列表
        var ul = uList.children;
        for(var i =0;i<ul.length;i++){
            ul[i].destory();
        }
    }

    function cleml(){  // 清空消息
        showMsg_Col.y = 0;
        for(var i = 0;i<showMsg_Col.children.length;i++){
            showMsg_Col.children[i].destroy();
        }
    }

    function msgShow_recv(id,msg){
        var str =
               'import QtQuick 2.7;
                Rectangle {
                    x: 6;radius:5;color:"#fafbfd";
                    Text {
                        text: "from '+id+':";
                        color: "#458bf3";
                        font.pixelSize: 13;
                        height: 18
                        verticalAlignment: TextInput.AlignVCenter;
                    }
                    Text {
                        x: 4
                        y: 18
                        wrapMode: TextEdit.Wrap
                    }
                }';
        var r = Qt.createQmlObject(str, showMsg_Col, "msgShow_recv");
        var ch = r.children[1];
        ch.text = msg;
        if(ch.width > showMsg_Col.width - 16) ch.width = showMsg_Col.width - 16;
        r.height = 20 + ch.height;
        r.width = Math.max(r.children[0].width, ch.width)+8;
    }

    function msgShow_send(id,msg){
        var str =
               'import QtQuick 2.7;
                Rectangle {
                    radius:5;color:"#fafbfd";anchors.right: parent.right;anchors.rightMargin:8;
                    Text {
                        anchors.right: parent.right;
                        text: "to '+id+':";
                        color: "#00cc6a";
                        font.pixelSize: 13;
                        height: 18
                        verticalAlignment: TextInput.AlignVCenter;
                    }
                    Text {
                        x:4
                        y: 18
                        wrapMode: TextEdit.Wrap
                    }
                }';
        var r = Qt.createQmlObject(str, showMsg_Col, "msgShow_send");
        var ch = r.children[1];
        ch.text = msg;
        if(ch.width>showMsg_Col.width - 16) ch.width = showMsg_Col.width - 16;
        r.height = 20 + ch.height;
        r.width = Math.max(r.children[0].width, ch.width)+8;
    }

    function setTitle(t){
        title = t;
    }

    function shakeWindow(){  // 晃动窗口

    }

    function login(id,pass){
        if (funcs.login(id,pass)){
            m_id = id;
            showM_id_text.text = id;
            beforeLogin.visible = false;
            afterLogin.visible = true;
            setTitle(id);
            textEdit.focus = true;
        }else{
            showM_id_text.text = 0;
            setTitle("登录失败!");
        }
    }

    Component.onCompleted: {
        addu(1789912638,1789912638);
        addu(1964014004,1964014004);
        addu(428781998,428781998);
        funcs.setIp("10.70.2.122");
    }
}
