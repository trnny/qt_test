#include "mthread.h"
#include <QDebug>

void Thread::run()
{
    while(runable){
        char recvData[255];
        int ret = recvfrom(sockClient, recvData, 255, 0, (sockaddr *)&roserAddr, &nAddrLen);
        if(ret > 0){
            recvData[ret] = 0x00;
            std::string data = recvData;
            if(data.find("from:")==0){
                int id = atoi(data.substr(5,data.find("&msg:")-5).c_str());
                data = data.substr(data.find("&msg:")+5);
                emit recv(id,QString::fromStdString(data));  // 发送接到消息的信号
            }
        }else{
        }
    }
}

void Thread::iSo(SOCKET &sockClient, sockaddr_in roserAddr){
    this->sockClient = sockClient;
    this->roserAddr = roserAddr;
}
