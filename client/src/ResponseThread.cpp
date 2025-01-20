#include "../include/ResponseThread.h"
#include <iostream>

ResponseThread::ResponseThread(int id, std::mutex &mutex) : _id(id), _mutex(mutex){}

void ResponseThread::run() {
    std::string input;
    while (true) {
        std::getline(std::cin, input);
        if (input == "exit") break;
        _mutex.lock();
        std::cout << "Keyboard input: " << input << std::endl;
        _mutex.unlock();
    }
}