#include "ctodo.h"
#include <QDebug>

#pragma comment(lib,"ws2_32.lib")

Funcs::Funcs(){
    WSADATA wsaData;

    if (WSAStartup(MAKEWORD(2, 2), &wsaData) != 0)
    {
        qDebug() << "sockVersion error!    ____";
    }

    sockClient = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockClient == INVALID_SOCKET)
    {
        qDebug() << "socket error !    _______";
    }

    serAddr.sin_family = AF_INET;
    serAddr.sin_port = htons(s_port);
    serAddr.sin_addr.S_un.S_addr = inet_addr(s_ip);

    struct timeval timeout = {10,0};  // 10s超时
    setsockopt(sockClient, SOL_SOCKET, SO_RCVTIMEO, (char *)&timeout, sizeof(timeout));  // 设置接收超时
    QObject::connect(&thread, SIGNAL(recv(int,QString)), this, SIGNAL(recvMsg(int,QString)));
}

Funcs::~Funcs(){
    thread.runable = false;
    thread.quit();
    logout();
    closesocket(sockClient);
    WSACleanup();
}

void Funcs::sendMsg(int to, const QString &msg)
{
    QString str = QString("to:%1&msg:%2").arg(to).arg(msg);
    //qDebug() << str;
    if(m_login){
        std::string m = str.toStdString();
        sendto(sockClient, m.c_str(), strlen(m.c_str()), 0, (sockaddr *)&roserAddr, nAddrLen);
    }
}

void Funcs::setIp(const QString &ip)
{
    s_ip = ip.toLatin1();
    serAddr.sin_addr.S_un.S_addr = inet_addr(s_ip);
}

bool Funcs::login(int id, const QString &password)
{
    QString str = QString("user:login?id=%1&password=%2").arg(id).arg(password);
    std::string m = str.toStdString();
    sendto(sockClient, m.c_str(), strlen(m.c_str()), 0, (sockaddr *)&serAddr, nAddrLen);
    char recvData[255];
    int ret = recvfrom(sockClient, recvData, 255, 0, (sockaddr *)&roserAddr, &nAddrLen);
    if(ret > 0){
        recvData[ret] = 0x00;
        std::string data = recvData;
        if(data.find("dataService:address?") == 0){
            m_ip = data.substr(data.find("?ip=") + 4, data.find("&port=")-data.find("?ip=")-4 );
            m_port = atoi(data.substr(data.find("&port=") + 6).c_str());
            m_id = id;
            m_login = true;
            roserAddr.sin_family = AF_INET;
            roserAddr.sin_port = htons(m_port);
            roserAddr.sin_addr.S_un.S_addr = inet_addr(m_ip.c_str());
            thread.iSo(sockClient, roserAddr);
            thread.runable = true;
            thread.start();
            return true;
        }
    }else{
        //qDebug() << "timeout! :" << ret;
    }
    return false;
}

void Funcs::logout()
{
    if(m_login){
        thread.runable = false;
        thread.quit();
        QString str = QString("user:logout?id=%1").arg(m_id);
        std::string m = str.toStdString();
        sendto(sockClient, m.c_str(), strlen(m.c_str()), 0, (sockaddr *)&serAddr, nAddrLen);
        m_login = false;
        m_id = 0;
    }
}
