package no.cerca.dtos.exchange;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Created by jadae on 13.03.2025
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseDTO<T> {
    private boolean success;
    private T data;
    private List<Object> meta;

    public ResponseDTO() {
    }

    public ResponseDTO(boolean success, T data, List<Object> meta) {
        this.success = success;
        this.data = data;
        this.meta = meta;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<Object> getMeta() {
        return meta;
    }

    public void setMeta(List<Object> meta) {
        this.meta = meta;
    }
}
