package nl.jimkaplan.foxy.web;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ProblemDetail;

@Getter
@Setter
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private ProblemDetail error;

    /**
     * Constructor for successful responses
     * @param data The data to return
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }

    /**
     * Constructor for error responses
     * @param problem The problem detail to return
     */
    public static ApiResponse<?> error(ProblemDetail problem) {
        ApiResponse<?> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setError(problem);
        return response;
    }
}
