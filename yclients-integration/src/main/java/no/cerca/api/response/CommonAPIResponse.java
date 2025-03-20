package no.cerca.api.response;

/**
 * Created by jadae on 18.03.2025
 */
public class CommonAPIResponse<T> {

    private String status;
    private T data;
    private String message;

    public CommonAPIResponse(String status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
