package user.service.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import user.constant.AppConstant;
import user.dto.UpdatePassword;
import user.dto.UserRequest;
import user.dto.UserResponse;
import user.dto.UserUpdateRequest;
import user.entity.User;
import user.exception.UserException;
import user.exception.ValidationException;
import user.repository.UserRepository;
import user.service.UserService;
import user.utils.UserRequestValidator;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRequestValidator userRequestValidator;


    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Method use to add new user
     *
     * @param userRequest request for new user
     * @return return User after adding
     */
    @Override
    public UserResponse registerUser(UserRequest userRequest) {
        try {
            UserRequestValidator.validateUserRequest(userRequest);

            userRequestValidator.checkPresent(userRequest);

            User user = convertToUser(userRequest);

            User saverUser = userRepository.save(user);

            return convertToUserResponse(saverUser);
        } catch (ValidationException | EntityExistsException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new UserException(AppConstant.USER_EXCEPTION, exception);
        }
    }

    /**
     * Method is used get user
     *
     * @param username username is passed
     * @return returning user
     */

    @Override
    public UserResponse getUser(String username) {
        try {
            User user = userRequestValidator.verifyUser(username);

            return convertToUserResponse(user);
        } catch (ValidationException | EntityNotFoundException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new UserException(AppConstant.USER_EXCEPTION, exception);
        }
    }

    /**
     * Method Used to change the password
     *
     * @param userName       to find register user
     * @param updatePassword contains old and new password
     * @return success message after updating
     */

    @Override
    public String changePassword(String userName, UpdatePassword updatePassword) {
        try {
            User user = userRequestValidator.verifyUser(userName);

            userRequestValidator.verifyPassword(user, updatePassword.getOldPassword(), updatePassword.getNewPassword());

            user.setPassword(updatePassword.getNewPassword());
            userRepository.save(user);
            return AppConstant.PASSWORD_CHANGE_SUCCESS;

        } catch (ValidationException | EntityNotFoundException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new UserException(AppConstant.USER_EXCEPTION, exception);
        }
    }

    /**
     * Method is used to delete the user
     *
     * @param username used to retrieve user
     * @return message of successfully deleting user
     */

    @Override
    public String deleteUser(String username) {
        try {

            User user = userRequestValidator.verifyUser(username);
            userRepository.delete(user);

            return AppConstant.DELETE_SUCCESS;
        } catch (ValidationException | EntityNotFoundException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new UserException(AppConstant.USER_EXCEPTION, exception);
        }
    }

    /**
     * Method is used to update Request coming from user
     *
     * @param userRequest conatins field for updating
     * @return updated user
     */

    @Override
    public UserResponse updateUser(UserUpdateRequest userRequest, String username) {
        try {
            UserRequestValidator.validateUpdateRequest(userRequest);
            User user = userRequestValidator.verifyUser(username);
            if (userRequest.getUsername() != null) {
                user.setUserName(userRequest.getUsername());
            }
            if (userRequest.getEmail() != null) {
                user.setEmail(userRequest.getEmail());
            }
            if (userRequest.getPhoneNumber() != null) {
                user.setPhoneNumber(userRequest.getPhoneNumber());
            }
            userRepository.save(user);
            return convertToUserResponse(user);
        } catch (ValidationException | EntityExistsException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new UserException(AppConstant.USER_EXCEPTION, exception);
        }
    }


    public User convertToUser(UserRequest userRequest) {
        return User.builder().userName(userRequest.getUsername()).email(userRequest.getEmail()).password(userRequest.getPassword()).phoneNumber(userRequest.getPhoneNumber()).role(userRequest.getRole()).build();
    }

    public UserResponse convertToUserResponse(User user) {

        return UserResponse.builder().id(user.getId()).username(user.getUserName()).email(user.getEmail()).phoneNumber(user.getPhoneNumber()).role(user.getRole()).build();

    }
}
