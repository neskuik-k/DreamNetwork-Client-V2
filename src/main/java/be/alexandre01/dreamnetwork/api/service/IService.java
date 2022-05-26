package be.alexandre01.dreamnetwork.api.service;

public interface IService {
    void stop();


    void restart();

    void sendData();

    void kill();

    void removeService();
}
