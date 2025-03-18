package no.cerca.api.response;

/**
 * Created by jadae on 18.03.2025
 */
public class CommonAPIResponse<T> {

    private String status;
    private T data;

    public CommonAPIResponse(String status, T data) {
        this.status = status;
        this.data = data;
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
}
