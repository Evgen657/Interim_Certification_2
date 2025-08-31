package restAssured.enitites;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ValidationErrorResponse {

    private String message;

    private String error;

    @JsonProperty(value = "wrong_type_fields")
    private List<String> wrongTypeFields;

    public ValidationErrorResponse(String message, String error, List<String> wrongTypeFields) {
        this.message = message;
        this.error = error;
        this.wrongTypeFields = wrongTypeFields;
    }

    public ValidationErrorResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<String> getWrongTypeFields() {
        return wrongTypeFields;
    }

    public void setWrongTypeFields(List<String> wrongTypeFields) {
        this.wrongTypeFields = wrongTypeFields;
    }
}