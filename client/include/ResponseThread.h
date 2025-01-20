
#include <mutex>

class ResponseThread {
private:
    int _id;
    std::mutex &_mutex;
public:
    ResponseThread(int id, std::mutex &mutex);
    void run();
};
