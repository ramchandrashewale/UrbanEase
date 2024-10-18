package user.utils;

import user.dto.UserRequest;
import user.dto.UserUpdateRequest;
import user.entity.User;
import user.exception.ValidationException;
import user.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRequestValidator {

    @Autowired
    private UserRepository userRepository;

    public static void validateUpdateRequest(UserUpdateRequest userRequest) {
        if (userRequest.getUsername() == null || userRequest.getUsername().isEmpty()) {
            throw new ValidationException("Username should not be null or empty");
        }else if(userRequest.getUsername().length()>15) {
            throw new ValidationException("Username should not exceed 15 characters");
        }


        if(userRequest.getEmail() == null || userRequest.getEmail().isEmpty()) {
            throw new ValidationException("Email should not be null or empty");
        }else if (!userRequest.getEmail().matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            throw new ValidationException("Invalid email format");
        }

        if (userRequest.getPhoneNumber() == null || userRequest.getPhoneNumber().isEmpty()) {
            throw new ValidationException("Phone number should not be null or empty");
        } else if (userRequest.getPhoneNumber().length()!=10 || !userRequest.getPhoneNumber().matches("\\d{10}")) {
            throw new ValidationException("Phone number should be  10 digits and it should be in numeric values only");
        }
    }

    public static void validateUserRequest(UserRequest userRequest) {
      UserUpdateRequest userUpdateRequest=  UserUpdateRequest.builder()
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .build();
        validateUpdateRequest(userUpdateRequest);
        validatePassword(userRequest.getPassword());

    }

    public static void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new ValidationException("Password should not be null or empty");
        } else if (password.length() < 8 || password.length() > 16) {
            throw new ValidationException("Password should be between 8 and 16 characters");
        } else if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("Password should contain at least one uppercase letter (A-Z)");
        } else if (!password.matches(".*[0-9].*")) {
            throw new ValidationException("Password should contain at least one digit (0-9)");
        }  else if (!password.matches(".*[!@#$%^&_].*")) {  // Allow only these special characters
            throw new ValidationException("Password should contain at least one special character (!@#$%^&_ etc.)");
        }
    }

    public  void checkPresent(UserRequest userRequest) {
     Optional<User> optionalUser= userRepository.findByUserName(userRequest.getUsername());
        if(optionalUser.isPresent()) {
            throw new EntityExistsException("Username is already taken");
        }

    }
    public User verifyUser(String user){
        if(user ==null || user.isEmpty()) {
            throw new ValidationException("Username should not be null or empty");
        }
        Optional<User> optionalUser= userRepository.findByUserName(user);
        if(!optionalUser.isPresent()) {
            throw new EntityNotFoundException("User with given name is not present");
        }
        return optionalUser.get();
    }
    
    public void verifyPassword(User user,String oldPassword,String newPassword){
        if(!user.getPassword().equals(oldPassword)) {
            throw new ValidationException("Password does not match");
        }
        validatePassword(newPassword);
        
    }
}
