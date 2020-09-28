package net.thumbtack.spaced.repetition.exception;

public enum ErrorCode {
    USERNAME_ALREADY_EXISTS("User with this username already exists"),
    EMAIL_ALREADY_EXISTS("User with this email already exists"),
    USER_ID_NOT_EXISTS("User with this id doesn't exist"),
    EXERCISE_NOT_EXISTS("Exercise with this uuid doesn't exist"),
    WORD_NOT_FOUND("Word is not found"),
    WRONG_DICTIONARY_ID("Dictionary with this id doesn't exist"),
    UNKNOWN_SERVER_ERROR("Something went wrong on server"),
    WORD_ALREADY_EXISTS("This word already exists"),
    DICTIONARY_ALREADY_EXISTS("Dictionary with this name already exists"),
    USERNAME_NOT_FOUND("User with this username not found"),
    INVALID_USERNAME_OR_PASSWORD("Invalid username or password"),
    FIELD_NOT_VALID("This field value is not valid"),
    WRONG_PASSWORD("This password is wrong"),
    COULD_NOT_READ_FILE("Could not read uploaded file"),
    FILE_IS_EMPTY("Uploaded file is empty"),
    NO_TRANSLATIONS_PRESENTED("Translation of word is absent"),
    CANT_FIND_PRONUNCIATION("Could not find word pronunciation");

    private String msg;

    ErrorCode(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
