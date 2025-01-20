
#include <iostream>
#include <thread>
#include <mutex>
#include "../include/KeyboardThread.h" // Include the header file for KeyboardThread
#include "../include/ResponseThread.h" // Include the header file for ResponseThread

int main(int argc, char *argv[]) {
	std::mutex mutex;
	KeyboardThread keyboardThread(1, mutex); // Create a new KeyboardThread object
	ResponseThread responseThread(2, mutex); // Create a new ResponseThread object
	std::thread keyboard(&KeyboardThread::run, &keyboardThread); // Create a new thread for the KeyboardThread object
	std::thread response(&ResponseThread::run, &responseThread); // Create a new thread for the ResponseThread object
	keyboardThread.run(); // Run the KeyboardThread object



	return 0;
}