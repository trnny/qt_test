#ifndef CTODO_H
#define CTODO_H

#include <QObject>
#include <WinSock2.h>
#include <WS2tcpip.h>
#include <string>
#include <Windows.h>
#include <mthread.h>

class Funcs : public QObject
{
    Q_OBJECT

public:
    Funcs();
    ~Funcs();

signals:
    void recvMsg(int id, const QString &msg);  // 不要在cpp里定义 在qml里用 on<Signal> 里定义接收的函数

public slots:
    void sendMsg(int to, const QString &msg);
    void setIp(const QString &ip);
    void logout();
    bool login(int id, const QString &password);

private:
    int m_id = 0;  // 已登录的id
    bool m_login = false;  // 是否已登录
    std::string m_ip;  // 服务器ip
    int m_port;   // 服务器port
    SOCKET sockClient;  // 自己
    sockaddr_in serAddr;  // 服务器地址
    sockaddr_in roserAddr;  // 转发服务器
    int nAddrLen = sizeof(roserAddr);
    const char *s_ip = "10.70.2.122";  // 固定IP
    int s_port = 11290;   // 固定端口
    Thread thread;  // 线程
};

#endif // CTODO_H
