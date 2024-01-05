package myschoolapp.com.gsnedutech.Models;

public class CustomTopic {
    String name = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTopicCount() {
        return topicCount;
    }

    public void setTopicCount(int topicCount) {
        this.topicCount = topicCount;
    }

    int topicCount = 0;
}
