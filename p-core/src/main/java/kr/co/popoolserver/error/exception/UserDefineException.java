package kr.co.popoolserver.error.exception;

import kr.co.popoolserver.error.model.ErrorCode;
import lombok.Getter;

@Getter
public class UserDefineException extends RuntimeException{

    private ErrorCode errorCode;
    private String originalMessage;

    public UserDefineException(String message, String originalMessage){
        super(message);
        this.originalMessage = originalMessage;
    }

    public UserDefineException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
