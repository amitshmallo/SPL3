#include "../include/KeyboardThread.h"
#include "../include/ResponseThread.h"
#include <iostream>

KeyboardThread::KeyboardThread(int id, std::mutex &mutex) : _id(id), _mutex(mutex){}

void KeyboardThread::run() {
    std::string input;
    ResponseThread responseThread(2, _mutex);
    while (true) {
        std::getline(std::cin, input);
        if (input == "exit") break;
        
    }
}