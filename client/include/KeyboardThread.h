
#include <mutex>

class KeyboardThread {
private:
    int _id;
    std::mutex &_mutex;
public:
    KeyboardThread(int id, std::mutex &mutex);
    void run();
};
