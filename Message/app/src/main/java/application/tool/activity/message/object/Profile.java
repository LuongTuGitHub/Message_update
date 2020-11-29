package application.tool.activity.message.object;

public class Profile {
    public String name, day, address;

    public Profile() {
    }

    public Profile(String name, String day, String address) {
        this.name = name;
        this.day = day;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
