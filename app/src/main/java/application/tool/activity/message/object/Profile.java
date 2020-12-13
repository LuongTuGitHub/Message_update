package application.tool.activity.message.object;

public class Profile {
    private String name,phone,day,address;

    public Profile(String name, String phone, String day, String address) {
        this.name = name;
        this.phone = phone;
        this.day = day;
        this.address = address;
    }

    public Profile() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
