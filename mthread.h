#ifndef THREAD_H
#define THREAD_H

#include <QThread>
#include <WinSock2.h>

class Thread : public QThread
{
    Q_OBJECT
public:
    virtual void run();
    void stop();
    void iSo(SOCKET &sockClient, sockaddr_in roserAddr);
    bool runable = true;
    SOCKET sockClient;
    sockaddr_in roserAddr;
    int nAddrLen = sizeof(roserAddr);

signals:
    void recv(int id, QString msg);
private:

};

#endif // THREAD_H
