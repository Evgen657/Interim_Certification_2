package restAssured.enitites;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EmployeeResponse {

    private String city;

    private String name;

    private String position;

    private String surname;

    private int id;

    @JsonCreator
    public EmployeeResponse(@JsonProperty(value = "city", required = true) String city,
                            @JsonProperty(value = "name", required = true) String name,
                            @JsonProperty(value = "position",required = true) String position,
                            @JsonProperty(value = "surname", required = true) String surname,
                            @JsonProperty(value = "id", required = true) int id
    ) {
        this.city = city;
        this.name = name;
        this.position = position;
        this.surname = surname;
        this.id = id;
    }

    public EmployeeResponse() {
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public short getDepartment() {
        return 0;
    }
}