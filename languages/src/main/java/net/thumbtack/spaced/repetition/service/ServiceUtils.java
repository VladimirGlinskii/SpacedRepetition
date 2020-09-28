package net.thumbtack.spaced.repetition.service;

import net.thumbtack.spaced.repetition.model.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class ServiceUtils {

    public static User getUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
